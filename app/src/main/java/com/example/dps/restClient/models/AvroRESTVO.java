package com.example.dps.restClient.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public abstract class AvroRESTVO {
    private String key;
    private String username;
    private String name;
    private Instant birthDate;
    private Instant createDate;
    private boolean patient;

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public boolean isPatient() {
        return patient;
    }

    public AvroRESTVO(String username, String name, Instant birthDate, boolean patient) {
        this.key = UUID.randomUUID().toString();
        this.username = username;
        this.name = name;
        this.birthDate = birthDate;
        this.createDate = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        this.patient = patient;
    }

    /**
     * Kafka 커넥터에 보내기 위한 REST 메시지 반환. key schema, key message, value schema, value message로 구성됨
     * @return REST 메시지
     */
    public String toRESTMessage() {
        return "{" + this.keySchemaMessage() + ", " + this.keyMessage() + "," +
                 this.valueSchemaMessage() + ", " + this.valueMessage() + "}";

    }

    /**
     * key schema 문자열 생성
     * @return key schema
     */
    protected String keySchemaMessage() {
        String message = "\"key_schema\": \"{\\\"name\\\": \\\"key\\\", \\\"type\\\": \\\"record\\\", " +
                "\\\"fields\\\": [{\\\"name\\\": \\\"key\\\", \\\"type\\\": \\\"string\\\"}]}\"";
        return message;
    }

    /**
     * key message 문자열 생성
     * @return key message
     */
    protected String keyMessage() {
        String message = "\"records\": [{\"value\": {\"key\": \""+this.key+"\"}}]";
        return message;
    }

    /**
     * value schema 문자열 생성
     * @return value schema
     */
    protected abstract String valueSchemaMessage();

    /**
     * value message 문자열 생성
     * @return value message
     */
    protected abstract String valueMessage();

    /**
     * 자바 타입을 avro 타입 문자열(따옴표 포함, date일 경우 데이터 구조 포함)로 변환
     * @param type
     * @param <T>
     * @return avro 문자열 타입
     */
    protected <T> String javaTypeToAvroType(T type) {
        String result = "\\\"string\\\"";

        if(type instanceof Integer) result = "\\\"int\\\"";
        else if(type instanceof Double || type instanceof Float) result = "\\\"double\\\"";
        else if(type instanceof Boolean) result = "\\\"boolean\\\"";
        else if(type instanceof Instant) result = "\\\"long\\\"";

        return result;
    }
}
