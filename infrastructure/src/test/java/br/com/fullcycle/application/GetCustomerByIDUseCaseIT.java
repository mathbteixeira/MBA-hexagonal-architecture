package br.com.fullcycle.application;

import br.com.fullcycle.IntegrationTest;
import br.com.fullcycle.application.customer.GetCustomerByIDUseCase;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.customer.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

class GetCustomerByIDUseCaseIT extends IntegrationTest {

    @Autowired
    private GetCustomerByIDUseCase useCase;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve obter um cliente por id")
    public void testGetById() {
        //given
        final var expectedCPF = "123.456.789-01";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        var customer = createCustomer(expectedCPF, expectedEmail, expectedName);
        final var expectedId = customer.customerId().value();

        final var input = new GetCustomerByIDUseCase.Input(String.valueOf(expectedId));

        //when
        final var output = useCase.execute(input).get();

        //then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um ciente inexistente por id")
    public void testGetByIdWithInvaidId() {
        //given
        final var expectedId = UUID.randomUUID().toString();

        final var input = new GetCustomerByIDUseCase.Input(expectedId);

        //when
        final var output = useCase.execute(input);

        //then
        Assertions.assertTrue(output.isEmpty());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        return customerRepository.create(Customer.newCustomer(name, cpf, email));
    }
}