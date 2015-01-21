package com.lessonslearned.lessonslearned;

import java.util.Date;

/**
 * Created by ias0nas on 01/06/14.
 */
public class LastAlert {

    private long id;
    private Date timestamp;

    public long getId() { return id; }

    public void setId(long id) { this.id = id;}

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() { return String.valueOf(id); }
}
