package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

public class EventTest {

    @Test
    @DisplayName("Deve criar um evento")
    void testCreateEvent() {
        //given
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = aPartner.partnerId().value();
        final var expectedTickets = 0;

        //when
        final var actualEvent = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, aPartner);

        //then
        Assertions.assertNotNull(actualEvent.eventId());
        Assertions.assertEquals(expectedDate, actualEvent.date().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(expectedName, actualEvent.name().value());
        Assertions.assertEquals(expectedTotalSpots, actualEvent.totalSpots());
        Assertions.assertEquals(expectedPartnerId, actualEvent.partnerId().value());
        Assertions.assertEquals(expectedTickets, actualEvent.allTickets().size());
    }

    @Test
    @DisplayName("Não deve criar um evento com nome invalido")
    void testCreateEventWithInvalidName() {
        //given
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");

        final var expectedError = "Invalid value for Name";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Event.newEvent(null, "2021-01-01", 10, aPartner));

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve criar um evento com data invalido")
    void testCreateEventWithInvalidDate() {
        //given
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");

        final var expectedError = "Invalid date for Event";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Event.newEvent("John Doe", "20210101", 10, aPartner));

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve criar um evento com total spots invalido")
    void testCreateEventWithInvalidTotalSpots() {
        //given
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");

        final var expectedError = "Invalid totalSpots for Event";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Event.newEvent("John Doe", "2021-01-01", 0, aPartner));

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Deve reservar um ticket quando é possível")
    void testReserveTicket() {
        //given
        final var aCustomer =
                Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");

        final var expectedCustomerId = aCustomer.customerId();
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = aPartner.partnerId().value();
        final var expectedTickets = 1;
        final var expectedTicketOrder = 1;
        final var actualEvent = Event.newEvent(expectedName, expectedDate, expectedTotalSpots, aPartner);
        final var expectedEventId = actualEvent.eventId();

        //when
        final var actualTicket = actualEvent.reserveTicket(expectedCustomerId);

        //then
        Assertions.assertNotNull(actualTicket.ticketId());
        Assertions.assertNotNull(actualTicket.reservedAt());
        Assertions.assertNull(actualTicket.paidAt());
        Assertions.assertEquals(expectedEventId, actualTicket.eventId());
        Assertions.assertEquals(expectedCustomerId, actualTicket.customerId());
        Assertions.assertEquals(TicketStatus.PENDING, actualTicket.ticketStatus());

        Assertions.assertEquals(expectedDate, actualEvent.date().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(expectedName, actualEvent.name().value());
        Assertions.assertEquals(expectedTotalSpots, actualEvent.totalSpots());
        Assertions.assertEquals(expectedPartnerId, actualEvent.partnerId().value());
        Assertions.assertEquals(expectedTickets, actualEvent.allTickets().size());

        final var actualEventTicket = actualEvent.allTickets().iterator().next();
        Assertions.assertEquals(expectedTicketOrder, actualEventTicket.ordering());
        Assertions.assertEquals(expectedEventId, actualEventTicket.eventId());
        Assertions.assertEquals(expectedCustomerId, actualEventTicket.customerId());
        Assertions.assertEquals(actualTicket.ticketId(), actualEventTicket.ticketId());
    }

    @Test
    @DisplayName("Não deve reservar um ticket quando o evento está esgotado")
    void testReserveTicketWhenEventIsSoldOut() {
        //given
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var aCustomer =
                Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var aCustomer2 =
                Customer.newCustomer("John Doe 1", "111.456.789-01", "john1.doe@gmail.com");

        final var expectedTotalSpots = 1;
        final var actualEvent = Event.newEvent("Disney", "2021-01-01", expectedTotalSpots, aPartner);
        actualEvent.reserveTicket(aCustomer.customerId());

        final var expectedError = "Event sold out";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> actualEvent.reserveTicket(aCustomer2.customerId()));;

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve reservar dois tickets para um mesmo cliente")
    void testReserveTwoTicketsForTheSameCustomer() {
        //given
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var aCustomer =
                Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");

        final var expectedTotalSpots = 1;
        final var actualEvent = Event.newEvent("Disney", "2021-01-01", expectedTotalSpots, aPartner);
        actualEvent.reserveTicket(aCustomer.customerId());

        final var expectedError = "Email already registered";

        //when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> actualEvent.reserveTicket(aCustomer.customerId()));;

        //then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }
}
