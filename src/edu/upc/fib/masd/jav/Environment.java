package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.utils.Field;
import edu.upc.fib.masd.jav.utils.FieldState;
import sml.Identifier;
import sml.Kernel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Environment {
    // CONFIG
    public static  int startNumBarons = 1;
    public static  int startNumCollectors = 3;
    public static  int startNumBuilders = 1;
    public static  int numFieldsEachCollector = 3;

    public static  int startFood = 5;
    public static  int startFoodSatiety = 15;
    public static  int maxFood = 5;

    public static  int startWood = 2;
    public static  int woodRequiredToBuild = 5;

    public static  int giveValue = 2;

    public static  int startYield = 2;
    public static  int minYield = 1;
    public static  int sownRounds = 5;
    public static  int increaseYieldRounds = 7;

    // We keep references to Agents.
    private ArrayList<GeneralAgent> agents;
    // Create executor services to run Soar in since it blocks.
    private ArrayList<ExecutorService> executors;
    // To read user input
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    private static final Environment instance = new Environment();

    private Environment() {
    }

    public static Environment getInstance() {
        return instance;
    }

    public void setAgents(ArrayList<GeneralAgent> agents) {
        this.agents = agents;
        this.executors = instance.initExecutors();
    }

    public void runSystemStep() {
        clearWMEOutputs();
        runAllAgentsOneStep();
        updateEnvironmentState();
        readAndTreatAllAgentsOutputs();
        updateGUI();
        System.out.println("===========================================");
        // Necessary delay (ms)
        delay(1000);
    }

    private void runAllAgentsOneStep() {
        for (GeneralAgent agent : agents) {
            agent.runStep();
        }
    }

    private void readAndTreatAllAgentsOutputs() {
        for (GeneralAgent a : agents) {
            a.readAndTreatOutput();
        }
    }

    private void updateEnvironmentState() {
        for (GeneralAgent agent : this.agents) {
            // Update agents
            agent.decreaseSatiety();
            // Update fields
            if (agent instanceof CollectorAgent) {
                for (Map.Entry<String, Field> field : ((CollectorAgent) agent).getFields().entrySet()) {
                    field.getValue().update();
                }
            }
        }
    }

    public void updateGUI() {
        for (GeneralAgent agent : this.agents) {
            agent.updateInfoGUI();
        }
        GUI.refresh();
    }

    private void clearWMEOutputs() {
        for (GeneralAgent a : agents) {
            a.clearOutput();
        }
    }

    private ArrayList<ExecutorService> initExecutors() {
        ArrayList<ExecutorService> exec = new ArrayList<ExecutorService>();
        for (int i = 0; i < this.agents.size(); ++i) {
            exec.add(Executors.newSingleThreadExecutor());
        }
        return exec;
    }

    public void shutdown() {
        // Shutdown the Soar interface and the executor service.
        for (int i = 0; i < this.agents.size(); ++i) {
            this.agents.get(i).shutdown();
            this.executors.get(i).shutdown();
        }
        System.exit(0);
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<GeneralAgent> createAgents(Kernel kernel) {
        ArrayList<GeneralAgent> allAgents = new ArrayList<>();

        // Barons
        for (int i = 0; i < Environment.startNumBarons; ++i) {
            BaronAgent baron = new BaronAgent(kernel, String.format("Baron_%d", i), "SOAR_Codes/PRESET_baron_agent.soar");
            allAgents.add(baron);

            // Collectors
            for (int j = 0; j < Environment.startNumCollectors; ++j) {
                CollectorAgent collector = new CollectorAgent(kernel, "Collector_" + j, "SOAR_Codes/PRESET_collector_agent.soar", baron);
                baron.addVillager(collector);
                allAgents.add(collector);
            }

            // Builders
            for (int j = 0; j < Environment.startNumBuilders; ++j) {
                BuilderAgent builder = new BuilderAgent(kernel, String.format("Builder_%d", j), "SOAR_Codes/PRESET_builder_agent.soar", baron);
                baron.addVillager(builder);
                allAgents.add(builder);
            }
        }
        return allAgents;
    }
}

