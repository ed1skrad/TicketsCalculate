package com.flight.cost.tz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.cost.tz.model.Ticket;
import com.flight.cost.tz.model.Tickets;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private static final String FILE_PATH = "tickets.json";
    private static final DateTimeFormatter DATE_TIME_FORMATTER_WITH_ZERO = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_WITHOUT_ZERO = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

    public List<Ticket> loadTickets() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Tickets tickets = mapper.readValue(new File(FILE_PATH), Tickets.class);
        System.out.println(tickets.getTickets());
        return tickets.getTickets();
    }

    public Map<String, Long> getMinFlightTimeByCarrier(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV"))
                .collect(Collectors.groupingBy(Ticket::getCarrier,
                        Collectors.mapping(this::calculateFlightDuration, Collectors.minBy(Long::compareTo))))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().orElse(Long.MAX_VALUE)));
    }

    public long calculateFlightDuration(Ticket ticket) {
        String departureDateTimeStr = ticket.getDeparture_date() + " " + ticket.getDeparture_time();
        String arrivalDateTimeStr = ticket.getArrival_date() + " " + ticket.getArrival_time();

        LocalDateTime departure = parseDateTime(departureDateTimeStr);
        LocalDateTime arrival = parseDateTime(arrivalDateTimeStr);

        return Duration.between(departure, arrival).toMinutes();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER_WITH_ZERO);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER_WITHOUT_ZERO);
        }
    }

    public double calculateAveragePrice(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV"))
                .mapToInt(Ticket::getPrice)
                .average().orElse(0.0);
    }

    public double calculateMedianPrice(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream()
                .filter(ticket -> ticket.getOrigin().equals("VVO") && ticket.getDestination().equals("TLV"))
                .map(Ticket::getPrice)
                .sorted()
                .toList();

        int size = prices.size();
        if (size == 0) return 0.0;

        if (size % 2 == 0) {
            return (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        } else {
            return prices.get(size / 2);
        }
    }

    public double calculatePriceDifference(double averagePrice, double medianPrice) {
        return averagePrice - medianPrice;
    }
}
