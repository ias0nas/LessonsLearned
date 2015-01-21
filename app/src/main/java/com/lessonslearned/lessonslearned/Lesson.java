package com.lessonslearned.lessonslearned;

import java.util.Date;

/**
 * Created by ias0nas on 02/03/14.
 */
public class Lesson {

    private long id;
    private String name;
    private String description;
    private Date timestamp;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Date getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }
    @Override
    public String toString(){
        return name;
    }
}
