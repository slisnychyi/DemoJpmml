package com.example;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestEntity implements Serializable{

    @JsonProperty("date")
    private Date date;
    @JsonProperty("name")
    private String name;
    @JsonProperty("age")
    private int age;

    @JsonProperty("adress")
    private transient String adress;

    public TestEntity() {
    }

    public boolean greaterThan(int age){
        return age > this.age;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEntity that = (TestEntity) o;

        if (age != that.age) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestEntity{");
        sb.append("date=").append(date);
        sb.append(", name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", adress='").append(adress).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
