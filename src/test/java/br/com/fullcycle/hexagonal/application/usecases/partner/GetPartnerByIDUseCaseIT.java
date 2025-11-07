package br.com.fullcycle.hexagonal.application.usecases.partner;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.PartnerEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.PartnerJpaRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.EventJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

class GetPartnerByIDUseCaseIT extends IntegrationTest {

    @Autowired
    private GetPartnerByIDUseCase useCase;

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
    @DisplayName("Deve obter um parceiro por id")
    public void testGetById() {
        //given
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";
        var partner = createPartner(expectedCNPJ, expectedEmail, expectedName);
        final var expectedId = partner.getId().toString();

        final var input = new GetPartnerByIDUseCase.Input(expectedId);

        //when
        final var output = useCase.execute(input).get();

        //then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um parceiro inexistente por id")
    public void testGetByIdWithInvalidId() {
        //given
        final var expectedId = UUID.randomUUID().toString();

        final var input = new GetPartnerByIDUseCase.Input(expectedId);

        //when
        final var output = useCase.execute(input);

        //then
        Assertions.assertTrue(output.isEmpty());
    }

    private PartnerEntity createPartner(final String cnpj, final String email, final String name) {
        final PartnerEntity aPartner = new PartnerEntity();
        aPartner.setCnpj(cnpj);
        aPartner.setEmail(email);
        aPartner.setName(name);
        return partnerJpaRepository.save(aPartner);
    }
}