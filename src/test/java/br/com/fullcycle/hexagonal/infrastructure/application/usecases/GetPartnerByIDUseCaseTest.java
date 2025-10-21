package br.com.fullcycle.hexagonal.infrastructure.application.usecases;

import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIDUseCase;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

class GetPartnerByIDUseCaseTest {

    @Test
    @DisplayName("Deve obter um parceiro por id")
    public void testGetById() {
        //given
        final long expectedId = UUID.randomUUID().getMostSignificantBits();
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var aPartner = new Partner();
        aPartner.setId(expectedId);
        aPartner.setCnpj(expectedCNPJ);
        aPartner.setEmail(expectedEmail);
        aPartner.setName(expectedName);

        final var input = new GetPartnerByIDUseCase.Input(expectedId);

        //when
        final PartnerService partnerService = Mockito.mock(PartnerService.class);
        Mockito.when(partnerService.findById(expectedId)).thenReturn(Optional.of(aPartner));

        final var useCase = new GetPartnerByIDUseCase(partnerService);
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
        final long expectedId = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetPartnerByIDUseCase.Input(expectedId);

        //when
        final PartnerService partnerService = Mockito.mock(PartnerService.class);
        Mockito.when(partnerService.findById(expectedId)).thenReturn(Optional.empty());

        final var useCase = new GetPartnerByIDUseCase(partnerService);
        final var output = useCase.execute(input);

        //then
        Assertions.assertTrue(output.isEmpty());
    }

}