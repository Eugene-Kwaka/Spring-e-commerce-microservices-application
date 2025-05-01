# ECOMMERCE-SPRING MICROSERVICES

	- Working on a Spring Microservices project.

	- An E-Commerce application with the following entities
		- Customer
		- Address
		- Product
		- Category
		- Order
		- Order Line
		- Payment
		- Notification


## Domain Driven Design

		- Based on the business requirements, we consider factors such as:

			- Scalability
			- Resiliency
			- faster development and deployment of new features and updates
			- Enhanced monitoring and debugging
		
		- Identifying the different domains of the application

			- Customer Domain
				- Customer
				- Address

			- Product Domain
				- Product
				- Category

			- Order Domain
				- Order
				- Order Line

			- Payment Domain
				- Payment

			- Notification Domain
				- Notification



		- To enable a relationship between the Customer and Order domains, we will have a *customerId* in the Order table. However, we will not have an orderReference in the Customer table.

			- This helps minimize coupling between the microservices aand adheres to the Domain Driven Design

		- When we want to get orders from a certain customer, I will query the Order domain and send a HTTP request to get the orders with the customerId as the param.

		- Same applies to the Product domain, if I want to query the orders of a certain product, I will send a HTTP request to the Order domain with the productId as the param.

		- Each service is doing its work without having to maintain a direct relationship or have knowledge of the other's internal workings, This redces dependencies between services, making them easier to scale,           evolve and be deployed independently.


### Architecture Design

		- We'll have 5 Microservices each with its own database that's hosted in Docker

			- Customer

			- Order

			- Product

			- Payment

			- Notification

		- Will be using Kafka as the Message Broker to enable asynchronous communication.

		- All this will be handled in a private network to ensure it is not accessible by the public.

		- Outside the private network, we will have the Config Server using Spring Cloud Config to configure the environment and use Eureka for service registry and discovery.

		- we will also will the API Gateway as a single endpoint for the services. However, I will only allow 3 endpoints to be discovered by users (Customers, Products, Orders).

		- I will use Zipkin for distributed tracing and monitoring of the application.



### Application Flow

		- Once the application starts, all microservices will pull their configuration from the Config server (Spring Cloud Config).

		- Then the microservices will then register themselves with Eureka server for discovery by other services.
	
		- Then the API Gateway will connect to the Eureka server to get all the services registered. We then define the routes to the 3 publicly accessible microservices.

		- User flow when using the app:

			- When a user places an order with the necessary info (customerId, products), the Order service will contact the Customer Service to confirm that the customerId
			   exists. Once successful, the Order service will contact the Product service to confirm the products and that there are enough products for order. if not,
			   order will be blocked and an error message sent. 

			- If order is successful, the order will be saved and an email and message will be sent to the Kafka message broker and notify the user while processing the
			   payment in the Payment service. Once payment is successful, it is saved and an async payment message will be sent to the Kafka broker. 

			- The message broker will receive two messages, a succesful order placement and payment message. These messages will be consumed by the Notification Server, store
			   the info in a database and then send the two emails to the user. 

			- All this process will be monitored by ZipKin using distributed tracing.


-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## MONO REPO Approach - Using Spring Cloud Config Server for centralized configuration management.

	- Start off by creating a mono repo that will carry all the microservices inside it.

	- Create a folder called /services inside the E-commerce Mono Repo project folder.

		> e-commerce-app

			> services


	- We then configure my infrastructure tools which includes the Docker containers. I will start by creating a 'docker-compose.yml' file.
		
		- In this file, I would set up containers for the databases, management tools and email testing capabilities. All running in isolated containers

			- PostgreSQL database container

			- MongoDB database container

			- PgAdmin Database management container

			- Mail-dev email server container

		- To run the docker-compose.yml file:
	
			> docker compose up -d

		- To shutdown the multiple containers
			
			> docker compose down



### Configuration Server
		
	- Now I need to create a project called Configuration Server in Spring Initializer that will hold the configuration service and discovery service functionalities.
		
		- This project will have one dependency - Config Server -> Central management for configuration via Git, SVN or HashiCorp Vault.	

			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-config-server</artifactId>
			</dependency>
		
		- I will generate the Config-Server project and move it inside the services folder in my e-commerce application.

			> e-commerce-app/

				> services/

					> config-server/

		- Since I am using Intellij, I will then go to 'File -> New -> Module from Existing Sources -> Search for the workspace(config-server project's pom.xml file) -> Ok
		  to make the pom.xml file discovered by the e-commerce application.  
	

		- Then move to the Main method in the config-server project and add the @EnableConfigServer annotation.

			- @EnableConfigServer
					- Transforms a Spring Boot application into a centralized configuration server for distributed systems and microservices.
					- Acts as an intermediary between Spring applications and version-controlled config repositories.
					- Creates a HTTP resource-based API for external configuration management.
					- Runs on port 8080 but is commonly configured to use port 8888

			 - config-server/

				> @SpringBootApplication
				  @EnableConfigServer 
				  public class ConfigServerApplication{
				 
				  }
				
		- I need to tell Spring where the locations of our configurations are, I will do it in the 'config-server/application.properties' file or 'application.yml' file.
			
			spring:
				profiles:
					active: native
						application:
							name: config-server
				cloud:
					config:
						server:
							native:
								# The classpath is in the /resources/configurations folder in the config-server project.
								search-locations: classpath:/configurations

				server:
					port: 8888


			- The line 'spring.cloud.config.server.native.search-locations=classpath/configurations':

				- configures where the Spring Cloud Config Server looks for configuration files when running in the 'native' profile mode.

				- The classpath/configurations indicates that the configuration files should be loaded from a directory called 'configurations'. 

				- Typically, this directory is located in 'resources/configurations'. 		

		- I can then test the config-server application to see if it is running well.



