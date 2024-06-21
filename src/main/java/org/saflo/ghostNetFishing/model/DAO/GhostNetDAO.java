package org.saflo.ghostNetFishing.model.DAO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.primefaces.model.FilterMeta;
import org.saflo.ghostNetFishing.model.Entity.GhostNet;
import org.saflo.ghostNetFishing.model.enums.GhostNetStatus;
import org.saflo.ghostNetFishing.util.DatabaseUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Named
@RequestScoped
public class GhostNetDAO implements Serializable {
    private static final Logger logger = Logger.getLogger(GhostNetDAO.class.getName());


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
                throw e;
            }
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to add a new ghost-net");
        }
    }

    public GhostNet findGhostNet(Long id) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
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
            throw e; // Optional: re-throwing the exception might depend on how you want to handle errors at a higher level.
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to find ghost-net with ID: " + id);
        }
    }


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
            throw e;
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to update ghost-net with ID: " + ghostNet.getId());
        }

    }

    public List<GhostNet> getAllGhostNets() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
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
            throw e; // Optional: re-throwing the exception might depend on how you want to handle errors at a higher level.
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to find all ghost-nets ");
        }
    }

    public List<GhostNet> getFilteredGhostNets(FilterMeta filterBy) {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
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
            throw e; // Optional: re-throwing the exception might depend on how you want to handle errors at a higher level.
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to find filtered ghost-nets ");
        }
    }
    public List<GhostNet> getAvailableGhostNets() {
        EntityManager em = DatabaseUtil.getEntityManager();
        try {
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
            throw e; // Optional: re-throwing the exception might depend on how you want to handle errors at a higher level.
        } finally {
            em.close();
            logger.info("EntityManager closed after attempting to find available ghost-nets ");

        }
    }
}

