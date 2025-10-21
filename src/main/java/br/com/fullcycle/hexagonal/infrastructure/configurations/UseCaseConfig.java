package br.com.fullcycle.hexagonal.infrastructure.configurations;

import br.com.fullcycle.hexagonal.application.usecases.*;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class UseCaseConfig {

    private final CustomerService customerService;
    private final PartnerService partnerService;
    private final EventService eventService;

    public UseCaseConfig(CustomerService customerService, PartnerService partnerService,
                         EventService eventService) {
        this.customerService = Objects.requireNonNull(customerService);
        this.partnerService = Objects.requireNonNull(partnerService);
        this.eventService = Objects.requireNonNull(eventService);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase() {
        return new CreateCustomerUseCase(customerService);
    }

    @Bean
    public CreateEventUseCase createEventUseCase() {
        return new CreateEventUseCase(partnerService, eventService);
    }

    @Bean
    public CreatePartnerUseCase createPartnerUseCase() {
        return new CreatePartnerUseCase(partnerService);
    }

    @Bean
    public GetCustomerByIDUseCase getCustomerUseCase() {
        return new GetCustomerByIDUseCase(customerService);
    }

    @Bean
    public GetPartnerByIDUseCase getPartnerUseCase() {
        return new GetPartnerByIDUseCase(partnerService);
    }

    @Bean
    public SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase() {
        return new SubscribeCustomerToEventUseCase(customerService, eventService);
    }
}
