module org.example.projetjavahbmcm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.projetjavahbmcm to javafx.fxml;
    exports org.example.projetjavahbmcm;
    exports org.example.projetjavahbmcm.controller;
    opens org.example.projetjavahbmcm.controller to javafx.fxml;

    exports org.example.projetjavahbmcm.model;
    opens org.example.projetjavahbmcm.model to javafx.base;

    requires java.sql;
    requires org.xerial.sqlitejdbc;


}