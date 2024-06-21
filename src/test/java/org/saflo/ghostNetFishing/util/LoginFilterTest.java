package org.saflo.ghostNetFishing.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private LoginFilter loginFilter;

    @BeforeEach
    public void setUp() {
        when(request.getContextPath()).thenReturn("");
    }


    // Wenn ein eingeloggter Benutzer die Login-Seite aufruft, wird er zur Startseite weitergeleitet
    @Test
    public void whenLoggedInUserAccessesLogin_thenRedirectToHome() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/login.xhtml");


        try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
            sessionUtilMock.when(SessionUtil::isLoggedIn).thenReturn(true);

            loginFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/home.xhtml");
            verify(chain, never()).doFilter(request, response);
        }
    }

    // Wenn ein nicht eingeloggter Benutzer eine geschützte Seite aufruft, wird er zur Login-Seite weitergeleitet
    @Test
    public void whenNotLoggedInUserAccessesProtectedPage_thenRedirectToLogin() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home.xhtml");
        try(MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
            sessionUtilMock.when(SessionUtil::isLoggedIn).thenReturn(false);

            loginFilter.doFilter(request, response, chain);

            verify(response).sendRedirect("/login.xhtml");
            verify(chain, never()).doFilter(request, response);
        }
    }

    // Wenn ein nicht eingeloggter Benutzer die Login-Seite aufruft, wird die Filterkette fortgesetzt
    @Test
    public void whenNotLoggedInUserAccessesLogin_thenChainContinues() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/login.xhtml");

        try (MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {
            sessionUtilMock.when(SessionUtil::isLoggedIn).thenReturn(false);
            loginFilter.doFilter(request, response, chain);

            verify(response, never()).sendRedirect(anyString());
            verify(chain).doFilter(request, response);
        }
    }

    // Wenn ein eingeloggter Benutzer eine geschützte Seite aufruft, wird die Filterkette fortgesetzt
    @Test
    public void whenLoggedInUserAccessesProtectedPage_thenChainContinues() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/home.xhtml");

        try(MockedStatic<SessionUtil> sessionUtilMock = mockStatic(SessionUtil.class)) {

            sessionUtilMock.when(SessionUtil::isLoggedIn).thenReturn(true);

            loginFilter.doFilter(request, response, chain);

            verify(response, never()).sendRedirect(anyString());
            verify(chain).doFilter(request, response);
        }

    }

}