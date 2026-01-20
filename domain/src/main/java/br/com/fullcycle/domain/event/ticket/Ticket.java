package br.com.fullcycle.domain.event.ticket;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.EventTicketId;
import br.com.fullcycle.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Ticket {

    private final TicketId ticketId;
    private final Set<DomainEvent> domainEvents;
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
        this.domainEvents = new HashSet<>();
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

    public static Ticket newTicket(
            final EventTicketId eventTicketId,
            final CustomerId customerId,
            final EventId eventId) {
        final Ticket aTicket = newTicket(
                customerId,
                eventId);
        aTicket.domainEvents.add(new TicketCreated(aTicket.ticketId, eventTicketId, eventId, customerId));
        return aTicket;
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

    public Set<DomainEvent> allDomainEvents() {
        return Collections.unmodifiableSet(domainEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ticketId);
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
