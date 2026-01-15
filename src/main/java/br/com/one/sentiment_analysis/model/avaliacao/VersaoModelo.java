package br.com.one.sentiment_analysis.model.avaliacao;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VersaoModelo {
    RANDOM_FOREST("rf"),
    LOGISTIC_REGRESSION("lr"),
    MULTINOMIAL_NB("nb");

    private final String pythonModelName;

    VersaoModelo(String pythonModelName) {
        this.pythonModelName = pythonModelName;
    }

    @JsonValue
    public String getPythonModelName() {
        return pythonModelName;
    }

    public static VersaoModelo fromString(String text) {
        for (VersaoModelo modelo : VersaoModelo.values()) {
            if (modelo.pythonModelName.equalsIgnoreCase(text)) {
                return modelo;
            }
        }
        return LOGISTIC_REGRESSION;
    }
}
