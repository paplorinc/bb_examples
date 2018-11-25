package lorinc.pap;

import lorinc.pap.agent.Agent;

import java.util.AbstractMap;

public class Main {

    public static void main(String... args) throws Exception {
        new AbstractMap.SimpleImmutableEntry<>("Key", "Value");

        System.out.println(Agent.constructors);
        if (Agent.constructors.stream().noneMatch(c -> c.contains("SimpleImmutableEntry"))) {
            throw new IllegalStateException();
        }
    }
}