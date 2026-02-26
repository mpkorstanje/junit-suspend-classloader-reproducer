package org.acme;

import org.junit.platform.engine.DiscoveryIssue;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.LauncherDiscoveryListener;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.discoveryRequest;

/*

    How to run:

    1. Run mvn clean test-compile
    2. Change MAVEN_HOME to the right directory.
    3. Run main.
    4. Observe: Warning
    5. Add Kotlin to runtime classpath (in pom.xml)
    6. Run main.
    7. Observe: No warning. Both tests logged.

 */
public class Main {

    public static final String MAVEN_HOME = "/home/mpkorstanje/.m2/";

public static void main(String[] args) throws MalformedURLException {
    var classLoader = Thread.currentThread().getContextClassLoader();
    var testClassLoader = new URLClassLoader(getTestClasspathURLs(), classLoader);
    var discoveryRequest = discoveryRequest()
            .selectors(selectClass(testClassLoader, "org.acme.SuspendTest"))
            .build();

    try (LauncherSession session = LauncherFactory.openSession()) {
        var launcher = session.getLauncher();
        launcher.registerLauncherDiscoveryListeners(new IssueReporter());
        TestPlan testPlan = launcher.discover(discoveryRequest);
        testPlan.getRoots().forEach(testIdentifier -> testPlan.getDescendants(testIdentifier).stream()
                .map(TestIdentifier::getDisplayName)
                .forEach(System.out::println));
    }

}

    private static java.net.URL[] getTestClasspathURLs() throws MalformedURLException {
        return new URL[]{
                new File("target/test-classes").toURL(),
                new File(MAVEN_HOME + "repository/org/jetbrains/kotlin/kotlin-stdlib/2.3.0/kotlin-stdlib-2.3.0.jar").toURL(),
                new File(MAVEN_HOME + "repository/org/jetbrains/annotations/13.0/annotations-13.0.jar").toURL(),
                new File(MAVEN_HOME + "repository/org/jetbrains/kotlinx/kotlinx-coroutines-core/1.10.2/kotlinx-coroutines-core-1.10.2.jar").toURL(),
                new File(MAVEN_HOME + "repository/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.10.2/kotlinx-coroutines-core-jvm-1.10.2.jar").toURL(),
                new File(MAVEN_HOME + "repository/org/jetbrains/kotlin/kotlin-reflect/2.3.0/kotlin-reflect-2.3.0.jar").toURL(),
        };
    }

    private static class IssueReporter implements LauncherDiscoveryListener {
        @Override
        public void issueEncountered(UniqueId engineId, DiscoveryIssue issue) {
            System.out.println(issue.message());
        }
    }
}