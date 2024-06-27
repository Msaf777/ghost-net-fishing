package org.saflo.ghostNetFishing.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.saflo.ghostNetFishing.model.DAO.GhostNetDAO;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service for managing GhostNets in the GhostNetFishing application.
 */
@Named
@ApplicationScoped
public class GhostNetService {

    private static final Logger LOGGER = Logger.getLogger(GhostNetService.class.getName());


    private GhostNetDAO ghostNetDAO;

    @Inject
    public GhostNetService(GhostNetDAO ghostNetDAO) {
        this.ghostNetDAO = ghostNetDAO;
    }

    public GhostNetService(){}

    /**
     * Populates the map with GhostNets and sets markers based on their status.
     * @return a MapModel containing the GhostNets.
     */
    public MapModel<Long> createMapModel() {
        MapModel<Long> mapModel = new DefaultMapModel<>();
        List<GhostNet> ghostNets = ghostNetDAO.getAvailableGhostNets();
        for (GhostNet ghostNet : ghostNets) {
            mapModel.addOverlay(new Marker<>(new LatLng(
                    ghostNet.getLatitude(),
                    ghostNet.getLongitude()),
                    ghostNet.getEstimatedSize() + "m²", ghostNet.getId(),
                    ghostNet.getStatus() == GhostNetStatus.REPORTED ?
                            "https://maps.google.com/mapfiles/ms/micons/blue-dot.png" :
                            "https://maps.google.com/mapfiles/ms/micons/pink-dot.png"));
        }
        return mapModel;
    }

    /**
     * @return a list of all GhostNets.
     */
    public List<GhostNet> getAllGhostNets() {
        LOGGER.info("Fetching all ghost nets");
        return ghostNetDAO.getAllGhostNets();
    }

    /**
     * Retrieves a list of filtered GhostNets based on the filter criteria.
     * @param filterBy the filter criteria.
     * @return a list of filtered GhostNets.
     */
    public List<GhostNet> getFilteredGhostNets(FilterMeta filterBy) {
        LOGGER.info("Fetching filtered ghost nets");
        LOGGER.info("Status: " + filterBy.getFilterValue());
        return ghostNetDAO.getFilteredGhostNets(filterBy);
    }

    /**
     * Adds a new GhostNet anonymously.
     * @param latitude the latitude of the GhostNet.
     * @param longitude the longitude of the GhostNet.
     * @param estimatedSize the estimated size of the GhostNet.
     */
    public void addGhostNetAnonymously(Double latitude, Double longitude, int estimatedSize) {
        GhostNet newGhostNet = new GhostNet(latitude, longitude, estimatedSize);
        LOGGER.info("Adding a new ghost net anonymously");
        ghostNetDAO.addGhostNet(newGhostNet);
        LOGGER.info("Ghost net added and reset");
    }

    /**
     * Adds a new GhostNet with the logged-in user's information.
     * @param latitude the latitude of the GhostNet.
     * @param longitude the longitude of the GhostNet.
     * @param estimatedSize the estimated size of the GhostNet.
     * @param person the person who reported the GhostNet.
     */
    public void addGhostNet(Double latitude, Double longitude, int estimatedSize, Person person) {
        GhostNet newGhostNet = new GhostNet(latitude, longitude, estimatedSize, person);
        LOGGER.info("Adding a new ghost net with user information");
        ghostNetDAO.addGhostNet(newGhostNet);
        LOGGER.info("Ghost net added and reset");
    }

    /**
     * Updates the status of a GhostNet based on the given ID and new status.
     * @param ghostNetId the ID of the GhostNet.
     * @param newStatus the new status of the GhostNet.
     * @param person the person performing the update.
     */
    public void updateGhostNetStatus(Long ghostNetId, GhostNetStatus newStatus, Person person) {
        GhostNet updatedGhostNet = ghostNetDAO.findGhostNet(ghostNetId);
        if (updatedGhostNet != null) {
            LOGGER.info("Updating status for net ID: " + ghostNetId);
            updatedGhostNet.setStatus(newStatus);
            switch (newStatus) {
                case RECOVERY_PENDING:
                    updatedGhostNet.setRecoveryPendingBy(person);
                    break;
                case RECOVERED:
                    updatedGhostNet.setRecoveredBy(person);
                    break;
                case LOST:
                    updatedGhostNet.setLosingReportedBy(person);
                    break;
                default:
                    break;
            }
            ghostNetDAO.updateGhostNet(updatedGhostNet);
            LOGGER.info("Status updated to " + newStatus);
        } else {
            LOGGER.warning("No ghost net found with ID: " + ghostNetId);
        }
    }
}
