package org.example.teamcity.api.requests;

import io.restassured.specification.RequestSpecification;
import org.example.teamcity.api.enums.Endpoint;
import org.example.teamcity.api.requests.unchecked.UncheckedBase;

import java.util.EnumMap;

public class UncheckedRequests {
    private final EnumMap<Endpoint, UncheckedBase> requests = new EnumMap<>(Endpoint.class);

    public UncheckedRequests(RequestSpecification spec) {
        for (var endpoint: Endpoint.values()) {
            requests.put(endpoint, new UncheckedBase(spec, endpoint));
        }
    }

    public UncheckedBase getRequest(Endpoint endpoint) {
        return requests.get(endpoint);
    }
}
