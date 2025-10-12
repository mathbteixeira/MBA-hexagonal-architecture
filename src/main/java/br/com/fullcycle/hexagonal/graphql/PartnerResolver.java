package br.com.fullcycle.hexagonal.graphql;

import br.com.fullcycle.hexagonal.application.usecases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetCustomerByIDUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIDUseCase;
import br.com.fullcycle.hexagonal.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.dtos.PartnerDTO;
import br.com.fullcycle.hexagonal.services.CustomerService;
import br.com.fullcycle.hexagonal.services.PartnerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

// Adapter
@Controller
public class PartnerResolver {
    private final PartnerService partnerService;

    public PartnerResolver(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @MutationMapping
    public CreatePartnerUseCase.Output createPartner(@Argument PartnerDTO input) {
        final var useCase = new CreatePartnerUseCase(partnerService);
        return useCase.execute(new CreatePartnerUseCase.Input(input.getCnpj(), input.getEmail(), input.getName()));
    }

    public GetPartnerByIDUseCase.Output partnerOfId(@Argument Long id) {
        final var useCase = new GetPartnerByIDUseCase(partnerService);
        return useCase.execute(new GetPartnerByIDUseCase.Input(id))
                .orElseGet(null);
    }
}
