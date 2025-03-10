package com.eugene.notificationservice.kafka.order;

public record CustomerDTO(
        String id,

        String firstName,

        String lastName,

        String email
) {
}
