package org.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.teamcity.api.models.BaseModel;
import org.example.teamcity.api.models.BuildType;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
