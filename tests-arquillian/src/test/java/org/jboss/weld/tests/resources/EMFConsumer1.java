package org.jboss.weld.tests.resources;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/emfconsumer1")
public class EMFConsumer1 extends HttpServlet {

    @Inject
    @ProducedViaStaticFieldOnEJB
    private EntityManagerFactory emf;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (emf == null || emf.createEntityManager() == null) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR);
        }
    }

}
