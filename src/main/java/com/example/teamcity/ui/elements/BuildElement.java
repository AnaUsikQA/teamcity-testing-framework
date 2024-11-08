package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class BuildElement extends BasePageElement {
    private SelenideElement name;
    private SelenideElement link;
    private SelenideElement button;

    public BuildElement(SelenideElement element) {
        super(element);
        this.name = find("span[class*='MiddleEllipsis__searchable']");
        this.link = find("a");
        this.button = find("button");
    }
}
