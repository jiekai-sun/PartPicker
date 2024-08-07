package it.unibo.application.controller;

import java.util.List;

import it.unibo.application.data.entities.ban.Ban;
import it.unibo.application.data.entities.builds.Build;
import it.unibo.application.data.entities.builds.Review;
import it.unibo.application.data.entities.compatibility.ComponentCompatibilityChecker;
import it.unibo.application.data.entities.components.Component;
import it.unibo.application.data.entities.components.Manufacturer;
import it.unibo.application.data.entities.enums.Part;
import it.unibo.application.data.entities.enums.State;
import it.unibo.application.data.entities.login.User;
import it.unibo.application.data.entities.login.UserDetails;
import it.unibo.application.data.entities.price.ComponentPrice;
import it.unibo.application.model.Model;
import it.unibo.application.model.states.AppStateController;
import it.unibo.application.view.View;

public class Controller {
    private final Model model;
    private final AppStateController appStateController;
    private final View view;

    public Controller(final Model model, final View view) {
        this.model = model;
        this.view = view;
        this.appStateController = new AppStateController();
    }

    public void setAppState(final State newState) {
        appStateController.setState(newState);
        view.switchPanel(newState);
    }

    public State getAppState() {
        return appStateController.getState();
    }

    public Part getDesiredPart() {
        return appStateController.getRequestedPart();
    }


    public void setDesiredPart(final Part part) {
        appStateController.setRequestedPart(part);
    }

    public int getTargetBuild() {
        return appStateController.getTargetBuild();
    }

    public void setTargetBuild(final int id) {
        appStateController.setTargetBuild(id);
    }

    public void loginAttempt(final String username, final String password) {
        if (model.login(username, password)) {
            view.showDialog("Login succesful");
            view.switchPanel(State.OVERVIEW);
        } else {
            view.showDialog("Login failed");
        }
    }

    public boolean registerUser(final User user) {
        return model.registerUser(user);
    }

    public User getLoggedUser() {
        return model.getLoggedUser();
    }

    public List<Component> getComponents(final Part part) {
        return model.getComponents(part);
    }

    public List<Build> getBuilds() {
        return model.getBuilds();
    }

    public Build findBuildById(final int id) {
        return model.getBuildById(id);
    }

    public UserDetails getUserDetails(final String username) {
        return model.getUserDetails(username);
    }

    public void banUser(final Ban ban) {
       model.banUser(ban);
    }

    public List<Review> getReviewsByBuild(final int buildId) {
        return model.getReviewsByBuild(buildId);
    }

    public void insertReview(final Review review) {
        model.insertReview(review);
    }

    public void updateReview(final Review review) {
        model.updateReview(review);
    }

    public int getLatestBuildId() {
        return model.getLatestBuildId();
    }

    public void insertBuild(Build build, User user) {
        model.insertBuild(build, user);
    }

    public ComponentPrice getScrapedPrice(final int componentId) {
        return model.getScrapedPrice(componentId);
    }

    public List<ComponentPrice> getRecentComponentPricesByReseller(final String reseller, final int componentId) {
        return model.getRecentComponentPricesByReseller(componentId, reseller);
    }

    public ComponentCompatibilityChecker getCCC() {
        return model.getComponentCompatibilityChecker();
    }

    public Manufacturer getManufacturerByName(final String name) {
        return model.getManufacturerByName(name);
    }
}
