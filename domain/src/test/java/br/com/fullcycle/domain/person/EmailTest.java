package br.com.fullcycle.domain.person;

import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    @DisplayName("Deve instanciar um Email valido")
    public void testCreateEmail() {
        //given
        final var expectedEmail = "john.doe@gmail.com";

        //when
        final var actualEmail = new Email(expectedEmail);

        //then
        Assertions.assertEquals(expectedEmail, actualEmail.value());
    }

    @Test
    @DisplayName("Nao deve instanciar um Email invalido")
    public void testCreateEmailWithInvalidValue() {
        //given
        final var expectedError = "Invalid value for Email";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Email("123456"));

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Nao deve instanciar um Email nulo")
    public void testCreateEmailWithNullValue() {
        //given
        final var expectedError = "Invalid value for Email";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Email(null));

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
