package br.com.fullcycle.hexagonal.infrastructure.controllers;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreatePartnerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.GetPartnerByIDUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.PartnerDTO;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

// Adapter
@RestController
@RequestMapping(value = "partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PartnerDTO dto) {
        try {
            final var useCase = new CreatePartnerUseCase(partnerService);
            final var output = useCase.execute(new CreatePartnerUseCase.Input(dto.getCnpj(), dto.getEmail(), dto.getName()));
            return ResponseEntity.created(URI.create("/partners/" + output.id())).body(output);
        } catch (ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        final var useCase = new GetPartnerByIDUseCase(partnerService);
        return useCase.execute(new GetPartnerByIDUseCase.Input(id))
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

}
