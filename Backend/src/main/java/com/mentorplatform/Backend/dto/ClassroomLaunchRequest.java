package com.mentorplatform.Backend.dto;

import java.util.List;

public class ClassroomLaunchRequest {
    private String mentorEmail;
    private List<String> menteeEmails;
    private String topic;

    // Getters and Setters
    public String getMentorEmail() { return mentorEmail; }
    public void setMentorEmail(String mentorEmail) { this.mentorEmail = mentorEmail; }

    public List<String> getMenteeEmails() { return menteeEmails; }
    public void setMenteeEmails(List<String> menteeEmails) { this.menteeEmails = menteeEmails; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}