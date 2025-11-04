package br.com.fullcycle.hexagonal.application.usecases.customer;

import br.com.fullcycle.hexagonal.application.usecases.UseCase;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;

import java.util.Objects;
import java.util.Optional;

public class GetCustomerByIDUseCase extends UseCase<GetCustomerByIDUseCase.Input, Optional<GetCustomerByIDUseCase.Output>> {

    private final CustomerRepository customerRepository;

    public GetCustomerByIDUseCase(final CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    @Override
    public Optional<Output> execute(final Input input) {
        return customerRepository.customerOfId(CustomerId.with(input.id))
                .map(c -> new Output(c.customerId().value(), c.cpf().value(), c.email().value(), c.name().value()));
    }

    public record Input(String id) {}

    public record Output(String id, String cpf, String email, String name) {}
}
