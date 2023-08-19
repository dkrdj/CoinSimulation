package com.coinsimulation.repository;

import com.coinsimulation.entity.Ticket;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TicketRepository extends ReactiveCrudRepository<Ticket, Long> {
}
