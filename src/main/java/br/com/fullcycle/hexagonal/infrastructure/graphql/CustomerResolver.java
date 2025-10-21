package br.com.fullcycle.hexagonal.infrastructure.graphql;

import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerByIDUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

// Adapter
@Controller
public class CustomerResolver {

    private final CustomerService customerService;

    public CustomerResolver(CustomerService customerService) {
        this.customerService = customerService;
    }

    @MutationMapping
    public CreateCustomerUseCase.Output createCustomer(@Argument CustomerDTO input) {
        final var useCase = new CreateCustomerUseCase(customerService);
        return useCase.execute(new CreateCustomerUseCase.Input(input.getCpf(), input.getEmail(), input.getName()));
    }

    @QueryMapping
    public GetCustomerByIDUseCase.Output customerOfId(@Argument Long id) {
        final var useCase = new GetCustomerByIDUseCase(customerService);
        return useCase.execute(new GetCustomerByIDUseCase.Input(id))
                .orElse(null);
    }
}
