package br.com.fullcycle.hexagonal.infrastructure.controllers;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.EventDTO;
import br.com.fullcycle.hexagonal.infrastructure.dtos.SubscribeDTO;
import br.com.fullcycle.hexagonal.infrastructure.services.CustomerService;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;

// Adapter
@RestController
@RequestMapping(value = "events")
public class EventController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EventService eventService;

    @Autowired
    private PartnerService partnerService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<?> create(@RequestBody EventDTO dto) {
        try {
            final Long partnerId = Objects.requireNonNull(dto.getPartner(), "Partner is required").getId();
            final var useCase = new CreateEventUseCase(partnerService, eventService);
            final var output = useCase.execute(new CreateEventUseCase.Input(dto.getDate(), dto.getName(), partnerId, dto.getTotalSpots()));
            return ResponseEntity.created(java.net.URI.create("/events/" + output.id())).body(output);
        } catch (ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }

    }

    @Transactional
    @PostMapping(value = "/{id}/subscribe")
    public ResponseEntity<?> subscribe(@PathVariable Long id, @RequestBody SubscribeDTO dto) {
        try {
            final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
            final var output = useCase.execute(new SubscribeCustomerToEventUseCase.Input(id, dto.getCustomerId()));
            return ResponseEntity.ok(output);
        } catch (ValidationException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }

    }
}
