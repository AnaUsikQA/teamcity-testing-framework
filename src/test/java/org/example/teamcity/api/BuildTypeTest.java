package org.example.teamcity.api;

import org.apache.http.HttpStatus;
import org.example.teamcity.api.models.*;
import org.example.teamcity.api.requests.CheckedRequests;
import org.example.teamcity.api.requests.unchecked.UncheckedBase;
import org.example.teamcity.api.spec.Specifications;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static io.qameta.allure.Allure.step;
import static org.example.teamcity.api.enums.Endpoint.*;
import static org.example.teamcity.api.generators.TestDataGenerator.generate;
import static org.example.teamcity.api.spec.Specifications.spec;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));


        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());


        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }


    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        // Создаём проект (от суперюзера, только он может)
        superUserCheckedRequests.getRequest(PROJECTS).create(testData.getProject());
        // Создаем юзера и сразу назначаем его проджектом
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        superUserCheckedRequests.<User>getRequest(USERS).create(testData.getUser());

        var projectAdminRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        // Проджект добавляет билдтайп
        projectAdminRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = projectAdminRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
        }



    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        // Шаг 1: Создаём проект1 от имени суперпользователя
        superUserCheckedRequests.getRequest(PROJECTS).create(testData.getProject());

        // Шаг 2: Создаём project2 (новый проект)
        var project2 = generate(Project.class);
        superUserCheckedRequests.getRequest(PROJECTS).create(project2);

        // Шаг 3: Создаём первого пользователя и назначаем ему роль PROJECT_ADMIN для project1
        var user1 = generate(User.class);
        user1.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckedRequests.<User>getRequest(USERS).create(user1);


        // Шаг 4: Создаём второго пользователя и назначаем ему роль PROJECT_ADMIN для project2
        var user2 = generate(User.class);
        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));
        superUserCheckedRequests.<User>getRequest(USERS).create(user2);

        // Шаг 5: Пытаемся создать buildType для project1 (проект user1) от имени user2
        var buildTypeForProject1 = generate(Arrays.asList(testData.getProject()), BuildType.class);
        buildTypeForProject1.setProjectId(testData.getProject().getId());

        // Шаг 6: Выполняем запрос на создание и проверяем, что он завершится ошибкой доступа
        new UncheckedBase(Specifications.authSpec(user2), BUILD_TYPES)
                .create(buildTypeForProject1)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("Access denied. Check the user has enough permissions to perform the operation."));

    }
}
