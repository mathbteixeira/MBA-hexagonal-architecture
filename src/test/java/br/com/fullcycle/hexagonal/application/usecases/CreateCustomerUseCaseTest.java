package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class CreateCustomerUseCaseTest {

    @Test
    @DisplayName("Deve criar um cliente")
    public void testCreateCustomer() {
        //given
        final var expectedCPF = "12345678901";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        //when
        final CustomerService customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.findByCpf(expectedCPF)).thenReturn(Optional.empty());
        Mockito.when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
        Mockito.when(customerService.save(any())).thenAnswer(a -> {
            var customer = a.getArgument(0, Customer.class);
            customer.setId(UUID.randomUUID().getMostSignificantBits());
            return customer;
        });
        final var useCase = new CreateCustomerUseCase(customerService);
        final var output = useCase.execute(createInput);

        //then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve criar um cliente com CPF duplicado")
    public void testCreateWithDuplicatedCPFShouldFail() {
        //given
        final var expectedCPF = "12345678901";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(expectedCPF);
        aCustomer.setEmail(expectedEmail);
        aCustomer.setName(expectedName);
        //when
        final CustomerService customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.findByCpf(expectedCPF)).thenReturn(Optional.of(aCustomer));

        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve criar um cliente com Email duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        //given
        final var expectedCPF = "12345678901";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Customer already exists";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedName);

        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(expectedCPF);
        aCustomer.setEmail(expectedEmail);
        aCustomer.setName(expectedName);
        //when
        final CustomerService customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.of(aCustomer));

        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
