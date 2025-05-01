package com.example.ce316project;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfigurationEntry {
    private final StringProperty name;
    private final StringProperty path;

    public ConfigurationEntry(String name, String path) {
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty pathProperty() {
        return path;
    }

    public String getName() {
        return name.get();
    }

    public String getPath() {
        return path.get();
    }
}

