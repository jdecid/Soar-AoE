package edu.upc.fib.masd.jav.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NameSampler {
    private static final NameSampler instance = new NameSampler();
    private final List<String> names;
    private int lastIndex = 0;

    private NameSampler() {
        names = new ArrayList<>();

        try {
            File file = new File("src/main/resources/names.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.shuffle(names);
    }

    public static NameSampler getInstance() {
        return instance;
    }

    public String sampleBaronName() {
        return "Javier";
    }

    public String sampleVillagerName() {
        return names.get(lastIndex++);
    }

    public void deleteAgentName(String name) {
        names.remove(name);
        names.add(name);
        --lastIndex;
    }
}