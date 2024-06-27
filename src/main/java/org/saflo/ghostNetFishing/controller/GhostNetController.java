package org.saflo.ghostNetFishing.controller;


import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.map.MapModel;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.service.FlashMessageService;
import org.saflo.ghostNetFishing.service.GhostNetService;
import org.saflo.ghostNetFishing.util.SessionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller for managing GhostNets in the GhostNetFishing application.
 * This class is responsible for adding, updating, and filtering GhostNets, as well as managing the map display.
 */
@Named
@ViewScoped
public class GhostNetController implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(GhostNetController.class.getName());

    private static final double MIN_LATITUDE = -90.0000;
    private static final double MAX_LATITUDE = 90.0000;
    private static final double MIN_LONGITUDE = -180.0000;
    private static final double MAX_LONGITUDE = 180.0000;


    private GhostNetService ghostNetService;
    private FlashMessageService flashMessageService;

    @Inject
    public GhostNetController(GhostNetService ghostNetService, FlashMessageService flashMessageService) {
        this.ghostNetService = ghostNetService;
        this.flashMessageService = flashMessageService;
    }

    public GhostNetController(){}

    private Double latitude;
    private Double longitude;
    private int estimatedSize = 1;
    private boolean stayAnonymous;
    private boolean disableSplitButton = false;

    private List<GhostNet> filteredGhostNets;

    private FilterMeta filterBy;
    private final List<String> netStatuses = Arrays.stream(GhostNetStatus.values())
            .map(Enum::name)
            .filter(status -> !status.equals(GhostNetStatus.UNKNOWN.toString()))
            .collect(Collectors.toList());


    private MapModel<Long> simpleModel;

    @PostConstruct
    public void init() {
        populateMapWithGhostNets();

        filteredGhostNets = new ArrayList<>();

        filterBy = FilterMeta.builder()
                .field("status")
                .filterValue(GhostNetStatus.REPORTED)
                .matchMode(MatchMode.EQUALS)
                .build();
    }


    //Getter und Setter

    public double getMIN_LATITUDE() {
        return GhostNetController.MIN_LATITUDE;
    }

    public double getMAX_LATITUDE() {
        return GhostNetController.MAX_LATITUDE;
    }

    public double getMIN_LONGITUDE() {
        return GhostNetController.MIN_LONGITUDE;
    }

    public double getMAX_LONGITUDE() {
        return GhostNetController.MAX_LONGITUDE;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(int estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public boolean isStayAnonymous() {
        return stayAnonymous;
    }

    public void setStayAnonymous(boolean stayAnonymous) {
        this.stayAnonymous = stayAnonymous;
    }

    public FilterMeta getFilterBy() {
        return filterBy;
    }
    public void setFilterBy(FilterMeta filterBy) {
        this.filterBy = filterBy;
    }

    public List<String> getNetStatuses() {
        return netStatuses;
    }

    public MapModel<Long> getSimpleModel() {
        return simpleModel;
    }

    /**
     * Populates the map with GhostNets.
     */
    public void populateMapWithGhostNets() {
        simpleModel = ghostNetService.createMapModel();
    }

    /**
     * Retrieves all GhostNets.
     * @return a list of all GhostNets.
     */
    public List<GhostNet> getGhostNets() {
        return ghostNetService.getAllGhostNets();
    }

    /**
     * @return true if the menu button should be disabled, otherwise false.
     */
    public boolean isDisableMenuButton() {
        return this.disableSplitButton;
    }

    /**
     * @return a list of filtered GhostNets based on the current filter.
     */
    public List<GhostNet> getFilteredGhostNets() {
        LOGGER.info("Fetching filtered ghost nets");
        LOGGER.info("Status: " + filterBy.getFilterValue());
        if (filterBy != null && filterBy.getFilterValue() != null) {
            filteredGhostNets = ghostNetService.getFilteredGhostNets(filterBy);
        }

        return filteredGhostNets;
    }

    public void setFilteredGhostNets(List<GhostNet> filteredGhostNets) {
        this.filteredGhostNets = filteredGhostNets;
    }

    /**
     * Adds a new GhostNet anonymously.
     */
    public void addGhostNetAnonymously() {
        ghostNetService.addGhostNetAnonymously(latitude, longitude, estimatedSize);
        flashMessageService.addFlashMessage(FacesMessage.SEVERITY_INFO, "Netz ist erfolgreich gemeldet", "");
    }

    /**
     * Adds a new GhostNet with the logged-in user's information.
     */
    public void addGhostNet() {
        ghostNetService.addGhostNet(latitude, longitude, estimatedSize, SessionUtil.getLoggedInPerson());
        flashMessageService.addFlashMessage(FacesMessage.SEVERITY_INFO, "Netz ist erfolgreich gemeldet", "");
    }

    /**
     * Handles adding a new GhostNet based on the user's login status and anonymity preference.
     * @return the target page after adding the GhostNet.
     */
    public String handleAddGhostNet() {
        if (!SessionUtil.isLoggedIn()) {
            addGhostNetAnonymously();
            return "login?faces-redirect=true";
        }

        Person loggedInPerson = SessionUtil.getLoggedInPerson();
        boolean isReporterAndAnonymous = loggedInPerson.getType().equals(PersonType.REPORTER) && isStayAnonymous();

        if (isReporterAndAnonymous) {
            addGhostNetAnonymously();
        } else {
            addGhostNet();
        }
        return "listGhostNets?faces-redirect=true";
    }

    /**
     * Updates the status of a GhostNet.
     * @param ghostNetId the ID of the GhostNet.
     * @param newStatus the new status of the GhostNet.
     */
    public void updateStatus(Long ghostNetId, String newStatus) {
        ghostNetService.updateGhostNetStatus(ghostNetId, GhostNetStatus.valueOf(newStatus), SessionUtil.getLoggedInPerson());
    }

    /**
     * Checks if the current user is authorized to set the status of a GhostNet to "RECOVERY_PENDING".
     * @param ghostNet the GhostNet.
     * @return true if the user is authorized, otherwise false.
     */
    public boolean isReportRecoveryPendingAllowed(GhostNet ghostNet) {
        return ghostNet.getStatus() == GhostNetStatus.REPORTED &&
                SessionUtil.getLoggedInPerson().getType() == PersonType.RECOVERER;
    }

    /**
     * Checks if the current user is authorized to set the status of a GhostNet to "RECOVERED".
     * @param ghostNet the GhostNet.
     * @return true if the user is authorized, otherwise false.
     */
    public boolean isReportRecoveredAllowed(GhostNet ghostNet) {
        return ghostNet.getStatus() == GhostNetStatus.RECOVERY_PENDING &&
                SessionUtil.getLoggedInPerson().getType() == PersonType.RECOVERER;
    }

    /**
     * Checks if the current user is authorized to set the status of a GhostNet to "LOST".
     * @param ghostNet the GhostNet.
     * @return true if the user is authorized, otherwise false.
     */
    public boolean isReportLostAllowed(GhostNet ghostNet) {
        return ghostNet.getStatus() != GhostNetStatus.LOST && ghostNet.getStatus() != GhostNetStatus.RECOVERED;
    }

    /**
     * Returns the status of a GhostNet in German.
     * @param status the status of the GhostNet.
     * @return the status in German.
     */
    public String getStatusInGerman(GhostNetStatus status) {
        return GhostNetStatus.fromStatus(status).getGermanStatus();
    }

    /**
     * Checks the current filter and updates the status of the split button based on the filter value.
     */
    public void toggleSplitButton() {
        if (filterBy.getFilterValue() != null) {
            String filterValue = filterBy.getFilterValue().toString();
            disableSplitButton = filterValue.equals("LOST") || filterValue.equals("RECOVERED");
        } else {
            disableSplitButton = false;
        }
    }

}