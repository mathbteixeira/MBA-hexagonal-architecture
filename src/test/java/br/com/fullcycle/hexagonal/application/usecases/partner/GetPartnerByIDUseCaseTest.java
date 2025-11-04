package br.com.fullcycle.hexagonal.application.usecases.partner;

import br.com.fullcycle.hexagonal.application.repository.InMemoryPartnerRepository;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class GetPartnerByIDUseCaseTest {

    @Test
    @DisplayName("Deve obter um parceiro por id")
    public void testGetById() {
        //given
        final var expectedCNPJ = "41.536.538/0001-00";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aPartner = Partner.newPartner(expectedName, expectedCNPJ, expectedEmail);

        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var expectedId = aPartner.partnerId().value();
        final var input = new GetPartnerByIDUseCase.Input(expectedId);

        //when
        final var useCase = new GetPartnerByIDUseCase(partnerRepository);
        final var output = useCase.execute(input).get();

        //then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um parceiro inexistente por id")
    public void testGetByIdWithInvaidId() {
        //given
        final var expectedId = UUID.randomUUID().toString();

        final var input = new GetPartnerByIDUseCase.Input(expectedId);

        final var partnerRepository = new InMemoryPartnerRepository();

        //when
        final var useCase = new GetPartnerByIDUseCase(partnerRepository);
        final var output = useCase.execute(input);

        //then
        Assertions.assertTrue(output.isEmpty());
    }

}