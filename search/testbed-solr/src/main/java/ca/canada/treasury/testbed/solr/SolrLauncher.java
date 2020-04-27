package ca.canada.treasury.testbed.solr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility wrapper around {@link SolrStandAloneServer} that helps starting
 * Solr with different options.
 * @author Pascal Essiembre
 */
public class SolrLauncher {

    private static final Logger LOG =
            LoggerFactory.getLogger(SolrLauncher.class);

    public static final String ARG_SOURCE_HOME = "sourceHome";
    public static final String ARG_TARGET_HOME = "targetHome";
    public static final String ARG_PORT = "port";
    public static final String ARG_CLEAN = "clean";

    private File sourceHomeDir;
    private File targetHomeDir;
    private int port;
    private boolean clean;

    public File getSourceHomeDir() {
        return sourceHomeDir;
    }
    public void setSourceHomeDir(File sourceHomeDir) {
        this.sourceHomeDir = sourceHomeDir;
    }

    public File getTargetHomeDir() {
        return targetHomeDir;
    }
    public void setTargetHomeDir(File targetHomeDir) {
        this.targetHomeDir = targetHomeDir;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public boolean isClean() {
        return clean;
    }
    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public SolrStandAloneServer start() throws Exception {
        validateLaunchArgs();

        prepareTargetSolrHome();

        SolrStandAloneServer solr =
                new SolrStandAloneServer(targetHomeDir.getAbsolutePath(), port);
        solr.start();

        String solrBaseURL = solr.getBaseURL();

        LOG.info("Solr is ready to use at: {}", solrBaseURL);

        LOG.info("Example(s):");
        Arrays.asList(targetHomeDir.listFiles()).forEach(f -> {
            if (f.isDirectory() && f.list().length > 0) {
                LOG.info("    {}/{}/select?q=*:*", solrBaseURL, f.getName());
            }
        });
        return solr;
    }

    private void validateLaunchArgs() {
        if (targetHomeDir == null) {
            throw new IllegalStateException(
                    "Target home directory cannot be null");
        }
        if (getSourceHomeDir() != null && !getSourceHomeDir().isDirectory()) {
            throw new IllegalStateException(
                    "Source home directory is invalid: "
                    + getSourceHomeDir().getAbsolutePath());
        }
        if (!targetHomeDir.exists() && sourceHomeDir == null) {
            throw new IllegalStateException(
                    "Target home directory must exists or source home "
                            + "directory must be set.");
        }
    }

    private void prepareTargetSolrHome() throws IOException {
        if (clean) {
            if (sourceHomeDir != null) {
                LOG.info("DELETING  existing Solr target home directory.");
                FileUtils.deleteDirectory(targetHomeDir);
            } else {
                LOG.info("DELETING  all \"data\" directories under "
                        + "Solr target home directory.");
                for(Iterator<File> it = FileUtils.iterateFilesAndDirs(
                        targetHomeDir, FalseFileFilter.INSTANCE,
                        TrueFileFilter.INSTANCE); it.hasNext();) {
                    File dir = it.next();
                    if ("data".equals(dir.getName())) {
                        LOG.info("    Deleting {}", dir.getAbsolutePath());
                        FileUtils.deleteDirectory(dir);
                    }
                }
            }
        }

        if (!targetHomeDir.exists()) {
            LOG.info("CREATING  new Solr target home directory from source: {}",
                    sourceHomeDir);
            FileUtils.copyDirectory(sourceHomeDir, targetHomeDir);
            modifyConfigsForEmbedded();
        } else {
            LOG.info("REUSING   existing Solr target home directory.");
        }
    }

    private void modifyConfigsForEmbedded() {
        LOG.info("MODIFYING Solr target home config files for embedded use.");

        // Library dependencies are in class path (Maven dependencies)
        // so we remove them from solrconfig.xml to eliminate the many
        // log warnings on startup.
        forEachTargetFile("solrconfig\\.xml",
            p -> replaceAll(p.toFile(), "(?si)(<lib.*?(/>|</lib>))", ""));
    }

    private void replaceAll(File file, String regex, String replacement) {
        try {
            String text = FileUtils.readFileToString(
                    file, StandardCharsets.UTF_8);
            text = text.replaceAll(regex, replacement);
            FileUtils.writeStringToFile(file, text, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Could replace in file \"{}\". Regex: {} Replacement: {}",
                    file, regex, replacement, e);
        }
    }
    private void forEachTargetFile(
            String fileRegex, Consumer<? super Path> action) {
        try (Stream<Path> stream = Files.walk(targetHomeDir.toPath(), 3)) {
            stream.filter(p -> p.getFileName().toString().matches(
                    fileRegex)).forEach(action);
        } catch (IOException e) {
            LOG.error("Could not disable solrconfig.xml lib loading.", e);
        }
    }

    public static void main(String[] args) throws Exception {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder(ARG_SOURCE_HOME).desc(
                "(Optional) Source directory to be copied into target "
                        + "directory.")
                .hasArg().build());
        options.addOption(Option.builder(ARG_TARGET_HOME).desc(
                "(Required) Solr home directory. Must exist unless "
                        + "\"" + ARG_SOURCE_HOME + "\" is specified.")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_PORT).desc(
                "(Optional) Solr port (defaults to a random available port).")
                .hasArg().build());
        options.addOption(Option.builder(ARG_CLEAN).desc(
                "(Optional) Deletes entire target home if \"" + ARG_SOURCE_HOME
                        + "\" is specified. Otherwise delete "
                        + "all \"data\" dirs from target home.")
                .build());

        SolrLauncher launcher = new SolrLauncher();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(ARG_SOURCE_HOME)) {
                launcher.setSourceHomeDir(
                        new File(cmd.getOptionValue(ARG_SOURCE_HOME)));
            }
            launcher.setTargetHomeDir(
                    new File(cmd.getOptionValue(ARG_TARGET_HOME)));
            if (cmd.hasOption(ARG_PORT)) {
                launcher.setPort(
                        Integer.parseInt(cmd.getOptionValue(ARG_PORT)));
            }
            launcher.setClean(cmd.hasOption(ARG_CLEAN));
        } catch (ParseException exp) {
            LOG.error("Cannot start Solr. {}", exp.getMessage());
            new HelpFormatter().printHelp("SolrLuncher", options);
        }

        try {
            launcher.start();
        } catch (Throwable t) {
            LOG.error("Cannot start Solr.", t);
            System.exit(-1);
        }
    }
}
