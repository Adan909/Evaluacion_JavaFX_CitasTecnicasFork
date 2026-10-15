module ni.edu.uam.evaluacion_javafx_citastecnicas {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.sql;

    opens ni.edu.uam.evaluacion_javafx_citastecnicas to javafx.fxml;
    exports ni.edu.uam.evaluacion_javafx_citastecnicas;

    exports ni.edu.uam.evaluacion_javafx_citastecnicas.application;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.application to javafx.fxml;

    exports ni.edu.uam.evaluacion_javafx_citastecnicas.controller;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.controller to javafx.fxml;

    // --- LÍNEAS AGREGADAS PARA SOLUCIONAR EL ERROR DE LA TABLA ---
    exports ni.edu.uam.evaluacion_javafx_citastecnicas.model;
    opens ni.edu.uam.evaluacion_javafx_citastecnicas.model to javafx.base;
}