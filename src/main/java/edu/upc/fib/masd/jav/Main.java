package edu.upc.fib.masd.jav;

import edu.upc.fib.masd.jav.agents.GeneralAgent;
import sml.Kernel;

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
        Environment env = Environment.getInstance();
        Map<String, GeneralAgent> agentsMap = env.createAgents(kernelPort, k);
        env.setAgents(agentsMap);
        GUI.setEnvironment(env);
        env.updateGUI();
    }
}
