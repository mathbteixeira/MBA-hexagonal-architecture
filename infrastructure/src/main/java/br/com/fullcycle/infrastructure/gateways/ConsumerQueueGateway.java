package br.com.fullcycle.infrastructure.gateways;

import br.com.fullcycle.application.ticket.CreateTicketForCustomerUseCase;
import br.com.fullcycle.domain.event.ticket.EventTicketReserved;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ConsumerQueueGateway implements QueueGateway {

    private final CreateTicketForCustomerUseCase createTicketForCustomerUseCase;
    private final ObjectMapper objectMapper;

    public ConsumerQueueGateway(final CreateTicketForCustomerUseCase createTicketForCustomerUseCase,
                                final ObjectMapper objectMapper) {
        this.createTicketForCustomerUseCase = Objects.requireNonNull(createTicketForCustomerUseCase);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Async(value = "queueExecutor")
    @Override
    public void publish(final String content) {

        if (content == null){
            return;
        }
        if (content.contains("event-ticket.reserved")) {
            final var dto = safeRead(content, EventTicketReserved.class);
            this.createTicketForCustomerUseCase.execute(new CreateTicketForCustomerUseCase.Input(dto.eventTicketId(), dto.eventId(), dto.customerId()));
        }
    }

    private <T> T safeRead(String content, Class<T> clazz) {
        try {
            return this.objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
