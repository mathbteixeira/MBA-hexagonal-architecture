package br.com.fullcycle.hexagonal.infrastructure.application.usecases;

import br.com.fullcycle.hexagonal.application.InMemoryPartnerRepository;
import br.com.fullcycle.hexagonal.application.domain.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreatePartnerUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreatePartner() {
        //given
        final var expectedCNPJ = "41.536.538/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        //when
        final var partnerRepository = new InMemoryPartnerRepository();
        final var useCase = new CreatePartnerUseCase(partnerRepository);
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
        final var expectedCNPJ = "41.536.538/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var aPartner = Partner.newPartner(expectedName, expectedCNPJ, "john@gmail.com");

        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        //when
        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve criar um parceiro com Email duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        //given
        final var expectedCNPJ = "41.536.538/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var aPartner = Partner.newPartner(expectedName, "41.536.538/0002-00", expectedEmail);

        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var createInput = new CreatePartnerUseCase.Input(expectedCNPJ, expectedEmail, expectedName);

        //when
        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}
