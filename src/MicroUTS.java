import ai.socket.SocketAI;
import rts.*;
import ai.*;
import ai.core.AI;
import rts.units.UnitTypeTable;
import standard.StrategyTactics;
import ai.pvai.PVAIML_ED;
import tests.Experimenter;
import tests.RunConfigurableExperiments;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class MicroUTS {
    static UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL, UnitTypeTable.MOVE_CONFLICT_RESOLUTION_CANCEL_BOTH);
    private static List<AI> bots = new LinkedList<AI>();
    private static List<PhysicalGameState> maps = new LinkedList<PhysicalGameState>();

    public static AI getBotExtended(String botline) {
        String[] tokens = botline.split("\\s");
        String botName = tokens[0];
        switch (botName) {
            case "StrategyTactics":
                try {
                    return new StrategyTactics(utt);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case "PVAIML_ED":
                return new PVAIML_ED(utt);
            case "SocketAI":
                try {
                    String serverIP = tokens[1];
                    int serverPort = Integer.parseInt(tokens[2]);
                    return new SocketAI(100, 100, serverIP, serverPort, SocketAI.LANGUAGE_JSON, utt);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.err.println("SocketAI specification malformed, expected \"SocketAI <server_ip|hostname> <server_port>\", got \"" + botline + "\"");
                    throw new RuntimeException(e);
                }
            default:
                return RunConfigurableExperiments.getBot(botName);
        }
    }

    public static void loadBots(String botFileName, List<AI> botList) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(botFileName), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                try {
                    if (!line.startsWith("#") && !line.isEmpty()) {
                        botList.add(getBotExtended(line));
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public static void loadMaps(String mapFileName, List<PhysicalGameState> mapList) throws IOException {
        if (mapFileName.endsWith(".txt")) {
            try (Stream<String> lines = Files.lines(Paths.get(mapFileName), Charset.defaultCharset())) {
                lines.forEachOrdered(line -> {
                    try {
                        if (!line.startsWith("#") && !line.isEmpty()) {
                            mapList.add(PhysicalGameState.load(line, utt));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        } else if (mapFileName.endsWith(".xml")) {
            try {
                mapList.add(PhysicalGameState.load(mapFileName, utt));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            throw new IllegalArgumentException("Map file name must end in .txt "
                    + "(for a list of maps) or .xml (for a single map).");
        }
    }

    /**
     * Runs bot tests from command line
     * @param args - Command line arguments: configfile resultsfile (traceDir)
     *             where (traceDir) is optional
     *
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String propertyFile = args[0];
        String resultsFile = args[1];

        Properties prop = new Properties();
        InputStream is = new FileInputStream(propertyFile);
        prop.load(is);

        int uttVersion = GameSettings.readIntegerProperty(prop, "UTT_version", 2);
        int conflictPolicy = GameSettings.readIntegerProperty(prop, "conflict_policy", 1);
        utt = new UnitTypeTable(uttVersion, conflictPolicy);

        loadBots(prop.getProperty("bot_file"), bots);
        RunConfigurableExperiments.processBots(bots);

        loadMaps(prop.getProperty("map_file"), maps);

        PrintStream out = new PrintStream(new File(resultsFile));

        int specificAI = GameSettings.readIntegerProperty(prop, "specific_AI", -1);
        int iterations = GameSettings.readIntegerProperty(prop, "iterations", 1);
        int max_cycles = GameSettings.readIntegerProperty(prop, "max_cycles", 3000);
        boolean skip_self_play = Boolean.parseBoolean(prop.getProperty("skip_self_play"));
        boolean partially_observable = Boolean.parseBoolean(prop.getProperty("partially_observable"));


        String traceDir = null;
        boolean saveTrace = false;
        boolean saveZip = false;
        if (args.length >= 3) {
            saveTrace = true;
            saveZip = true;
            traceDir = args[2];
        }

        Experimenter.runExperiments(bots, maps, utt, iterations, max_cycles, 300, false, out,
                specificAI, skip_self_play, partially_observable, saveTrace, saveZip, traceDir);

    }
}