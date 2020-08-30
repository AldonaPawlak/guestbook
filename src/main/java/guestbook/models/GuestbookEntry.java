package guestbook.models;

import java.util.Date;

public class GuestbookEntry {

    private String name;
    private String message;
    private Date date;

    public GuestbookEntry(String name, String message, Date date) {
        this.name = name;
        this.message = message;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }
}
