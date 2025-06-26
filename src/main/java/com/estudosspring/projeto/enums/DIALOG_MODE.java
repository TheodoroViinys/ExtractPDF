package com.estudosspring.projeto.enums;

public enum DIALOG_MODE {
    OPEN_FILE("Abrir arquivo"),
    SAVE_FILE("Salvar");

    private String titleMode;

    DIALOG_MODE(String s) {
        titleMode = s;
    }

    public String getTitleMode() {
        return titleMode;
    }
}
