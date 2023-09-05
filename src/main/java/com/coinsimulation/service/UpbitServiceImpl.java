package com.coinsimulation.service;

import com.coinsimulation.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class UpbitServiceImpl implements UpbitService {
    private final ObjectMapper mapper;
    private final TicketRepository ticketRepository;


}
