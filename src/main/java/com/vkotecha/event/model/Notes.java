package com.vkotecha.event.model;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Vishal Kotecha
 */
public class Notes {
  private static final AtomicLong COUNTER = new AtomicLong();

  private Long id;
  private String title;
  private String description;
  private String date;
  private String noteType;
  private String speaker;
  private Boolean isPublished;

  public Notes(String title, String description) {
    this.id = COUNTER.getAndIncrement();
    this.title = title;
    this.description = description;
    this.date = LocalDate.now().toString();
    this.noteType = "session";
    this.speaker = "Test";
    this.isPublished = true;
  }

  public Notes(){
    this.id = COUNTER.getAndIncrement();
  }

  public Notes(Long id, String title, String description) {
    this.id = id;
    this.title = title;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getNoteType() {
    return noteType;
  }

  public void setNoteType(String noteType) {
    this.noteType = noteType;
  }

  public String getSpeaker() {
    return speaker;
  }

  public void setSpeaker(String speaker) {
    this.speaker = speaker;
  }

  public Boolean getPublished() {
    return isPublished;
  }

  public void setPublished(Boolean published) {
    isPublished = published;
  }
}
