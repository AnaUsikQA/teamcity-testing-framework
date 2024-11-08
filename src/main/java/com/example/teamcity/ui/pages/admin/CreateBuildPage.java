package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildPage extends CreateBasePage {
    private static final String BUILD_SHOW_MODE = "createBuildTypeMenu"; // Замените на соответствующий режим

    private SelenideElement buildNameInput = $("#buildName"); // ID поля для имени билд-конфигурации

    public static CreateBuildPage open(String projectId) {
        // Формируем URL для страницы создания билд-конфигурации
        String url = "http://192.168.1.110:8111/admin/createObjectMenu.html?projectId=" + projectId + "&showMode=" + BUILD_SHOW_MODE;
        return Selenide.open(url, CreateBuildPage.class); // Открываем страницу и возвращаем текущий объект
    }

    public CreateBuildPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupBuild(String buildTypeName) {
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
    }
}
