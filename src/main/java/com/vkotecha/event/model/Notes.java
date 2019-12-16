package com.vkotecha.vertx.entity;

import com.vkotecha.vertx.util.DateUtils;
import java.time.LocalDate;


/**
 * @author Vishal Kotecha
 */
public class Notes {

  private Long id;
  private LocalDate dateTime;
  private String title;
  private String description;
  private Integer noteType;
  private String speaker;
  private Boolean isPublished;

  public Notes() {
  }

  public Notes(String title, String description, String dateTime, Integer noteType, String speaker) {
    this.dateTime = DateUtils.convertStrToLD(dateTime);
    this.title = title;
    this.description = description;
    this.noteType = noteType;
    this.speaker = speaker;
  }

  private Integer parse(String noteType) {
    try {
      return Integer.parseInt(noteType);
    } catch (NumberFormatException nfe) {
      throw new RuntimeException("Invalid note type!");
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDate dateTime) {
    this.dateTime = dateTime;
  }

  public java.sql.Date getDate() {
    if (dateTime == null) { return null; }
    return java.sql.Date.valueOf(dateTime);
  }


  public void setDate(java.sql.Date dateTime) {
    this.dateTime = dateTime.toLocalDate();
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

  public Integer getNoteType() {
    return noteType;
  }

  public void setNoteType(Integer noteType) {
    this.noteType = noteType;
  }

  public Boolean getPublished() {
    return isPublished;
  }

  public void setPublished(Boolean published) {
    isPublished = published;
  }

  public String getSpeaker() {
    return speaker;
  }

  public void setSpeaker(String speaker) {
    this.speaker = speaker;
  }
}
