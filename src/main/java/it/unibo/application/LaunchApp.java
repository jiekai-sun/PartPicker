package it.unibo.application;

import it.unibo.application.controller.Controller;
import it.unibo.application.view.AppGUI;

public class LaunchApp {
    public static void main(String[] args) {
        Controller controller = new Controller();
        AppGUI appGUI = new AppGUI(controller);
        controller.setGUI(appGUI);
    }
}
