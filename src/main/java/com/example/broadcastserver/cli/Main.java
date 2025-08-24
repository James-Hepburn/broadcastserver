package com.example.broadcastserver.cli;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        new CommandLine(new BroadcastCommand()).execute(args);
    }
}