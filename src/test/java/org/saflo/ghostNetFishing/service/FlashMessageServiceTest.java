package org.saflo.ghostNetFishing.service;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
* unit tests for the {@link FlashMessageService} class.
* */
@ExtendWith(MockitoExtension.class)
class FlashMessageServiceTest {
    @InjectMocks
    private FlashMessageService flashMessageService;
    @Mock
    private FacesContext facesContext;
    @Mock
    private ExternalContext externalContext;
    @Mock
    private Flash flash;

    private MockedStatic<FacesContext> facesContextStatic;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        flashMessageService = new FlashMessageService();

        facesContextStatic = Mockito.mockStatic(FacesContext.class);
        facesContextStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getFlash()).thenReturn(flash);
    }

    /**
     * Cleans up the test environment after each test.
     */
    @AfterEach
    void tearDown() {
        facesContextStatic.close();
    }

    /**
     * Tests that a flash message is added when {@code addFlashMessage} is called.
     */
    @Test
    void whenAddFlashMessage_thenFlashMessageIsAdded() {
        FacesMessage.Severity severity = FacesMessage.SEVERITY_INFO;
        String summary = "Test Summary";
        String detail = "Test Detail";

        flashMessageService.addFlashMessage(severity, summary, detail);

        verify(flash).setKeepMessages(true);

        // with ArgumentCaptor
 /*
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());

        FacesMessage capturedMessage = messageCaptor.getValue();
        assertEquals(severity, capturedMessage.getSeverity());
        assertEquals(summary, capturedMessage.getSummary());
        assertEquals(detail, capturedMessage.getDetail())
        */;

        // with Matchers
        verify(facesContext).addMessage(eq(null), argThat(message ->
                message.getSeverity() == severity &&
                        "Test Summary".equals(message.getSummary()) &&
                        "Test Detail".equals(message.getDetail())
        ));
    }
}