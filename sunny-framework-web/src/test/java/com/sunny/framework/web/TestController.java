package com.sunny.framework.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;


@SpringBootTest
@AutoConfigureRestTestClient
public class TestController {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    public void testForm() {
        restTestClient.post().uri("/form").contentType(MediaType.APPLICATION_JSON)
                .body("""
                         {
                             "name": "aaa",
                             "date": "2026-01-01 00:00:00",
                             "localDate": "2026-01-01",
                             "localDateTime": "2026-01-01 00:00:00"
                         }
                        """)
                .exchange()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("aaa")
                .jsonPath("$.data.date").isEqualTo("2026-01-01 00:00:00")
                .jsonPath("$.data.localDate").isEqualTo("2026-01-01")
                .jsonPath("$.data.localDateTime").isEqualTo("2026-01-01 00:00:00");
    }

}