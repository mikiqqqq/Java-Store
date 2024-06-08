package org.store.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.store.model.User;
import org.store.utils.UserSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApiService {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper mapper;
    private final String baseUrl;

    public ApiService(String baseUrl) {
        this.httpClient = HttpClients.createDefault();
        this.mapper = new ObjectMapper();
        this.baseUrl = baseUrl;
    }

    public CloseableHttpResponse sendRequest(String endpoint, Object requestBody, String requestType) throws IOException {
        HttpUriRequestBase request = createRequest(baseUrl + endpoint, requestBody, requestType);
        return httpClient.execute(request);
    }

    private HttpUriRequestBase createRequest(String url, Object requestBody, String requestType) throws IOException {
        HttpUriRequestBase request;

        switch (requestType.toUpperCase()) {
            case "POST":
                request = new HttpPost(url);
                break;
            case "PUT":
                request = new HttpPut(url);
                break;
            case "DELETE":
                request = new HttpDelete(url);
                break;
            case "GET":
            default:
                request = new HttpGet(url);
                break;
        }

        if (requestBody != null) {
            String json = mapper.writeValueAsString(requestBody);
            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            request.setEntity(entity);
            request.setHeader("Content-type", "application/json");
        }

        setAuthHeaders(request);

        return request;
    }

    private void setAuthHeaders(HttpUriRequestBase request) {
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            request.setHeader("Email", user.getEmail());
            request.setHeader("Hashed-Password", user.getPasswordHash());
        }
    }
}