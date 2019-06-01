package com.github.lzy.haystack.agent;

public class AgentLoader {
    public static void main(String[] args) {
        Agent agent = new SpanAgent();
        agent.start();
        System.out.println(agent);
    }
}
