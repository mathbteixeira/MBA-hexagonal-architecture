package br.com.fullcycle.domain.person;

import br.com.fullcycle.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    @DisplayName("Deve instanciar um Name valido")
    public void testCreateName() {
        //given
        final var expectedName = "John Doe";

        //when
        final var actualName = new Name(expectedName);

        //then
        Assertions.assertEquals(expectedName, actualName.value());
    }

    @Test
    @DisplayName("Nao deve instanciar um Name nulo")
    public void testCreateNameWithNullValue() {
        //given
        final var expectedError = "Invalid value for Name";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> new Name(null));

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
