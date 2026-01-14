package br.com.one.sentiment_analysis.model.avaliacao;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VersaoModelo {
    RANDOM_FOREST("RandomForestClassifier"),
    DECISION_TREE("DecisionTreeClassifier"),
    LOGISTIC_REGRESSION("LogisticRegression"),
    MULTINOMIAL_NB("MultinomialNB");

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
