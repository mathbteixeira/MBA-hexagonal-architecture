package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.exceptions.ValidationException;

public class EventTicket {

    private final EventTicketId eventTicketId;
    private final EventId eventId;
    private final CustomerId customerId;
    private TicketId ticketId;
    private int ordering;

    public EventTicket(final EventTicketId eventTicketId, final EventId eventId, final CustomerId customerId, final TicketId ticketId, final Integer ordering) {
        if (eventTicketId == null) {
            throw new ValidationException("Invalid EventTicketId for EventTicket");
        }

        if (eventId == null) {
            throw new ValidationException("Invalid EventId for EventTicket");
        }

        if (customerId == null) {
            throw new ValidationException("Invalid CustomerId for EventTicket");
        }

        this.eventTicketId = eventTicketId;
        this.eventId = eventId;
        this.customerId = customerId;
        this.ticketId = ticketId;
        this.ordering = ordering;
    }

    public static EventTicket newTicket(final EventId eventId, final CustomerId customerId, final int ordering) {
        return new EventTicket(EventTicketId.unique(), eventId, customerId, null, ordering);
    }

    public EventTicket associateTicket(final TicketId ticketId) {
        this.ticketId = ticketId;
        return this;
    }

    public EventTicketId eventTicketId() {
        return eventTicketId;
    }

    public TicketId ticketId() {
        return ticketId;
    }

    public EventId eventId() {
        return eventId;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public int ordering() {
        return ordering;
    }

    private void setOrdering(final Integer ordering) {
        if (ordering == null) {
            throw new ValidationException("Invalid ordering for EventTicket");
        }
        this.ordering = ordering;
    }
}
