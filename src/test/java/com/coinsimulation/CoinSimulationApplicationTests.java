package com.coinsimulation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CoinSimulationApplicationTests {


    //    private RSocketRequester requester;
//    @Autowired
//    private RSocketRequester.Builder builder;
//
//    @BeforeAll
//    public void setup() {
//        this.requester = this.builder
//                .transport(TcpClientTransport.create("localhost", 800));
//    }
//
//    @Test
//    public void requestStream() {
//        Flux<Ticket> send = this.requester.route("ticket.bitcoin")
//                .data(new RequestDto(""))
//                .retrieveFlux(Ticket.class)
//                .doOnNext(System.out::println);
//        StepVerifier.create(send)
//                .expectNextCount(10);
//    }

    @Test
    void contextLoads() {
    }

}
