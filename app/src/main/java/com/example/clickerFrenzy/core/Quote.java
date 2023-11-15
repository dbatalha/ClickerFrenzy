package com.example.clickerFrenzy.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


public class Quote {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public String text;

    public String author;

}
