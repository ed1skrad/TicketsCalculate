package com.flight.cost.tz.controller;

import com.flight.cost.tz.model.Ticket;
import com.flight.cost.tz.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/calculate")
    public String getTicketStats() throws IOException {
        List<Ticket> tickets = ticketService.loadTickets();

        Map<String, Long> minFlightTimes = ticketService.getMinFlightTimeByCarrier(tickets);
        double averagePrice = ticketService.calculateAveragePrice(tickets);
        double medianPrice = ticketService.calculateMedianPrice(tickets);
        double priceDifference = ticketService.calculatePriceDifference(averagePrice, medianPrice);

        StringBuilder result = new StringBuilder();
        result.append("Minimal flight time for every carrier:\n");
        minFlightTimes.forEach((carrier, time) -> result.append(carrier).append(": ").append(time).append(" minutes\n"));
        result.append("\nDifference between average and median price for flight VVO to TLV:\n");
        result.append("Average: ").append(averagePrice).append("\n");
        result.append("Median: ").append(medianPrice).append("\n");
        result.append("Difference: ").append(priceDifference).append("\n");

        return result.toString();
    }
}