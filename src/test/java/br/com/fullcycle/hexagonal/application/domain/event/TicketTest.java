package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

public class TicketTest {

    @Test
    @DisplayName("Deve criar um ticket")
    void testCreateTicket() {
        //given
        final var aCustomer =
                Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail.com");
        final var aPartner =
                Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var anEvent = Event.newEvent("Disney", "2021-01-01", 10, aPartner);

        final var expectedCustomerId = aCustomer.customerId();
        final var expectedEventId = anEvent.eventId();

        //when
        final var actualTicket = Ticket.newTicket(aCustomer.customerId(), anEvent.eventId());

        //then
        Assertions.assertNotNull(actualTicket.ticketId());
        Assertions.assertNotNull(actualTicket.reservedAt());
        Assertions.assertNull(actualTicket.paidAt());
        Assertions.assertEquals(expectedEventId, actualTicket.eventId());
        Assertions.assertEquals(expectedCustomerId, actualTicket.customerId());
        Assertions.assertEquals(TicketStatus.PENDING, actualTicket.ticketStatus());
    }
}
