package org.jboss.weld.examples.numberguess.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Test;

public class NumberguessIT {

    private java.util.Random random = new java.util.Random(System.currentTimeMillis());

    @Test
    public void testGuessNumber() throws Exception {
        guessNumber();
    }

    public void guessNumber() throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(BasicRuntimeIT.getServerHost() + "/home.jsf"))
                .POST(ofFormData(Map.of("numberGuess:inputGuess", (Object) getNumber())))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().toString().contains("<form id=\"numberGuess\" name=\"numberGuess\" method=\"post\""));
    }

    private int getNumber() {
        return random.nextInt(99) + 1;
    }

    public static BodyPublisher ofFormData(Map<String, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder
                    .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder
                    .append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return BodyPublishers.ofString(builder.toString());
    }

}
