package com.align.pages;

public interface BirthDetailsPage {
    boolean isBirthDetailsScreenVisible();
    void setBirthDate();                 // opens picker, accepts the shown date
    void setBirthTime();                 // opens picker, accepts the shown time
    void setBirthLocation(String city);  // searches and selects the first match
    void tapNext();
}
