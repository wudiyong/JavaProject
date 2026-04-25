package com;

public class OpenCodeAgentRequest {

    private String gitUsername;
    private String sessionID;
    private String modelID;
    private String agent;
    private String opencodeVersion;

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getModelID() {
        return modelID;
    }

    public void setModelID(String modelID) {
        this.modelID = modelID;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getOpencodeVersion() {
        return opencodeVersion;
    }

    public void setOpencodeVersion(String opencodeVersion) {
        this.opencodeVersion = opencodeVersion;
    }
}
