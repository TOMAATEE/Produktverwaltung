package de.fabian.fxstarter.assets;

import java.io.Serializable;
import java.util.Objects;

public class Setting<T> implements Serializable {
    private String name;
    private T option;

    public Setting(String name, T option) {
        this.name = name;
        this.option = option;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getOption() {
        return option;
    }

    public void setOption(T option) {
        this.option = option;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Setting<?> setting = (Setting<?>) o;
        return Objects.equals(name, setting.name) && Objects.equals(option, setting.option);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, option);
    }
}
