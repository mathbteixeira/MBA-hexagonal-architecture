package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.entities.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.PartnerRepository;

import java.util.Objects;

public class CreatePartnerUseCase extends UseCase<CreatePartnerUseCase.Input, CreatePartnerUseCase.Output> {

    private final PartnerRepository partnerRepository;

    public CreatePartnerUseCase(final PartnerRepository partnerRepository) {
        this.partnerRepository = Objects.requireNonNull(partnerRepository);
    }

    @Override
    public CreatePartnerUseCase.Output execute(final CreatePartnerUseCase.Input input) {
        if (partnerRepository.partnerOfCnpj(input.cnpj).isPresent()) {
            throw new ValidationException("Partner already exists");
        }
        if (partnerRepository.partnerOfEmail(input.email).isPresent()) {
            throw new ValidationException("Partner already exists");
        }

        var partner = Partner.newPartner(input.name, input.cnpj, input.email);
        partner = partnerRepository.create(partner);

        return new CreatePartnerUseCase.Output(
                partner.partnerId().value(),
                partner.cnpj().value(),
                partner.email().value(),
                partner.name().value()
        );
    }

    public record Input(String cnpj, String email, String name) {}

    public record Output(String id, String cnpj, String email, String name) {}
}
