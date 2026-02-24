package org.acme;

import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.discoveryRequest;

public class Main {

    public static void main(String[] args) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        FacadeClassLoader facadeClassLoader = new FacadeClassLoader(classLoader);
        LauncherDiscoveryRequest discoveryRequest = discoveryRequest()
                .selectors(selectClass(facadeClassLoader, "org.acme.SuspendTest"))
                .build();

        try (LauncherSession session = LauncherFactory.openSession()) {
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            session.getLauncher().execute(discoveryRequest, listener);
            listener.getSummary().printTo(new PrintWriter(System.out));
        }

    }
}