### Discovery Service

	- We need to then configure the Discovery-Server. We started with the Config-Server first to ensure microservices access the necessary configuration, and then configure
	  the Discovery-Server for all new microservices created to register and discover each other.

	- For this service, we need to create a new Spring project from Spring Initializer as well, called 'Service_Discovery' with the following dependencies:

		- Config Client

			- allows a spring app to connect to config server to access the config properties.

			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-config</artifactId>
			</dependency>

		- Eureka Server

			- Make the current microservice app as a Service registry.
		
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
			</dependency>


	- I will add the new Service_Discovery app into the ecommerce-app directory just like I did with the Config-Server service.

	- Then in the Service_Discovery app's main class, I will add the '@EnableEurekaServer' annotation to enable it to act as a service register for other microservices.

		- As a service registry, it will maintain a directory of available services and their locations(IP, Port, host)

		> @SpringBootApplication
		  @EnableEurekaServer
		  public class ServiceDiscoveryApplication{} 

	- Then in the Service_Discovery/application.yml file, I will add the following properties

		> spring:
			application:
				name: service_discovery # Specify the name of the Service_Discovery application
	
		  	config:
				import: optional:configserver:http://localhost:8888 # The 'optional' tells us if we cannot access the config-server on port 8888, try to start it again without it so that the app does not fail.


		  # Eureka configuration
		  eureka:
		  	instance:
				hostname: localhost # hostname of the Service-Discovery application.

			client:
				registerWithEureka: false # As a Eureka server, Service_Discovery app should not register with Eureka.

				fetchRegistry: false # As a Eureka registry server, I don't want the Service_Discovery app to fetch itself.

				serviceUrl:
					defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

					# This sets the default Eureka server URL that clients use to register themselves and fetch registry information.
					# The placeholders ${eureka.instance.hostname} and ${server.port} will be replaced with the actual hostname and port values.

		server:
			port: 8761 # Eureka's port number. Once I run the Service_Discovery app, I can access its UI with the URL "http://localhost:8761/eureka/"



### Customer Service

	- We create a Customer Service for my ecommerce project inside the /services/ folder and it will contain the following dependencies:

		- Spring Data MongoDB

		- Eureka Discovery Client

		- Spring Cloud Config Client

		- Validation

		- Spring Web

		- Spring Devtools

		- Lombok


	- Our Customer service needs to be able to read configuration data stored in Config-Server and for this, we include the following configuration details in the Customer service's
	  application.yml file:

		> spring:
			application:
				name: customer-service

			config:
				import: optional:configserver:http://localhost:8888 # The service fetches configuration from the config server that is at the port 8888


	- Then we need to add Eureka configuration to our service so that it is registered and made available to other services. We will do this in the 
	  Config-Server/resources/configurations folder, under an yaml file we'll call 'customer-service.yml' file. This file will also contain the MongoDB properties.

		- You're including the Customer Service configuration file (`customer-service.yml`) in the Config Server's configurations folder for the following reasons:

			- The Config Server acts as a central repository for all configuration data in your microservices architecture. By placing the customer-service.yml file in the Config Server, you centralize the management of the Customer Service's configuration.

			- Config-Server/resources/configurations/customer-service.yml

			> spring:
				data:
					mongodb:
						username: eugene
						password: eugene
						host: localhost
						port: 27017
						database: customer-service
						authentication-database: admin
			server:
				port: 8090

	

	- To test if the Customer Service app is registered in our Eureka server, run the three applications in this order

		Config-Server -> Discovery-Service -> Customer Service



#### Implementing the Customer Service logic:

		- I will first create the document(Entity class) for this service in a package called Customer with a Document class called Customer

			-> customer/Customer

				> @Data
				  @NoArgsConstructor
				  @AllArgsConstructor
				  @Builder
				  @Builder
				  @Validated # Ensures the constraints defined in this class are validated.
				  @Document # MongoDB annotation that specifies that this class is a document.
				  public class Customer{
					@Id
					private String id;
					private String firstName;
					private String lastName;
					private String email;
					private Address address;
				  }

		- I will create the Address Document that is part of the Customer Document as well.

				> @Data
				  @NoArgsConstructor
				  @AllArgsConstructor
				  @Builder
				  @Builder
				  @Validated
				  @Document
				  public class Address{
					private String street;
					private String houseNumber;
					private String zipCode;
				  }

		
