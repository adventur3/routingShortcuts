package main;

import simulator.OverallSimulation_test;
import simulator.OverallSimulation_variance;

public class Main {
    public static void main(String[] args) throws Exception{
        OverallSimulation_variance overallSimulator = new OverallSimulation_variance();
        overallSimulator.simulate();
    }
}
