package br.com.fullcycle.hexagonal.application.usecases.partner;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.Partner;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.EventJpaRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.PartnerJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreatePartnerUseCaseIT extends IntegrationTest {

    @Autowired
    private CreatePartnerUseCase useCase;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private PartnerJpaRepository partnerJpaRepository;

    @BeforeEach
    void tearDown() {
        eventJpaRepository.deleteAll();
        partnerJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreatePartner() {
        //given
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        //when
        final var output = useCase.execute(createInput);

        //then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Não deve criar um parceiro com CNPJ duplicado")
    public void testCreateWithDuplicatedCNPJShouldFail() {
        //given
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        createPartner(expectedCNPJ, expectedEmail, expectedName);

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve criar um parceiro com Email duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        //given
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        createPartner("12345678901234", expectedEmail, expectedName);

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        final Partner aPartner = new Partner();
        aPartner.setCnpj(cnpj);
        aPartner.setEmail(email);
        aPartner.setName(name);
        return partnerJpaRepository.save(aPartner);
    }
}
