package org.example.teamcity.api.spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.example.teamcity.api.config.Config;
import org.example.teamcity.api.models.User;

public class Specifications {
    public static Specifications spec;
    private static RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.addFilter(new RequestLoggingFilter());
        requestBuilder.addFilter(new ResponseLoggingFilter());
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder;
    }

    public static RequestSpecification superUserSpec() {
        var requestBuilder = reqBuilder();
        System.out.println("Creating super user spec with host: " + Config.getProperty("host"));
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted("", Config.getProperty("superUserToken"), Config.getProperty("host")));
        return requestBuilder.build();
    }

    public static RequestSpecification unauthSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder.build();
    }

    public static RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(), Config.getProperty("host")));
        return requestBuilder.build();
    }
}
