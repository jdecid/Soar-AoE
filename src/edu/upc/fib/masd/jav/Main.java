package edu.upc.fib.masd.jav;

import sml.Kernel;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Create the kernel
        final int kernelPort = 27391;
        Kernel k = Kernel.CreateKernelInNewThread(kernelPort);
        if (k.HadError()) {
            System.err.println("Error creating kernel: " + k.GetLastErrorDescription());
            System.exit(1);
        }

        // Create all the agents and load productions
        int numBarons = 1;
        int numCollectors = 3;
        int numBuilders = 0;
        ArrayList<GeneralAgent> agentsArray = Environment.createAgents(k, numBarons, numCollectors, numBuilders);

        Environment env = Environment.getInstance();
        env.setAgents(agentsArray);

        // Spawn debugger just for testing
        agentsArray.get(0).getAgent().SpawnDebugger(kernelPort, "libs/soar/SoarJavaDebugger.jar");


        GUI gui = GUI.getInstance();
        GUI.setEnvironment(env);
        for (GeneralAgent agent : agentsArray) {
            agent.updateInfoGUI("-");
        }
    }
}
