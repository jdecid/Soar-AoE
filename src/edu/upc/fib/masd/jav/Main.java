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
        ArrayList<GeneralAgent> agentsArray = Environment.createAgents(k);

        Environment env = Environment.getInstance();
        env.setAgents(agentsArray);

        // Spawn debugger just for testing
        agentsArray.get(0).getAgent().SpawnDebugger(kernelPort, "libs/soar/SoarJavaDebugger.jar");

        GUI.setEnvironment(env);
        env.updateGUI();
    }
}
