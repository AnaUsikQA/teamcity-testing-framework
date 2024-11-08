package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {
        // подготовка окружения
        loginAs(testData.getUser());

        // взаимодействие с UI
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // проверка состояния API
        // (корректность отправки данных с UI на API)
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);



        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)
        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));


        softy.assertTrue(foundProjects);
    }


    // Базовое ДЗ: Реализовать создание билд конфигурации (минимум - один позитивный и один негативный тест)

    @Test(description = "User should be able to create build configuration", groups = {"Positive"})
    public void userCreatesProjectAndOpensBuildConfigurationPage() {
        loginAs(testData.getUser());



        // Создаем проект через API
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        softy.assertNotNull(createdProject, "Проект должен быть создан для привязки билд конфигурации.");

        // Получаем projectId созданного проекта
        String projectId = createdProject.getId();
        System.out.println("Созданный projectId: " + projectId);

        // Открываем страницу создания билд-конфигурации
        CreateBuildPage.open(projectId) // Открываем страницу для данного projectId
                .createForm(REPO_URL) // Заполняем URL репозитория
                .setupBuild(testData.getBuildType().getName()); // Указываем имя билд-конфигурации

        // Проверка состояния API: (корректность отправки данных с UI на API)
        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES)
                .read("name:" + testData.getBuildType().getName());
        softy.assertNotNull(createdBuildType, "Билд-конфигурация должна быть успешно создана через API.");


        // Пауза на 10 секунд для визуальной проверки
        Selenide.sleep(5000);

        // Проверка состояния UI: наличие сообщения о создании билд-конфигурации
        $("div.successMessage#unprocessed_objectsCreated") // Ищем элемент с классом successMessage и id unprocessed_objectsCreated
                .shouldBe(Condition.visible) // Убедимся, что элемент видим
                .shouldHave(Condition.text("have been successfully created")); // Проверяем, что текст соответствует ожиданиям

    }

    @Test(description = "User should not be able to create build configuration without name", groups = {"Negative"})
    public void userCreatesBuildConfigurationWithoutName() {
        // Подготовка окружения — авторизация
        loginAs(testData.getUser());

        // Создаем проект через API
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());
        softy.assertNotNull(createdProject, "Проект должен быть создан для привязки билд конфигурации.");

        // Получаем projectId созданного проекта
        String projectId = createdProject.getId();
        System.out.println("Созданный projectId: " + projectId);

        // Открываем страницу создания билд-конфигурации
        CreateBuildPage.open(projectId) // Открываем страницу для данного projectId
                .createForm(REPO_URL) // Заполняем URL репозитория
                .setupBuild(""); // Оставляем имя билд-конфигурации пустым

        // Проверка состояния UI: проверка на отображение сообщения об ошибке
        $("span#error_buildTypeName") // Используем ID для элемента с сообщением об ошибке
                .shouldBe(Condition.visible) // Убедимся, что элемент видим
                .shouldHave(Condition.text("Build configuration name must not be empty")); // Проверяем текст ошибки
    }




}
