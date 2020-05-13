package edu.upc.fib.masd.jav;

import sml.Agent;
import sml.Kernel;

public class AoE {

    public static void main(String[] args) {
        final int kernelPort = 27314;
        final String agentName = "Eva";
        final int agentRandomSeed = 41372;

        final Kernel kernel = Kernel.CreateKernelInNewThread(kernelPort);
        final Agent agent = kernel.CreateAgent(agentName);
        agent.ExecuteCommandLine("srand " + agentRandomSeed);

        final AoEWorld world = new AoEWorld(agent);

        agent.SpawnDebugger(kernelPort, "libs/SoarJavaDebugger.jar");
        agent.LoadProductions("resources/soar/hello_world.soar");
    }

}