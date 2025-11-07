package br.com.fullcycle.hexagonal.application.domain.event.ticket;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.TicketStatus;

import java.time.Instant;

public class Ticket {

    private final TicketId ticketId;
    private CustomerId customerId;
    private EventId eventId;
    private TicketStatus ticketStatus;
    private Instant paidAt;
    private Instant reservedAt;

    public Ticket(
            final TicketId ticketId,
            final CustomerId customerId,
            final EventId eventId,
            final TicketStatus ticketStatus,
            final Instant paidAt,
            final Instant reservedAt) {
        this.ticketId = ticketId;
        this.setCustomerId(customerId);
        this.setEventId(eventId);
        this.setTicketStatus(ticketStatus);
        this.setPaidAt(paidAt);
        this.setReservedAt(reservedAt);
    }

    public static Ticket newTicket(
            final CustomerId customerId,
            final EventId eventId) {
        return new Ticket(
                TicketId.unique(),
                customerId,
                eventId,
                TicketStatus.PENDING,
                null,
                Instant.now());
    }

    public TicketId ticketId() {
        return ticketId;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public EventId eventId() {
        return eventId;
    }

    public TicketStatus ticketStatus() {
        return ticketStatus;
    }

    public Instant paidAt() {
        return paidAt;
    }

    public Instant reservedAt() {
        return reservedAt;
    }

    private void setCustomerId(CustomerId customerId) {
        if (customerId == null) {
            throw new ValidationException("Invalid customerId for Ticket");
        }
        this.customerId = customerId;
    }

    private void setEventId(EventId eventId) {
        if (eventId == null) {
            throw new ValidationException("Invalid eventId for Ticket");
        }
        this.eventId = eventId;
    }

    private void setTicketStatus(TicketStatus ticketStatus) {
        if (ticketStatus == null) {
            throw new ValidationException("Invalid ticketStatus for Ticket");
        }
        this.ticketStatus = ticketStatus;
    }

    private void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    private void setReservedAt(Instant reservedAt) {
        if (reservedAt == null) {
            throw new ValidationException("Invalid reservedAt for Ticket");
        }
        this.reservedAt = reservedAt;
    }
}
