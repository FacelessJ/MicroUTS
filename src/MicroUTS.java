import rts.*;
import ai.*;
import ai.core.AI;
import rts.units.UnitTypeTable;
import standard.StrategyTactics;
import ai.pvai.PVAIML_ED;
import tests.Experimenter;
import tests.RunConfigurableExperiments;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class MicroUTS {
    static UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED, UnitTypeTable.MOVE_CONFLICT_RESOLUTION_CANCEL_BOTH);
    private static List<AI> bots = new LinkedList<AI>();
    private static List<PhysicalGameState> maps = new LinkedList<PhysicalGameState>();

    public static AI getBotExtended(String botName) {
        switch (botName) {
            case "StrategyTactics":
                try {
                    return new StrategyTactics(utt);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case "PVAIML_ED":
                return new PVAIML_ED(utt);
            /** Todo: Add socket AI */
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
     * @param args - Command line arguments: botfile mapsfile resultsfile iterations (specificAI) (traceDir)
     *             where (specificAI) tells the experimenter to ignore all runs not involving the chosen AI
     *             (indexed according to botfile). Set to -1 to use all runs if want to set trace output as well.
     *
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String botfile = args[0];
        String mapsfile = args[1];
        String resultsfile = args[2];
        int iterations = Integer.parseInt(args[3]);

        loadBots(botfile, bots);
        RunConfigurableExperiments.processBots(bots);

        loadMaps(mapsfile, maps);

        PrintStream out = new PrintStream(new File(resultsfile));

        int specificAI = -1;
        if(args.length >= 5) {
            specificAI = Integer.parseInt(args[4]);
        }

        String traceDir = null;
        boolean saveTrace = false;
        boolean saveZip = false;
        if (args.length >= 6) {
            saveTrace = true;
            saveZip = true;
            traceDir = args[5];
        }

        Experimenter.runExperiments(bots, maps, utt, iterations, 3000, 300, false, out,
                specificAI, true, false, saveTrace, saveZip, traceDir);

    }
}