##### CustomerDTO Record:
			
			- A record in Java is a special type of class introduced in Java 14 and finalized in Java 16.

			- Records are designed to be a concise way to create immutable data carriers without the boilerplate code typically associated with such classes.

			- They have the following characteristics:

				- They are immutable meaning their values cannot be changed.

				- Automatically create getters, equals(), hashCode(), toString() methods.

				- They only extend the java.lang.Record class and cannot extend other classes.

				- They can implement interfaces.

			- The syntax of a record is: 

				> public record(int x, int y){}

			- For the CustomerRequest record, I will also add validations for the attributes.

				> public record CustomerDTO(
					String id,
				
					@NotNull(message="Provide a first name")
					String firstName,

					@NotNull(message="Provide a last name")
					String lastName,

					@NotNull(message="Provide an email address)
					@Email(message="Provide a valid email address")
					String email,

					Address address
			  	){}




			
##### CustomerRepository interface:

			- After working on the Entity classes and DTO records, I will create a CustomerRepository interface to enable database access and operations.

			> @Repository
			  public interface CustomerRepository extends MongoRepository<Customer, String>{}

			- The repository interface extends the MongoRepository since the CustomerMS works with MongoDB database.

			- We don't have to specify the default MongoRepository methods not unless I want to override them.



			
##### CustomerMapper class:
			
			- I want to create a custom mapper class that will help me convert the DTOs records to an Entity classes. I will call this class CustomerMapper.

			- It will be a @Service component that I will inject in my CustomerService class as a dependency to help me convert customerDTO object to an Entity object.
	
			- It will have a method called toCustomer() which takes in a CustomerDTO parameter and returns a Customer object return type.

				- We will be using the Buider class, which is used to implement the Builder Design Pattern that provides a flexible and readable way to build complex
				  objects.

				- It is mostly useful when:

					- The object to be created has many parameters.

					- Some of the parameters are optional.

					- The object is immutable.


			- I will also add a method toCustomerDTO() which takes a Customer entity object as parameter and converts it to a CustomerDTO record object.

				- I won't be using the Builder class this time as a record is immutable and can't be changed. Just copying the values from the customer entity object to the customerDTO
				  type.



			```java
				  @Service
				  public class CustomerMapper{

					public Customer toCustomer(CustomerDTO customerDTO){

						if(customerDTO == null){
							return null;
						}

						return Customer.builder()
							.id(customerDTO.id())
							.firstName(customerDTO.firstName())
							.lastName(customerDTO.lastName())
							.email(customerDTO.email())
							.address(customerDTO.address())
							.build();

					}


					public CustomerDTO toCustomerDTO(Customer customer){

						if(customer == null){
							return null;
						}

						return CustomerDTO
							.id(customer.getId())
							.firstName(customer.getFirstName())
							.lastName(customer.getLastName())
							.email(customer.getEmail())
							.address(customer.getAddress())

				      }
				  }

		
##### Exception Handling:

			- Before I work on the logic for this service, I want to figure out exception handling.

			- One exception logic would be for the CustomerNotFoundException, when I look for a customer.

			- To do this, I will create an exceptions package in my root folder. 

			- Then create the CustomerNotFoundException class. This class will extend the RunTimeExceptions class.

				- Benefit of extending the RunTimeException class which is an Unchecked exception that does not require handling is:

					- I do not have to declare the 'throws' at every method that would anticipate the exception.

					- I can translate any errors occured, such as not finding a customer into business-friendly messages.
	
					- More cleaner code that reduces boiler plate.

					- I want to control the HTTP Status codes returned.

					- Allows centralized handling via the @ExceptionHandler

			- In the exceptions/CustomerNotFoundException file:


				> public class CustomerNotFoundException extends RunTimeException{

					// Stores a custom message using the msg field.
					private final String msg;

				 }

			- I will also create a CustomerErrorResponse Record that I will use to provide structural responses for all validation errors for my CustomerDTO objects.
			
				- This record will store the responses in HashMap<String, String>.
	
				- They fieldName will be the Key while the errorMessage will be the Value.


					- exceptions/CustomerErrorResponse:

				
						> public record CustomerErrorResponse(

							Map<String, String> errors
						){}



			- I will then create a GlobalExceptionHandler class in the same package folder.

				- This class is responsible for:

					-> central exception handling by providing application-wide exception handling.

					-> Removes the need for try-catch blocks at every controller method.

					-> Handles the CustomerNotFoundException by returning a 404 status code when customer is not found.

					-> Handles the MethodArgumentNotValidException from the @Valid annotation I will be using in my Controller class when an invalid CustomerDTO object
					   is passed as a RequestBody parameter in the controller methods.

					-> Handles the ConstraintViolationException for constraints like @NotNull, @NotBlank, @Email and returns field-specific error messages.


				> @RestControllerAdvice
				  public class GlobalExceptionHandler{
					
					@ExceptionHandler(CustomerNotFoundException.class)
					public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException e){

						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage);

					}


					@ExceptionHandler({MethodArgumentNotValidException.class})
					public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

						// Create a HashMap to store the fieldNames and their corresponding error messages
						var errors = new HashMap<String, String>();

				
						// This statement gets all validation errors from the BindingResult object.		
						e.getBindingResult.getAllErrors()

							// Loop through each error to get the following:
							.forEach(error ->{
								
								// type casts the error to a FieldError and gets the name of the failing field
								var fieldName = ((FieldError) error).getField();

								// Gets the errorMessage associated with the failing field.
								var errorMessage = error.getDefaultMessage();

								// using the put() method, it adds the fieldName and the errormessage to the errors map.
								errors.put(fieldName, errorMessage)

							});
						
						// Returns a ResponseEntity with the HTTP status 400 and the error body showing the fieldName and messages.
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomerResponseError(errors);

					}

					

					@ExceptionHandler(ConstraintViolationException.class})
					public ResponseEntity<CustomerResponseError> handleConstraintViolationException(ConstraintViolationException e){

						var errors = new HashMap<String, String>();

						// Get the constraint violations from the exception and loop through each violation using the forEach() loop
						e.getConstraintViolations().forEach(violation -> 
						
							// getPropertyPath().toString() gets the path to the invalid property and converts it to a String.
							// violation.getMessage() gets the validation error message.
							// errors.put() adds the fieldName and the message to the HashMap.
							errors.put(violation.getPropertyPath().toString(), violation.getMessage())
						)


						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomerResponseError(errors));
					
					}
	

				}




		CustomerService class for the logic.

			- After handling the exceptions that will be thrown in the application, let's configure the business logic.

			- In this class I will have multiple methods:

				-> createCustomer(CustomerDTO customerDTO);

				-> updateCustomer(CustomerDTO customerDTO)

				-> findAll()
		
				-> getCustomerById(String id)

				-> deleteCustomer(String id)


			> @Service
			  public class CustomerService{

				@Autowired
				private CustomerRepository customerRepository;

				@Autowired
				private CustomerMapper customerMapper;

				
				public String createCustomer(CustomerDTO customerDTO){

					Customer customer = customerMapper.toCustomer(customerDTO);

					Customer newCustomer = customerRepository.save(customer);

					return newCustomer.getId();

				}


				public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO){

					Customer customer = customerRepository.findById(id)
								.orElseThrow(() -> new CustomerNotFoundException("Customer with provided ID not found!");

					if(customerDTO.firstName() != null){
						customer.setFirstName(customerDTO.firstName());
					}

					if(customerDTO.lastName() != null){
						customer.setLastName(customerDTO.lastName());
					}

					if(customerDTO.email() != null){
						customer.setEmail(customerDTO.email());
					}

					if(customerDTO.address() != null){
						customer.Address(customerDTO.address());
					}

			
					Customer updatedCustomer = customerRepository.save(customer);

					return customerMapper.toCustomerDTO(updatedCustomer);
					
				}

				
				public List<CustomerDTO> findAllCustomers(){

					List<Customers> customers = customerRepository.findAll();

					return customers.stream().map(customer -> customerMapper.toCustomerDTO(customer)).toList();
				
				}


				public CustomerDTO getCustomerById(String id){

					Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with provided ID not found!"));

					return customMapper.toCustomerDTO(customer);
	
				}



				public void deleteCustomer(String id){
					
					Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with provided ID not found!"));

					customerRepository.deleteById(customer.getId());
				
				}

			  }


		
		CustomerController class:

			- The endpoints for the Customer service:


			> @RestController
			  @RequiredArgsConstructor
			  @Validated
			  @RequestMapping("/api/v1/customers")
			  public class CustomerController{

				@Autowired
				private CustomerService customerService;


				@PostMapping
				public ResponseEntity<String> createCustomer(@Valid CustomerDTO customerDTO){

					return ResponseEntity.ok(customerService.createCustomer(customerDTO));
				}

				
				@PutMapping("/{id}")
				public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("id") String id, @Valid @RequestBody CustomerDTO customerDTO){

					CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);

					return ResponseEntity.ok(updatedCustomer);
				}

		
				@GetMapping
				public ResponseEntity<List<CustomerDTO>> findAllCustomers(){

					return ResponseEntity.ok(customerService.findAll());

				}

				
				@GetMapping("/{id}")
				public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("id") String id){

					return ResponseEntity.ok(customerService.getCustomerById(id));
				
				}


				@DeleteMapping("/{id}")
				public ResponseEntity<CustomerDTO> deleteCustomer(@PathVariable("id") String id){

					customerService.deleteCustomer(id);

					return ResponseEntity.noContent().build()
				}
			

		          }

			
			


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Product Service

	- Dependencies:

		-> Spring Data JPA

		- Eureka Discovery Client

		- Spring Cloud Config Client

		- Validation

		- Spring Web

		- Spring Devtools

		- Lombok
	
		- Flyway Migration -> Performs Database migration in a controlled manner to maintain DB integrity across all instances of the DB. 

				   -> Automates, tracks and manages changes to your DB schema.

				   -> Instead of manually running SQL scripts, it applies versioned migration scripts when the app starts or on demand so that the DB schema is alyways 
				      in sync with my code. 


	- I had connection problems configuring the PostgreSQL DB for this service through the pgAdmin4 and postgres containers. However, I fixed them by delete the postgres volume and
	  rerunning the image and container again. 

		> docker stop ms_pg_sql -> Stopping the postgres container

		> docker rm ms_pg_sql -> Removing the postgres container

		> docker volume rm ms_pg_sql -> removed the volume that was causing the issues.

		> docker-compose up -d -> Restarted my docker images specified in the docker-compose file.
	
	- I also used the postgres' container's IP address (172.19.0.2) and used it as the hostname when configuring the PostgreSQL Server. This is important to note for future use.

		> docker inspect ms_pg_sql -> After rerunning the images, I inspected the newly created postgres container to find the container's IP address. 

 	- I came across issues with the Postgres Database running in Docker and decided to go away with it. So I'm using the normal one in installed in my local machine. 

	- I will create a product-service.yml file in the Config-Server service that will contain the port number and database details regarding the Product-Service microservice.

		-> config-server/resources/configurations/product-service.yml


			> server:
				port: 8050


			  spring:
				datasource:
					driver-class-name: org.postgreSQL.Driver
					url: jdbc:postgresql://localhost:5432/product-service
					username: postgres
					password: root

				jpa:
					hibernate:
						// Validate because FlyWay will create and manage the schema for us.
						// Schema is created by flyway and hibernate only checks if the existing schema  matches my JPA mappings
						ddl-auto: validate
					database: postgresql
					database-platform: org.hibernate.dialect.PostgreSQLDialect
				
				
				// FLYWAY CONFIGURATION
				flyway:
					// telling flyway which database to handle migrations
					url: jdbc:postgresql://localhost:5432/ecommerceProductService

					// baseline an existing database on the first migration run
					baseline-on-migrate: true

					// enables flyway migrations at startup
					enabled: true

					// A description for baseline migration. This text shows up in flyway schema history table so you know which version was used to start the migration
					   in the DB.
					baseline-description: "init"

					// The version to tag an existing schema when executing baseline. If the DB has not been managed by flyway before, it will be marked 0 and then 
					   subsquent ones will be marked v1 v2....
					baseline-version: 0

					// Username and password for flyway to connect to the database
					user: postgres
					password: root



--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Order Service

	- Order Service will communicate to both the Customer and Product servics as well as Payment Service.

	- It also requires asynchronous communication with the Kafka(Message Broker). Kafka will be handled using Docker
	
	- I will use OpenFeign and 



	- Order Service dependencies

		- Spring Web

		- Spring Data JPA

		- Lombok

		- Eureka Discovery Client

		- Spring Cloud Config Client

		- Validation

		- PostgreSQL Driver

		- OpenFeign -> For synchronous communication with the Customer, Product and Payment services


	- When connecting to the other microservices, we need to ask ourselves, are we going fetch the other services' URLs using the Eureka Server or the API Gateway? Why?

		- When scaling the application, let's say a the CustomerMS scales and requires multiple instances, each new instance will have its own port to connect to.

		- In this case, we will need the API Gateway to send Order Service requests to that service. It provides load balancing as well in the instance of scaling.



	- Given our Domain Driven Design, our services will not have foreign keys linking the entities because each service uses its own database and not connected. 

		

	- After creating an order, I will need to send an order confirmation message using the Notification Service and Kafka. For this reason, I will setup Kafka in my project using
	  Docker.



	Setting up Kafka with Docker

		- I will setup Kafka for the asyncronous communication with the Order Service where after an order is made, a message is sent to Kafka which then initializes the 
		  Notification Service to send an email to the user that an order has been placed. 

		- I will setup Kafka with the help of Zookeeper.
	
			-> Zookeeper is used with Kafka to manage and coordinate the Kafka brokers. It helps in maintaining the configuration information, naming, providing distributed sync
		   	   and group services. 


		- In my docker-compose.yml file, I will add the following config for both zookeeper and kafka containers. 

			> #Zookeeper Service
  		   	  zookeeper:

				# Specifies the Docker image to use for Zookeeper, provided by Confluent.
    				image: confluenctinc/cp-zookeeper:latest

    				container_name: zookeeper

    				environment:

      					# This config is only required when working with this container in dev mode. When deploying, more configuration is required.
					# Uniquely identifies this Zookeeper server in a cluster. When you run multiple Zookeeper instances, each one should have a unique ID. 
      					ZOOKEEPER_SERVER_ID: 1

					# The main port on which Zookeeper listens for client connections—by default, port 2181
      					ZOOKEEPER_CLIENT_PORT: 2181

					# Defines the length of a Zookeeper “tick” in milliseconds. This “tick” is the basic time unit used by Zookeeper for heartbeats and timeouts.
      					ZOOKEEPER_TICK_TIME: 2000

				# Maps port 22181 on the host machine to port 2181 in the container. This lets you connect to Zookeeper on localhost:22181 from your host.
    				ports:
      					- 22181:2181

				# Connects this container to the custom Docker network microservices-net, ensuring that other services on the same network (like Kafka) can communicate with
			  	  Zookeeper by its container name (zookeeper).
    				networks:
      					- microservices-net

  
			kafka:
    				image: confluentinc/cp-kafka:latest

    				container_name: ms_kafka

				# Exposes Kafka on port 9092 both internally and externally. You can connect to Kafka at localhost:9092 on your host machine.
    				ports:
      					- 9092:9092

				# Instructs Docker Compose to start the zookeeper service first. Kafka relies on Zookeeper, so you must ensure Zookeeper is running before Kafka starts.
    				depends_on:
      					- zookeeper

    				environment:
      					# How many replications do I want.
					# The offsets topic is a special internal Kafka topic that stores consumer group offsets. 
					# REPLICATION_FACTOR is how many copies of this data you want stored across different brokers. Since you’re running a single broker, 1 is sufficient.
      					KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

					# Transaction_state_log is used to track transactions for exactly-once semantics. In a single-broker setup, this also needs to be 1
      					KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1

					# Minimum in-sync replicas for the transaction state log. With only one broker, it’s set to 1 to allow Kafka to work in a single-node setup.
      					KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

					# Specifies how Kafka connects to Zookeeper. Since both containers share the microservices-net network, zookeeper resolves to the Zookeeper
					 container’s hostname, and 2181 is the Zookeeper client port.
      					KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

					# Maps named listeners to security protocols. Here, both PLAINTEXT and PLAINTEXT_HOST refer to unencrypted connections. 
					# This is standard for local development or testing.
      					KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
	
					# Tells Kafka to advertise localhost:9092 to clients as the connection address. 
					 This is crucial if clients outside the Docker network (e.g., your host machine) need to connect to Kafka on localhost:9092.
      					KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092

    				networks:
      					- microservices-net



		- To run my services in docker-compose file, I will write the command:

			> docker compose up -d


	
	Kafka config in Order service:

		- First I need to add kafka dependency to my order-service as well. 
		
			> <dependency>
				<groupId>org.springframework.kafka</groupId>
				<artifactId>spring-kafka</artifactId>
			 </dependency>


		- I will create a KafkaOrderTopicConfig file in a config package folder in my order-service for the Kafka configutation.

		- A Topic is a channel which produces and sends messages from which a consumer read messages.
			
			- Topics are part of Kafka's publish-subscirbe messaging model.

			- They have the following fundamental concepts:
			
				-> Topics organize and store messages. Each message in a topic is assigned a unique offset, which is a sequential ID that identifies the message within 
				   the topic.

				-> Topics can be divided into partitions, which allows for parallel processing and scalability, with each partition being an ordered, immutable sequence of 				   messages that is continually appended to.

				-> Topics can be replicated across multiple Kafka brokers to ensure fault tolerance and high availability. 
				   Each partition can have multiple replicas, one of which is the leader that handles all reads and writes, while the others are followers.

				-> Kafka topics can be configured with retention policies that determine how long messages are stored. 
				   Messages can be retained for a specified period or until a certain size limit is reached.

				-> Consumers subscribe to topics to read messages. They can read from the beginning of the topic or from a specific offset.

		
			-> order-service/config/KafkaOrderTopicConfig:

				> @Configuration
				  public class KafkaOrderTopicConfig {

    					/**
     					* NewTopic is a class in Apache Kafka used to define a new topic configuration.
     					* In the context of your order-service application, it is used to create a new Kafka topic named "order-topic".
     					* This topic will be used to publish and consume messages related to orders.*/
    					@Bean
    					public NewTopic orderTopic(){
    	    					//  TopicBuilder.name("order-topic").build() creates a new NewTopic instance with the name "order-topic".
        					return TopicBuilder
                					.name("order-topic")
                					.build();
    					}
				  }


		- Then I create a Kafka package for the OrderProducer class. 

			- Order service is the producer that sends the message through kafka to the Notication service to generate an email to the user once an order is done. 
	
			- The OrderProducer class will contain a sendOrderConfirmation() method that takes in a OrderConfirmationDTO object containing all the details of the order done.

				-> OrderConfirmation Record:

				> /**
 				  * This class will contain all the order information that will be sent to the Kafka broker
				  */
				  public record OrderConfirmationDTO(

        				String orderReference,

        				BigDecimal totalAmount,

        				PaymentMethod paymentMethod,

        				// Details of the customer making the order.
        				CustomerDTO customer,

        				// List of all the products the customer has purchased.
        				List<ProductPurchaseResponseDTO> productPurchasesDTO


				 ) {}


		- After producing and consuming something with Kafka, we need to define serializers(Key serializer and Value serializer). Since we are sending an OrderConfirmation object, 
		  we need to serialize the object so it can be consumed and also to deserialize it back to an object.

			- This will be done in the Config server by adding a few configurations. 

				-> config-server



------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Payment Service

	- Create a Payment service with the following configurations:

		-> Spring Data JPA
		
		-> Postgres

		-> Lombok

		-> Eureka Discovery Client

		-. Spring for Apache Kafka

		-> Config Client

		-> Spring web


	- The service will have two entities 

		-> Payment 

			> @Data
			  @Builder
			  @AllArgsConstructor
			  @NoArgsConstructor
			  @Entity
			  @EntityListeners(AuditingEntityListener.class)
			  @Table(name="payment")
			  public class Payment {
		
    				@Id
    				@GeneratedValue(strategy = GenerationType.IDENTITY)
    				private Integer id;

    				private BigDecimal price;

    				@Enumerated(STRING)
    				private PaymentMethod paymentMethod;

    				private Integer orderId;

    				@CreatedDate
    				@Column(updatable=false, nullable=false)
    				private LocalDateTime createdAt;


    				@LastModifiedDate
    				@Column(updatable=false, nullable=false)
    				private LocalDateTime lastModifiedAt;

			 }
			

		-> PaymentMethod enum 

			- Enums are not stored in the database as entities. Instead, they are typically stored as a specific column type in a table.
			
			- As defined in the Payment entity, PaymentMethod enum is stored in the database as a string due to the @Enumerated(STRING) annotation. 
			  This means that the enum values will be stored as their corresponding string representations in the database.

			> public enum PaymentMethod {

    				PAYPAL,

    				CREDIT_CARD,

    				VISA,

    				MASTER_CARD,

    				BITCOIN
			  }
					
					



-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Notification Service

	- Has the following dependencies.

		-> Java Mail Sender

		-> ThymeLeaf

		-> Spring Data MongoDB

		-> Lombok

		-> Eureka Discovery Client

		-> Spring Cloud Config Client

		-> Spring for Apache Kafka



	- I will create a NotificationConsumer class inside the kafka/ directory.
		
		- This class will have two methods
	
			-> consumeOrderNotification

			-> consumePaymentNotification
	
		- However, I need to create the persist the data first then send the emails.



	- For the Email Service, I will use the JavaMailSender and SpringTemplateEngine from Thymeleaf.

		- I will make my method asynchronous because when we receive a notification,we do not want to block the whole process until the email is sent.

		- To do this I will the @EnableAsync annotation to the Notification service's main class:

			> @SpringBootApplication
			  @EnableAsync
			  public class NotificationServiceApplication {

				public static void main(String[] args) {
					SpringApplication.run(NotificationServiceApplication.class, args);
				}

			  }


		- And add the @Async annotation to my method in the EmailService class.

			> 



---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

DISTRIBUTED TRACING with Zipkin

	- Distributed tracing is a technique used to track and visualize requests as they flow through different microservices in a distributed system. 

	- It helps trace a single user request as it passes through multiple services, helping debug performance issues or failures.

	- How distributed tracing works:

		-> Each request gets a trace ID and span ID.

		-> As the request moves through different microservices, the trace ID remains the same, but new span IDs are created to track individual operations.

		-> The trace data is collected and sent to monitoring tools like Zipkin, Jaeger, or Grafana for visualization and analysis.

	- In our case, distributed tracing will help our microservices application by:

		-> Better Debugging & Root Cause Analysis 
			- If an order fails, you can trace where it broke (Payment, Order, or Notification Service).
			- You can identify slow database queries, network latencies, or failed API calls.

		-> Performance Monitoring
			- Helps in finding slow services that are affecting response times (e.g., slow checkout due to Payment Service delay).

		-> Error Tracking
			- If an API call to a third-party payment gateway fails, distributed tracing shows where and why the error happened.

		-> Microservices Dependency Mapping
 			- Provides a visual representation of how services interact, helping in troubleshooting and optimizing the system.


	- I will be using Zipkin for tracing, I will download the image and setup in Docker. I will have to update the project's docker-compose.yml file to include Zipkin container config.

		>   zipkin:
    			container_name: ms_zipkin
    			image: openzipkin/zipkin
    			ports:
      				- 9411:9411
    			networks:
      				- microservices-net
    			restart: unless-stopped

	
	- To use Zipkin in our services, I have to first add the dependency to every service the requests will be coming from and to:
			- It will add two more dependencies from the actuator and Micrometer.
			- Actuator provides additional endpoints for tracing such as /actuator/health and /actuator/metrics
			- While micrometer p
		
		> <dependency>
           		 <groupId>io.zipkin.reporter2</groupId>
            		<artifactId>zipkin-reporter-brave</artifactId>
        	  </dependency>

        	  <dependency>
            		<groupId>org.springframework.boot</groupId>
            		<artifactId>spring-boot-starter-actuator</artifactId>
        	  </dependency>

        	  <dependency>
            		<groupId>io.micrometer</groupId>
            		<artifactId>micrometer-tracing-bridge-brave</artifactId>
        	  </dependency>

	- Then configure Zipkin properties in the config-server/resources/configurations/application.yml file so that every microservice has access to Zipkin.

		-> config-server/resources/configurations/application.yml

			management:
  				tracing:
    					sampling:
      						# Probability of 1.0 means 100% of the requests will be traced.
      						probability: 1.0

		


	- To access Zipkin's dashboard in my browser:

		> http://localhost:9411/


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Securing our E-commerce application with Spring Security KeyCloak

	- Spring Security Keycloak is an integration between Spring Security and Keycloak (an open-source identity and access management solution).

	- The security is configured at the gateway level to validate each request coming to the application.

	- For example, each service has endpoints that may require specific permissions and roles to access, so we can configure that using Spring Security and Keycloak. 
		
	- First we add Keycloak into our project through Docker in docker-compose.yml file with the following config:

		>   keycloak:
    			container_name: keycloak-ms
    			image: quay.io/keycloak/keycloak:24.0.2
    			ports:
      				- 9098:8080
    			environment:
      				KEYCLOAK_ADMIN: admin
      				KEYCLOAK_ADMIN_PASSWORD: admin
    			networks:
      				- microservices-net
    			command: 
      				- "start-dev"
    			restart: unless-stopped



	- I will then add the Oauth2-resource-server dependency at the API Gateway's pom.xml file as I would like to configure the security at that point hitting all endpoints. 

		> <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
		 </dependency>

	- Then I will add the keycloak configuration in my API Gateway service's application.yml file:

		>   security:

    			# Indicates you're configuring OAuth2 authentication, which is an industry-standard protocol for authorization.
    			oauth2:

      				# Specifies that you're configuring a resource server, which is a server that hosts protected resources and requires valid tokens to access those resources.
      				resourceserver:

        				# Specifies that you're configuring JWT (JSON Web Token) authentication, which is a compact and self-contained way of representing claims to be transferred between two parties.
        				jwt:
          					# Specifies the URI of the issuer of the JWT tokens.
          					# The issuer is the entity that issued the token and is typically a trusted authority.
          					# The uri points to the Keycloak server that issues the tokens.
          					# 9098 is the port of the Keycloak server.
          					# realms/micro-services is the path to the realm in Keycloak that issues the tokens.
          					issuer-uri: "http://localhost:9098/realms/ecommerce-microservices"


	
	API Gateway 

		- I will create a SecurityConfig clsss that has the configuration for Keycloak. Given that the whole application is a microservice, the API Gateway is a reactive web
	  	  server with WebFlux, with the spring-cloud-starter-gateway dependency being reactive hence the security configuration would be different. We will use the @EnableWebFluxSecurity

		> @Configuration
		  @EnableWebFluxSecurity // Enables Spring Security for web applications using the WebFlux framework.
		  public class SecurityConfig {

    			@Bean
    			public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        			serverHttpSecurity
                			.csrf(ServerHttpSecurity.CsrfSpec::disable) // Disables Cross-Site Request Forgery (CSRF) protection.

                			.authorizeExchange(exchange -> exchange // Begins the configuration for authorization rules for different paths.

                        			.pathMatchers("(/eureka/**)") // All paths with from the "/eureka/**" path are allowed without authentication.
                        			.permitAll()

                        			.anyExchange() // All other paths require authentication.
                        			.authenticated()
                			)

                			// Configures the application as an OAuth2 resource server that validates JWT tokens.
                			// It uses default settings which connect to the Keycloak server specified in the application.yml file.
                			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

       				// Builds and returns the configured security filter chain.
        			return serverHttpSecurity.build();
    			}
		  }


			- This configuration ensures that:

				- Eureka service discovery endpoints remain accessible

				- All other API endpoints require authentication

				- JWT tokens from Keycloak are used for authentication

				- The API Gateway acts as a secure entry point to your microservices


		- Once I configure the Keycloak config properties, I can access the dashboard through this url in the browser.

			> http://localhost:9098
		

			- Inside the dashboard, I will create a new realm in the top left drop-down at the screen and choose 'Create Realm' with the same anme as specified in my API Gateway's application.yml file.

				> ecommerce-microservices

			- Then I will create a client in the Clients tab by clicking 'Create Client 'and provide the same name as the realm.

				> Clinent ID -> ecommerce-microservices
				> Name -> ecommerce-microservices
				> Description -> ecommerce-microservices


			- I enable Client authentication and authorization. 

			- Then copy the Client secret in the your new realms's Client dashboard.

			- Restart the services and try accessing the endpoints using spring security keycloak.

				- This will lead to a 401 Unauthorized Access Status Code



	Postman

		- When I run my requests, I will get a 401 Unauthorized Status code, meaning my requests now require authentication. 

		- In Postman, I will go to the Authorization tab -> Oauth 2.0 -> Configure New Token -> Grant type -> Client Credentials

			- Here I will write ddown the client secret from my Keycloak dashboard and the Client ID which is the client name. 

		- We need the Access Point URL which is gotten from  KeyCloak
			
			- Go back to the Keycloak dashboard, choose the Realm settings from the left tab option, scroll down to the bottom and click on the OpenID EndpointConfiguration link.
			
			- The link will take you to a new tab, click on pretty print and copy the Token Endpoint.

			```
				> token endpoint

			```

			- Paste this token endpoint in the "Access Token URL" line in Postman. 
		
		- Write down the client name in the "Client ID" line in Postman
			
		- Copy the Client secret from the Keycloak Client dashboard and paste in the "Client Secret" line in Postman. 
		
		- Then click on the "Get New Access Token" button to get the access token. 

		- After creating the new access token, I can click on the "Use Token" button and now I can make requests to my services using spring security with Keycloak.

		- Postman will save my access token until it expires, however for each request I send, I have to ensure the Authorization tab shows Oauth 2.0. and if the token is expired,
		  I have to recreate the token by clicking on the Get New Access Token button. 


--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## DOCKERIZING THE MICROSERVICES

	- To Dockerize the application:

		- I added the Postgres and PgAdmin configs in the docker-compose.yml file. These are different from the other Postgres and PgAdmin containers from mydbnetwork that carry all my databases.

			services:
				# Container for postgresqlDB service
				postgres:
					container_name: ecommerce_pg_sql
					image: postgres
					environment:
						POSTGRES_USER: postgres
						POSTGRES_PASSWORD: root
						PGDATA: /var/lib/postgresql/data
					# Persistent data storage with the postgres volume
					volumes:
						- ecommerce_pg_sql:/var/lib/postgresql/data
					ports:
						# Exposing container ports "External Port: Container Port"
						- 5433:5432
					# Custom network
					networks:
						- ecommercemicroservices-net
					# If the container is stopped, I want it to automatically restart
					restart: unless-stopped

				# This container is for the pgAdmin interface microservice - For users who do  not have access to the Intellij Ultimate version
				pgadmin:
					container_name: ecommerce_pgadmin
					image: dpage/pgadmin4
					environment:
						PGADMIN_DEFAULT_EMAIL: ${PGADMIN_DEFAULT_EMAIL:-pgadmin@pgadmin.org}
						PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_DEFAULT_PASSWORD:-admin}
						PGADMIN_CONFIG_SERVER_MODE: 'False'
					volumes:
						- ecommerce_pgadmin:/var/lib/pgadmin
					ports:
						- 5200:80
					networks:
						- ecommercemicroservices-net
					restart: unless-stopped
	
		- I want to use Spring profiles, where I use a Docker profile for the app. When I run the application in Docker, its configuration and dependencies wpuld be accessed from the Docker profile.

			- I achieved this by creating a docker.yml file for each configuration file. For example, the 'api-gateway.yml' file has its own 'api-gateway-docker.yml' file, with Docker specific properties where necessary.

			- In the ConfigServer app, I created an 'application-docker.yml' file that specifies the location that the config server would search for the docker configurations folder for all the Dockerized services.

				> config-server/src/main/resources/application-docker.yml

				spring:
					profiles:
						active: native
					application:
						name:
							config-server
					cloud:
						config: # config settings for Spring Cloud Config
							server: # Configures the Config Server itself
								native: # Specifies that the Config Server should use the local filesystem (or classpath) to load configuration files.
									# The classpath is in the /resources/docker_configurations folder in the config-server project to allow the Docker containers to access the configurations.
									search-locations: classpath:/docker_configurations

				server:
					port: 8888
			
			- I will also create a application-docker.yml file for all the application.yml files in each individual service. Replace any 'localhost' with the name of the docker service as specified in the docker-compose.yml file.

			- For example, the api-gateway's application-docker.yml would look like;

				> api-gateway/src/main/resources/application-docker.yml

				spring:
					application:
						name: api-gateway
					config:
						import: optional:configserver:http://config-server:8888
					security:
						# Indicates you're configuring OAuth2 authentication, which is an industry-standard protocol for authorization.
						oauth2:
							# Specifies that you're configuring a resource server, which is a server that hosts protected resources and requires valid tokens to access those resources.
							resourceserver:
								# Specifies that you're configuring JWT (JSON Web Token) authentication, which is a compact and self-contained way of representing claims to be transferred between two parties.
								jwt:
									# Specifies the URI of the issuer of the JWT tokens.
									# The issuer is the entity that issued the token and is typically a trusted authority.
									# The uri points to the Keycloak server that issues the tokens.
									# 9098 is the port of the Keycloak server.
									# realms/microservices is the path to the realm in Keycloak that issues the tokens.
									issuer-uri: "http://keycloak:9098/realms/ecommerce-microservices"

#### Building Images of my services

	- Firstly before running the docker-compose.yml file, I need to create images of all my services, then write them in the docker-compose.yml file so that it creates running containers for all the services defined in it.

	- We will be using Maven's Buildpacks to create these images, which is simpler and easier than handling dockerfiles.

	- To use create the images, first we need to make all Maven wrapper scripts (mvnw) executable in your project. The executable scripts will enable us to run the maven commands necessary to create the images.

		- To this write the command

			> find . -name mvnw -exec chmod +x {} \;

		- I faced an error in my terminal that said that maven was not found. I had to install maven in my machine then configure the maven:wrapper to work.

			> sudo apt install maven

			> mvn wrapper:wrapper


		- I found out that each service requires me to install the mvn wrapper before I could execute the mvnw command to create an image. So I decided to create a script that would install the maven files for all services. The file is called 'setup-maven-wrappers.sh'

		#!/bin/bash

		# List of service directories
		services=(
			"services/config-server"
			"services/discovery-service"
			"services/api-gateway"
			"services/customer-service"
			"services/product-service"
			"services/order-service"
			"services/payment-service"
			"services/notification-service"
		)

		# Setup Maven wrapper for each service
		for service in "${services[@]}"; do
			echo "Setting up Maven wrapper for $service"
			cd "$service" || continue

			# Create directory structure
			mkdir -p .mvn/wrapper

			# Create properties file
			echo "distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.6/apache-maven-3.8.6-bin.zip" > .mvn/wrapper/maven-wrapper.properties

			# Download wrapper JAR and scripts
			curl -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.1/maven-wrapper-3.1.1.jar
			curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw
			curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd

			# Make wrapper executable
			chmod +x mvnw

			cd - || exit
		done

		echo "Maven wrappers set up for all services"


		- To run the script, navigate to the project's root directory then make the wrapper executable and run it

			> chmod +x setup-maven-wrappers.sh

			> ./setup-maven-wrappers.sh


	- To successfully build images of each service, navigate to each of the services' directory and write the following command:

			> cd services/api-gateway

			> ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=eugenekwaka/ecommerce_api-gateway:v1

	- Then I will push my images to Dockerhub, which is a public repository for all images created by myself and others.

		- I would first need to login to Dockerhub:

			> docker login # Enter my Dockerhub credentials

		- Then to push the images, I run

			> docker push eugenekwaka/ecommerce_config-server:v1


	- After pushing my images to dockerhub, I will update my docker-compose.yml specifying the additional services I want built to images. This is the final version of the docker-compose.yml file:

		services:
			# Container for postgresqlDB service
			postgres:
				container_name: ecommerce_pg_sql
				image: postgres
				environment:
					POSTGRES_USER: postgres
					POSTGRES_PASSWORD: root
					PGDATA: /var/lib/postgresql/data
				# Persistent data storage with the postgres volume
				volumes:
					- ecommerce_pg_sql:/var/lib/postgresql/data
				ports:
					# Exposing container ports "External Port: Container Port"
					- 5433:5432
				# Custom network
				networks:
					- ecommercemicroservices-net
				# If the container is stopped, I want it to automatically restart
				restart: unless-stopped

			# This container is for the pgAdmin interface microservice - For users who do  not have access to the Intellij Ultimate version
			pgadmin:
				container_name: ecommerce_pgadmin
				image: dpage/pgadmin4
				environment:
					PGADMIN_DEFAULT_EMAIL: ${PGADMIN_DEFAULT_EMAIL:-pgadmin@pgadmin.org}
					PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_DEFAULT_PASSWORD:-admin}
					PGADMIN_CONFIG_SERVER_MODE: 'False'
				volumes:
					- ecommerce_pgadmin:/var/lib/pgadmin
				ports:
					- 5200:80
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Zipkin service container for distributed tracing.
			zipkin:
				container_name: ecommerce_zipkin
				image: openzipkin/zipkin
				ports:
					- 9411:9411
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# KAFKA CONFIG USING ZOOKEEPER
			zookeeper:
				image: confluentinc/cp-zookeeper:latest
				container_name: ecommerce_zookeeper
				environment:
					# This config is only required when working with this container in dev mode. When deploying, more configuration is required.
					ZOOKEEPER_SERVER_ID: 1
					ZOOKEEPER_CLIENT_PORT: 2181
					ZOOKEEPER_TICK_TIME: 2000
				ports:
					- 22181:2181
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			kafka:
				image: confluentinc/cp-kafka:latest
				container_name: ecommerce_kafka
				ports:
					- 9092:9092
				depends_on:
					- zookeeper
				environment:
					# How many replications do I want
					KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
					KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
					KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
					KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
					KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
					KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# MongoDB service container
			mongodb:
				container_name: ecommerce_mongodb
				image: mongo
				ports:
					- 27017:27017
				volumes:
					- ecommerce_mongodb:/data
				environment:
					- MONGO_INITDB_ROOT_USERNAME=eugene
					- MONGO_INITDB_ROOT_PASSWORD=eugene
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# MongoExpress service container
			mongo-express:
				container_name: ecommerce_mongo_express
				image: mongo-express
				ports:
					- 8081:8081
				environment:
					- ME_CONFIG_MONGODB_ADMINUSERNAME=eugene
					- ME_CONFIG_MONGODB_ADMINPASSWORD=eugene
					- ME_CONFIG_MONGODB_SERVER=mongodb
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# mail-dev service container
			mail-dev:
				container_name: ecommerce_mail_dev
				image: maildev/maildev
				ports:
					# Web interface on port 1025
					- 1080:1080
					# SMTP server on port 1025
					- 1025:1025
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			keycloak:
				container_name: ecommerce_keycloak
				image: quay.io/keycloak/keycloak:24.0.2
				ports:
					- 9098:8080
				environment:
					KEYCLOAK_ADMIN: admin
					KEYCLOAK_ADMIN_PASSWORD: admin
				volumes:
					- ecommerce_keycloak:/opt/keycloak/data # Mount the volume to the Keycloak data directory
				networks:
					- ecommercemicroservices-net
				command:
					- "start-dev"
				restart: unless-stopped

			# Config Server service container
			config-server:
				container_name: ecommerce_config_server
				image: eugenekwaka/ecommerce_config-server:v1
				environment:
					SPRING_PROFILES_ACTIVE: native
				ports:
					- 8888:8888
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Discovery Server service container
			discovery-service:
				container_name: ecommerce_discovery-service
				image: eugenekwaka/ecommerce_discovery-service:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
				depends_on:
					- config-server
				ports:
					- 8761:8761
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# API Gateway service container
			api-gateway:
				container_name: ecommerce_api-gateway
				image: eugenekwaka/ecommerce_api-gateway:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
					SPRING_CLOUD_CONFIG_URI: http://config-server:8888
				depends_on:
					- config-server
					- discovery-service
				ports:
					- 8222:8222
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Customer Service service container
			customer-service:
				container_name: ecommerce_customer-service
				image: eugenekwaka/ecommerce_customer-service:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
					SPRING_CLOUD_CONFIG_URI: http://config-server:8888
				depends_on:
					- config-server
					- discovery-service
					- mongodb
				ports:
					- 8090:8090
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Product Service service container
			product-service:
				container_name: ecommerce_product-service
				image: eugenekwaka/ecommerce_product-service:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
					SPRING_CLOUD_CONFIG_URI: http://config-server:8888
				depends_on:
					- config-server
					- discovery-service
					- postgres
				ports:
					- 8050:8050
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Order Service service container
			order-service:
				container_name: ecommerce_order-service
				image: eugenekwaka/ecommerce_order-service:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
					SPRING_CLOUD_CONFIG_URI: http://config-server:8888
				depends_on:
					- config-server
					- discovery-service
					- kafka
					- postgres
				ports:
					- 8060:8060
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Payment Service service container
			payment-service:
				container_name: ecommerce_payment-service
				image: eugenekwaka/ecommerce_payment-service:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
					SPRING_CLOUD_CONFIG_URI: http://config-server:8888
				depends_on:
					- config-server
					- discovery-service
					- kafka
					- postgres
				ports:
					- 8070:8070
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Notification Service service container
			notification-service:
				container_name: ecommerce_notification-service
				image: eugenekwaka/ecommerce_notification-service:v1
				environment:
					SPRING_PROFILES_ACTIVE: docker
					SPRING_CLOUD_CONFIG_URI: http://config-server:8888
				depends_on:
					- config-server
					- discovery-service
					- kafka
					- mongodb
					- mail-dev
				ports:
					- 8080:8080
				networks:
					- ecommercemicroservices-net
				restart: unless-stopped

			# Creates a bridge network named "microservices-net" for container communication
				networks:
						ecommercemicroservices-net:
						driver: bridge

			# Three persistent volumes are defined:
			volumes:
				ecommerce_pg_sql:
				ecommerce_pgadmin:
				ecommerce_mongodb:
				ecommerce_keycloak:



	- Since I already used some of the containers when running my project locally (e.g., mongodb, mongo-express, zipkin, kafka, zookeeper, mail-dev, and keycloak), I would stop the containers and then use the --build flag to only rebuild the images for the services while starting containers for the services that are not yet created. Existing containers won't be removed.

		> docker compose stop  # stops all running containers defined in the docker-compose.yml file

		> docker compose up --rebuild  #rebuild the images for services defined in your docker-compose.yml file and start any containers that are not already running.







		



