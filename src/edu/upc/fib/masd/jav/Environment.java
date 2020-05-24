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
    private static final Environment instance = new Environment();

    // We keep references to Agents.
    private ArrayList<GeneralAgent> agents;
    // Create executor services to run Soar in since it blocks.
    private ArrayList<ExecutorService> executors;
    // To read user input
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    private Environment() {
    }

    public static Environment getInstance() {
        return instance;
    }

    public void setAgents(ArrayList<GeneralAgent> agents) {
        this.agents = agents;
        this.executors = instance.initExecutors();
    }

    public void runSystemCycle() {
        try {
            // Necessary delay (ms)
            delay(1000);

            // Loop
            while (!Thread.interrupted()) {
                System.out.println("========================");
                System.out.println("Press enter to continue (or X to exit):");
                String line = input.readLine();

                if (line.equalsIgnoreCase("x")) {
                    shutdown();
                }

                runSystemStep();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void runSystemStep() {
        clearWMEOutputs();
        runAllAgentsOneStep();
        updateEnvironmentState();
        readAndTreatAllAgentsOutputs();

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

    public static ArrayList<GeneralAgent> createAgents(Kernel kernel, int numBarons, int numCollectors, int numBuilders) {
        ArrayList<GeneralAgent> allAgents = new ArrayList<>();

        int food = 5;
        int foodSatiety = 15;
        int wood = 0;
        int numFields = 3;
        FieldState fieldState = FieldState.DRY;
        int fieldYield = 2;

        // Barons
        for (int i = 0; i < numBarons; ++i) {
            BaronAgent baron = new BaronAgent(kernel, String.format("Baron_%d", i), "SOAR_Codes/PRESET_baron_agent.soar", food, foodSatiety, wood);
            //baron.getAgent().RunSelf(0);
            allAgents.add(baron);

            // Collectors
            for (int j = 0; j < numCollectors; ++j) {
                CollectorAgent collector = new CollectorAgent(kernel, "Collector_" + j, "SOAR_Codes/PRESET_collector_agent.soar", baron, food, foodSatiety, wood);
                Identifier fieldsRoot = collector.inputLink.CreateIdWME("fields");
                for (int k = 0; k < numFields; ++k) {
                    Field field = new Field(collector, fieldsRoot, String.format("Field_%d", k), FieldState.DRY, fieldYield);
                    collector.addField(field);
                }
                //collector.getAgent().RunSelf(0);
                baron.addVillager(collector);
                allAgents.add(collector);
            }

            // Builders
            for (int j = 0; j < numBuilders; ++j) {
                BuilderAgent builder = new BuilderAgent(kernel, String.format("Builder_%d", j), "SOAR_Codes/PRESET_builder_agent.soar", baron, food, foodSatiety, wood);
                //builder.getAgent().RunSelf(0);
                baron.addVillager(builder);
                allAgents.add(builder);
            }
        }
        return allAgents;
    }
}

