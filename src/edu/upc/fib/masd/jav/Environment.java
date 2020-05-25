package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.utils.Field;
import sml.Kernel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Environment {
    // CONFIG
    public static int startNumBarons = 1;
    public static int startNumCollectors = 4;
    public static int startNumBuilders = 0;
    public static int numFieldsEachCollector = 3;

    public static int startFood = 5;
    public static int startFoodSatiety = 15;
    public static int maxFood = 5;
    public static int maxBaronFood = 20;

    public static int startWood = 0;
    public static int maxWood = 5;
    public static int maxBaronWood = 20;
    public static int woodRequiredToBuild = 5;

    public static int giveValue = 2;

    public static int startYield = 2;
    public static int minYield = 1;
    public static int sownRounds = 5;
    public static int increaseYieldRounds = 7;

    // We keep references to Agents.
    private Map<String, GeneralAgent> agents;
    // Create executor services to run Soar in since it blocks.
    private Map<String, ExecutorService> executors;
    // To read user input
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    private int kernelPort;

    private static final Environment instance = new Environment();

    private Environment() {
    }

    public static Environment getInstance() {
        return instance;
    }

    public void setAgents(Map<String, GeneralAgent> agents) {
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
        for (String agentId : agents.keySet()) {
            agents.get(agentId).runStep();
        }
    }

    private void readAndTreatAllAgentsOutputs() {
        for (String agentId : agents.keySet()) {
            agents.get(agentId).readAndTreatOutput();
        }
    }

    private void updateEnvironmentState() {
        for (String agentId : agents.keySet()) {
            // Update agents
            agents.get(agentId).decreaseSatiety();
            // Update fields
            if (agents.get(agentId) instanceof CollectorAgent) {
                for (Map.Entry<String, Field> field : ((CollectorAgent) agents.get(agentId)).getFields().entrySet()) {
                    field.getValue().update();
                }
            }
        }
    }

    public void addCollector(Kernel k, BaronAgent baron) {
        String collectorId = NameSampler.getInstance().sampleVillagerName();
        CollectorAgent collector = new CollectorAgent(k, collectorId, baron);
        baron.addVillager(collector);
        agents.put(collectorId, collector);
        System.out.println("Agent " + collectorId + "has born");
    }

    public void changeAgentProfession(Kernel k, BaronAgent baron, String agentId, int food, int foodSatiety, int wood) {
        if (agents.get(agentId) instanceof BuilderAgent) {
            agents.get(agentId).kill();
            CollectorAgent collector = new CollectorAgent(k, agentId, baron);
            collector.init(food, foodSatiety, wood);
            baron.addVillager(collector);
            agents.put(agentId, collector);
        } else if (agents.get(agentId) instanceof CollectorAgent) {
            agents.get(agentId).kill();
            BuilderAgent builder = new BuilderAgent(k, agentId, baron);
            builder.init(food, foodSatiety, wood);
            baron.addVillager(builder);
            agents.put(agentId, builder);
        }
    }

    public void deleteAgent(String agentId) {
        agents.remove(agentId);
        executors.remove(agentId);
    }

    public void updateGUI() {
        for (String agentId : agents.keySet()) {
            agents.get(agentId).updateInfoGUI();
        }
        GUI.refresh();
    }

    private void clearWMEOutputs() {
        for (String agentId : agents.keySet()) {
            agents.get(agentId).clearOutput();
        }
    }

    private Map<String, ExecutorService> initExecutors() {
        Map<String, ExecutorService> exec = new HashMap<>();
        for (String agentId : agents.keySet()) {
            exec.put(agentId, Executors.newSingleThreadExecutor());
        }
        return exec;
    }

    public void shutdown() {
        // Shutdown the Soar interface and the executor service.
        for (String agentId : agents.keySet()) {
            agents.get(agentId).shutdown();
            executors.get(agentId).shutdown();
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

    public Map<String, GeneralAgent> createAgents(int kernelPort, Kernel kernel) {
        this.kernelPort = kernelPort;
        Map<String, GeneralAgent> allAgents = new LinkedHashMap<>();

        // Barons
        for (int i = 0; i < Environment.startNumBarons; ++i) {
            String baronId = NameSampler.getInstance().sampleBaronName();
            BaronAgent baron = new BaronAgent(kernel, baronId);
            allAgents.put(baronId, baron);

            // Collectors
            for (int j = 0; j < Environment.startNumCollectors; ++j) {
                String collectorId = NameSampler.getInstance().sampleVillagerName();
                CollectorAgent collector = new CollectorAgent(kernel, collectorId, baron);
                baron.addVillager(collector);
                allAgents.put(collectorId, collector);
            }

            // Builders
            for (int j = 0; j < Environment.startNumBuilders; ++j) {
                String builderId = NameSampler.getInstance().sampleVillagerName();
                BuilderAgent builder = new BuilderAgent(kernel, builderId, baron);
                baron.addVillager(builder);
                allAgents.put(builderId, builder);
            }
        }
        return allAgents;
    }

    public void spawnDebugger(String agentId) {
        // Spawn debugger just for testing
        agents.get(agentId).getAgent().SpawnDebugger(kernelPort, "libs/soar/SoarJavaDebugger.jar");
    }

    public void decreaseFieldYield(String agentId, String fieldId) {
        if (agents.get(agentId) instanceof CollectorAgent) {
            ((CollectorAgent) agents.get(agentId)).decreaseFieldYield(fieldId);
            updateGUI();
        }
    }
}

