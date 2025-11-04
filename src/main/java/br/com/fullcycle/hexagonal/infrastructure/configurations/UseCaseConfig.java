package br.com.fullcycle.hexagonal.infrastructure.configurations;

import br.com.fullcycle.hexagonal.application.usecases.customer.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.customer.GetCustomerByIDUseCase;
import br.com.fullcycle.hexagonal.application.usecases.event.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.partner.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.partner.GetPartnerByIDUseCase;
import br.com.fullcycle.hexagonal.application.usecases.event.SubscribeCustomerToEventUseCase;
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
        // TODO: fix dependency
        return new CreateCustomerUseCase(null);
    }

    @Bean
    public CreateEventUseCase createEventUseCase() {
        // TODO: fix dependency
        return new CreateEventUseCase(null, null);
    }

    @Bean
    public CreatePartnerUseCase createPartnerUseCase() {
        // TODO: fix dependency
        return new CreatePartnerUseCase(null);
    }

    @Bean
    public GetCustomerByIDUseCase getCustomerUseCase() {
        // TODO: fix dependency
        return new GetCustomerByIDUseCase(null);
    }

    @Bean
    public GetPartnerByIDUseCase getPartnerUseCase() {
        // TODO: fix dependency
        return new GetPartnerByIDUseCase(null);
    }

    @Bean
    public SubscribeCustomerToEventUseCase subscribeCustomerToEventUseCase() {
        // TODO: fix dependency
        return new SubscribeCustomerToEventUseCase(null, null, null);
    }
}
