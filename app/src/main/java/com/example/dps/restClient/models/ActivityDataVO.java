package com.example.dps.restClient.models;

import java.time.Instant;

public class ActivityDataVO extends AvroRESTVO {
    private int calActive;
    private int calTotal;
    private int dailyMovement;
    private Instant dayEnd;
    private Instant dayStart;
    private int high;
    private int inactive;
    private int inactivityAlerts;
    private int low;
    private int medium;
    private int nonWear;
    private int score;
    private int scoreMeetDailyTargets;
    private int steps;
    private int total;

    public ActivityDataVO(String username, String name, Instant birthDate , int calActive,
                          int calTotal, int dailyMovement, Instant dayEnd, Instant dayStart, int high, int inactive,
                          int inactivityAlerts, int low, int medium, int nonWear, int score, int scoreMeetDailyTargets,
                          int steps, int total, boolean patient) {
        super(username, name, birthDate, patient);
        this.calActive = calActive;
        this.calTotal = calTotal;
        this.dailyMovement = dailyMovement;
        this.dayEnd = dayEnd;
        this.dayStart = dayStart;
        this.high = high;
        this.inactive = inactive;
        this.inactivityAlerts = inactivityAlerts;
        this.low = low;
        this.medium = medium;
        this.nonWear = nonWear;
        this.score = score;
        this.scoreMeetDailyTargets = scoreMeetDailyTargets;
        this.steps = steps;
        this.total = total;
    }

    public Instant getDayEnd() {
        return dayEnd;
    }

    public Instant getDayStart() {
        return dayStart;
    }

    @Override
    protected String valueSchemaMessage() {
        String message = "\"value_schema\": \"{" +
                "\\\"name\\\": \\\"activity_data_value\\\"," +
                "\\\"type\\\": \\\"record\\\"," +
                "\\\"fields\\\": [" +
                "{\\\"name\\\": \\\"username\\\", \\\"type\\\": "+ this.javaTypeToAvroType(this.getUsername()) +"}," +
                "{\\\"name\\\": \\\"name\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getName())+"}," +
                "{\\\"name\\\": \\\"birth_date\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getBirthDate())+"}," +
                "{\\\"name\\\": \\\"create_date\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getCreateDate())+"}," +
                "{\\\"name\\\": \\\"activity_cal_active\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.calActive)+"}," +
                "{\\\"name\\\": \\\"activity_cal_total\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.calTotal)+"}," +
                "{\\\"name\\\": \\\"activity_daily_movement\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.dailyMovement)+"}," +
                "{\\\"name\\\": \\\"activity_day_end\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getDayEnd())+"}," +
                "{\\\"name\\\": \\\"activity_day_start\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.getDayStart())+"}," +
                "{\\\"name\\\": \\\"activity_high\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.high)+"}," +
                "{\\\"name\\\": \\\"activity_inactive\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.inactive)+"}," +
                "{\\\"name\\\": \\\"activity_inactivity_alerts\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.inactivityAlerts)+"}," +
                "{\\\"name\\\": \\\"activity_low\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.low)+"}," +
                "{\\\"name\\\": \\\"activity_medium\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.medium)+"}," +
                "{\\\"name\\\": \\\"activity_non_wear\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.nonWear)+"}," +
                "{\\\"name\\\": \\\"activity_score\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.score)+"}," +
                "{\\\"name\\\": \\\"activity_score_meet_daily_targets\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.scoreMeetDailyTargets)+"}," +
                "{\\\"name\\\": \\\"activity_steps\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.steps)+"}," +
                "{\\\"name\\\": \\\"activity_total\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.total)+"}," +
                "{\\\"name\\\": \\\"is_patient\\\", \\\"type\\\": "+this.javaTypeToAvroType(this.isPatient())+"}" +
                "]}\"";
        return message;
    }

    @Override
    protected String valueMessage() {
        String message = "\"records\":" +
                "[{\"value\": {" +
                "\"username\": \""+this.getUsername()+"\"," +
                "\"name\": \""+this.getName()+"\"," +
                "\"birth_date\": "+this.getBirthDate().getEpochSecond()+"," +
                "\"create_date\": "+this.getCreateDate().getEpochSecond()+"," +
                "\"activity_cal_active\": "+this.calActive+"," +
                "\"activity_cal_total\": "+this.calTotal+"," +
                "\"activity_daily_movement\": "+this.dailyMovement+"," +
                "\"activity_day_end\": "+this.getDayEnd().getEpochSecond()+"," +
                "\"activity_day_start\": "+this.getDayStart().getEpochSecond()+"," +
                "\"activity_high\": "+this.high+"," +
                "\"activity_inactive\": "+this.inactive+"," +
                "\"activity_inactivity_alerts\": "+this.inactivityAlerts+"," +
                "\"activity_low\": "+this.low+"," +
                "\"activity_medium\": "+this.medium+"," +
                "\"activity_non_wear\": "+this.nonWear+"," +
                "\"activity_score\": "+this.score+"," +
                "\"activity_score_meet_daily_targets\": "+this.scoreMeetDailyTargets+"," +
                "\"activity_steps\": "+this.steps+"," +
                "\"activity_total\": "+this.total+"," +
                "\"is_patient\": "+this.isPatient() +
                "}}]";
        return message;
    }
}
