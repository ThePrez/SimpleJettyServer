package com.github.theprez.simplejetty;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class SimpleJettyServer {
  public static void main(String[] _args) {

    final LinkedList<String> args = new LinkedList<String>();
    args.addAll(Arrays.asList(_args));
    String portEnvVar = System.getenv("PORT");
    String root = getJarLocation();

    if (portEnvVar != null) {
      System.setProperty("jetty.port", portEnvVar);
    } else {
      System.setProperty("jetty.port", "80");
    }

    for (final String arg : args) {
      if (arg.toLowerCase().startsWith("--port=")) {
        System.setProperty("jetty.port", arg.replaceFirst(".*=", "").trim());
      } else if (arg.toLowerCase().startsWith("--root=")) {
        root = arg.replaceFirst(".*=", "").trim();
        ;
      } else if (arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help")) {
        printUsageAndExit();
      } else {
        System.err.println("WARNING: Argument '" + arg + "' unrecognized");
        printUsageAndExit();
      }
    }
    if (null == root) {
      System.err.println("ERROR: unable to determine web root");
      System.exit(1);
    }
    int port = 0;
    try {
      port = Integer.valueOf(System.getProperty("jetty.port"));
    } catch (NumberFormatException e) {
      System.err.println("ERROR: unable to parse port number");
      printUsageAndExit();
    }
    System.out.println("Starting jetty on port " + port + " with root " + root);
    Server server = new Server(port);

    ResourceHandler staticFilesHandler = new ResourceHandler();
    staticFilesHandler.setDirectoriesListed(true); //TODO: make configurable
    staticFilesHandler.setWelcomeFiles(new String[] { "index.html" });

    staticFilesHandler.setResourceBase(root);

    server.setHandler(staticFilesHandler);
    try {
      server.start();
      server.join();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static String getJarLocation() {
    try {
      URL url = SimpleJettyServer.class.getProtectionDomain().getCodeSource().getLocation();
      return new java.io.File(url.toURI()).getParentFile().getAbsolutePath();
    } catch (Exception e) {
      return null;
    }
  }

  private static void printUsageAndExit() {
    // @formatter:off
    final String usage = "Usage: java -jar simplejettyserver.jar [options]\n"
                            + "\n"
                            + "    Valid options include:\n"
                            + "        --port=<port>>: port to listen on\n"  + "\n"
                            + "                        (default is 80)\n"  + "\n"
                            + "        --root=<path>>: path on filesystem to serve\n"  + "\n"
                            + "                        (default is directory with .jar file)\n"  + "\n"
                            ;
    // @formatter:on
    System.err.println(usage);
    System.exit(-1);
  }
}
