package org.saflo.ghostNetFishing.model.DAO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.primefaces.model.FilterMeta;
import org.saflo.ghostNetFishing.exception.CustomDatabaseException;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.util.DatabaseUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for managing ghostNets in the database.
 */
@Named
@RequestScoped
public class GhostNetDAO implements Serializable {
    private static final Logger logger = Logger.getLogger(GhostNetDAO.class.getName());

    /**
     * Adds a new GhostNet to the database.
     * @param ghostNet the GhostNet entity to be added.
     */
    public void addGhostNet(GhostNet ghostNet) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            logger.info("Starting transaction to add a new ghost-net.");
            em.getTransaction().begin();
            em.persist(ghostNet);
            em.getTransaction().commit();
            logger.info("Transaction committed successfully and ghost-net added.");
        } catch (Exception e) {
            logger.severe("Transaction failed: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.info("Transcation rolled back.");
            }
            throw new CustomDatabaseException("Error adding GhostNet", e);
        } finally {
            em.close();
        }
    }

    /**
     * Finds a GhostNet by its ID.
     * @param id the ID of the GhostNet.
     * @return the found GhostNet entity, or null if not found.
     */
    public GhostNet findGhostNet(Long id) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            logger.info("Attempting to find ghost-net with ID: " + id);
            GhostNet ghostNet = em.find(GhostNet.class, id);
            if (ghostNet == null) {
                logger.warning("No ghost-net found with ID: " + id);
            } else {
                logger.info("Ghost-net found with ID: " + id);
            }
            return ghostNet;
        } catch (Exception e) {
            logger.severe("Error finding ghost-net with ID: " + id + ": " + e.getMessage());
            throw new CustomDatabaseException("Error finding GhostNet", e);
        }
    }

    /**
     * Updates an existing GhostNet in the database.
     * @param ghostNet the GhostNet entity to be updated.
     */
    public void updateGhostNet(GhostNet ghostNet) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
            logger.info("Starting transaction to update an existing ghost-net.");
            em.getTransaction().begin();
            em.merge(ghostNet);
            em.getTransaction().commit();
            logger.info("Transaction committed successfully and ghost-net updated.");
        } catch (Exception e) {
            logger.severe("Transaction failed: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.info("Transaction rolled back.");
            }
            throw new CustomDatabaseException("Error updating GhostNet", e);
        } finally {
            em.close();
        }

    }

    /**
     * Retrieves all GhostNets from the database.
     * @return a list of all GhostNet entities.
     */
    public List<GhostNet> getAllGhostNets() {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            logger.info("Attempting to get all ghost-nets");
            List<GhostNet> ghostNets = DatabaseUtil.getEntityManager().createQuery("SELECT g FROM GhostNet g", GhostNet.class).getResultList();
            if (ghostNets.isEmpty()) {
                logger.warning("No ghost-net found");
            } else {
                logger.info("Ghost-nets found");
            }
            return ghostNets;
        } catch (Exception e) {
            logger.severe("Error finding ghost-nets: " + e.getMessage());
            throw new CustomDatabaseException("Error finding GhostNets", e);
        }
    }

    /**
     * Retrieves filtered GhostNets from the database based on the provided filter criteria.
     * @param filterBy the filter criteria.
     * @return a list of filtered GhostNet entities.
     */
    public List<GhostNet> getFilteredGhostNets(FilterMeta filterBy) {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            logger.info("Attempting to get filtered ghost-nets");
            List<GhostNet> ghostNets = em.createQuery("SELECT g FROM GhostNet g WHERE g.status = :status", GhostNet.class)
                    .setParameter("status", GhostNetStatus.valueOf(filterBy.getFilterValue().toString()))
                    .getResultList();
            if (ghostNets.isEmpty()) {
                logger.warning("No ghost-net found");
            } else {
                logger.info("Ghost-nets found");
            }
            return ghostNets;
        } catch (Exception e) {
            logger.severe("Error finding ghost-nets: " + e.getMessage());
            throw new CustomDatabaseException("Error finding filtered GhostNets", e);
        }
    }

    /**
     * Retrieves available GhostNets from the database that have a status of REPORTED or RECOVERY_PENDING.
     * @return a list of available GhostNet entities.
     */
    public List<GhostNet> getAvailableGhostNets() {
        try (EntityManager em = DatabaseUtil.getEntityManager()) {
            logger.info("Attempting to get available ghost-nets");
            List<GhostNet> ghostNets = em.createQuery("SELECT g FROM GhostNet g WHERE g.status in(:statuses)", GhostNet.class)
                    .setParameter("statuses", Arrays.asList(GhostNetStatus.REPORTED, GhostNetStatus.RECOVERY_PENDING))
                    .getResultList();
            if (ghostNets.isEmpty()) {
                logger.warning("No ghost-net found");
            } else {
                logger.info("Ghost-nets found");
            }
            return ghostNets;
        } catch (Exception e) {
            logger.severe("Error finding ghost-nets: " + e.getMessage());
            throw new CustomDatabaseException("Error finding available GhostNets", e);
        }
    }
}

