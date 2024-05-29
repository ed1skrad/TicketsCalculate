package com.flight.cost.tz.model;

import java.util.List;

public class Tickets {
    private List<Ticket> tickets;

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public String toString() {
        return "Tickets{" +
                "tickets=" + tickets +
                '}';
    }
}
