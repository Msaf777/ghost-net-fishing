package org.saflo.ghostNetFishing.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.inject.Named;

/**
 * Service for managing Flash messages in the GhostNetFishing application.
 */
@Named
@ApplicationScoped
public class FlashMessageService {

    /**
     * Adds a flash message to the current FacesContext.
     *
     * @param summary the summary of the message.
     * @param detail  the detail of the message.
     */
    public void addFlashMessage(FacesMessage.Severity serverityType, String summary, String detail) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if(facesContext != null) {
            Flash flash = facesContext.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(serverityType, summary, detail));
        }
    }
}
