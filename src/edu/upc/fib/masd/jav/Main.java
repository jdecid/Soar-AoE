package edu.upc.fib.masd.jav;

import sml.Kernel;

import java.util.ArrayList;
import java.util.Map;

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
        Map<String, GeneralAgent> agentsMap = Environment.createAgents(k);

        Environment env = Environment.getInstance();
        env.setAgents(agentsMap);

        // Spawn debugger just for testing
        agentsMap.get("Baron_0").getAgent().SpawnDebugger(kernelPort, "libs/soar/SoarJavaDebugger.jar");

        GUI.setEnvironment(env);
        env.updateGUI();
    }
}
