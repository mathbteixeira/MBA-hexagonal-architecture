package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.person.Name;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.domain.partner.PartnerId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Event {

    private static final int ONE = 1;
    private final EventId eventId;
    private Name name;
    private LocalDate date;
    private int totalSpots;
    private PartnerId partnerId;
    private Set<EventTicket> tickets;

    public Event(final EventId eventId) {
        if (eventId == null) {
            throw new ValidationException("Invalid eventId for Event");
        }

        this.eventId = eventId;
        this.tickets = new HashSet<>(0);
    }
    public Event(final EventId eventId, final String name, final String date, final int totalSpots, final PartnerId partnerId) {
        this(eventId);
        this.setName(name);
        this.setDate(date);
        this.setTotalSpots(totalSpots);
        this.setPartnerId(partnerId);
    }

    public static Event newEvent(final String name, final String date, final int totalSpots, final Partner partner) {
        return new Event(EventId.unique(), name, date, totalSpots, partner.partnerId());
    }

    public Ticket reserveTicket(final CustomerId customerId) {
        this.allTickets().stream()
                .filter(it -> Objects.equals(it.customerId(), customerId))
                .findFirst().ifPresent(it -> {
                    throw new ValidationException("Email already registered");
                });
        if (totalSpots() < allTickets().size() + ONE) {
            throw new ValidationException("Event sold out");
        }

        final var newTicket = Ticket.newTicket(customerId, this.eventId());

        this.tickets.add(new EventTicket(newTicket.ticketId(), this.eventId(), customerId, this.tickets.size() + ONE));

        return newTicket;
    }

    public EventId eventId() {
        return eventId;
    }

    public Name name() {
        return name;
    }

    public LocalDate date() {
        return date;
    }

    public int totalSpots() {
        return totalSpots;
    }

    public PartnerId partnerId() {
        return partnerId;
    }

    public Set<EventTicket> allTickets() {
        return Collections.unmodifiableSet(tickets);
    }

    private void setName(final String name) {
        this.name = new Name(name);
    }

    private void setDate(final String date) {
        if (date == null) {
            throw new ValidationException("Invalid date for Event");
        }
        try {
            this.date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (final RuntimeException e) {
            throw new ValidationException("Invalid date for Event", e);
        }
    }

    private void setTotalSpots(final int totalSpots) {
        if (totalSpots <= 0) {
            throw new ValidationException("Invalid totalSpots for Event");
        }
        this.totalSpots = totalSpots;
    }

    private void setPartnerId(final PartnerId partnerId) {
        if (partnerId == null) {
            throw new ValidationException("Invalid partnerId for Event");
        }
        this.partnerId = partnerId;
    }
}
