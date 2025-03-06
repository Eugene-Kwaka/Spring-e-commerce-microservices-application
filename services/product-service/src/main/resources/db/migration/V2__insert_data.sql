-- Use nextval('category_seq') to generate category IDs
INSERT INTO category (id, name, description)
VALUES
    (nextval('category_seq'), 'Electronics', 'Devices, gadgets, and accessories'),
    (nextval('category_seq'), 'Books', 'Fiction, non-fiction, educational'),
    (nextval('category_seq'), 'Clothing', 'Apparel and accessories'),
    (nextval('category_seq'), 'Home & Kitchen', 'Household appliances, kitchenware, etc.'),
    (nextval('category_seq'), 'Beauty & Personal Care', 'Cosmetics and personal hygiene products');

-- Suppose the first call to nextval('category_seq') returns 1,
-- the second returns 51 (because increment is by 50), then 101, 151, 201, etc.
-- You'll need to check what ID each category was assigned to determine how to reference it below.

-- Use nextval('product_seq') to generate product IDs
-- Make sure that category_id matches the actual IDs generated above.
-- For demonstration, let's assume the category IDs got assigned as 1, 51, 101, 151, 201 respectively:
INSERT INTO product (id, name, description, available_quantity, price, category_id)
VALUES
    (nextval('product_seq'), 'Smartphone', '5G smartphone with high-quality camera', 50, 599.99, 1),
    (nextval('product_seq'), 'Laptop', 'Lightweight laptop for everyday use', 20, 999.99, 1),
    (nextval('product_seq'), 'Novel', 'A gripping novel by a famous author', 100, 9.99, 51),
    (nextval('product_seq'), 'T-Shirt', 'Comfortable cotton t-shirt', 200, 15.49, 101),
    (nextval('product_seq'), 'Blender', 'High-speed blender for smoothies', 30, 49.99, 151),
    (nextval('product_seq'), 'Shampoo', 'Organic shampoo for daily hair care', 60, 7.99, 201);
