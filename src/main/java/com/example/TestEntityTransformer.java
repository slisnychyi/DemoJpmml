package com.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Date;

public class TestEntityTransformer {

    public TestEntity generate(){
        TestEntity result = new TestEntity();
        result.setDate(new Date());
        result.setName("Tom");
        result.setAge(25);
        result.setAdress("Ukraine, Kiev");
        return result;
    }

    public TestEntity deserialize() throws IOException, ClassNotFoundException {
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://127.0.0.1:8080/generateEntity");

        HttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();

        InputStream content = entity.getContent();

        String s = IOUtils.toString(content);

        System.out.println(s);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        TestEntity testEntity = objectMapper.readValue(s, TestEntity.class);

        return null;
    }

    public TestEntity deserialize2() throws IOException, ClassNotFoundException {
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://127.0.0.1:8080/generateEntityByte");

        HttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();

        InputStream content = entity.getContent();

        byte[] bytes = IOUtils.toByteArray(content);

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));

        return (TestEntity) objectInputStream.readObject();
    }
}
