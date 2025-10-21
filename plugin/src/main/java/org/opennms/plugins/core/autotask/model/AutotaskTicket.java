package com.company.autotask.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutotaskTicket {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description") 
    private String description;

    @JsonProperty("companyID")
    private Long companyID;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("ticketCategory")
    private Integer ticketCategory;

    @JsonProperty("assignedResourceID")
    private Long assignedResourceID;

    @JsonProperty("queueID")
    private Integer queueID;

    @JsonProperty("contactID")
    private Long contactID;

    @JsonProperty("source")
    private Integer source;

    @JsonProperty("ticketType")
    private Integer ticketType;

    @JsonProperty("dueDateTime")
    private String dueDateTime;

    @JsonProperty("createDate")
    private String createDate;

    @JsonProperty("ticketNumber")
    private String ticketNumber;

    @JsonProperty("externalID")
    private String externalID;

    // Constructors
    public AutotaskTicket() {}

    // Getters and Setters
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

    public Long getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Long companyID) {
        this.companyID = companyID;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(Integer ticketCategory) {
        this.ticketCategory = ticketCategory;
    }

    public Long getAssignedResourceID() {
        return assignedResourceID;
    }

    public void setAssignedResourceID(Long assignedResourceID) {
        this.assignedResourceID = assignedResourceID;
    }

    public Integer getQueueID() {
        return queueID;
    }

    public void setQueueID(Integer queueID) {
        this.queueID = queueID;
    }

    public Long getContactID() {
        return contactID;
    }

    public void setContactID(Long contactID) {
        this.contactID = contactID;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getTicketType() {
        return ticketType;
    }

    public void setTicketType(Integer ticketType) {
        this.ticketType = ticketType;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    // Equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutotaskTicket that = (AutotaskTicket) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(companyID, that.companyID) &&
                Objects.equals(status, that.status) &&
                Objects.equals(priority, that.priority) &&
                Objects.equals(ticketCategory, that.ticketCategory) &&
                Objects.equals(assignedResourceID, that.assignedResourceID) &&
                Objects.equals(queueID, that.queueID) &&
                Objects.equals(contactID, that.contactID) &&
                Objects.equals(source, that.source) &&
                Objects.equals(ticketType, that.ticketType) &&
                Objects.equals(dueDateTime, that.dueDateTime) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(ticketNumber, that.ticketNumber) &&
                Objects.equals(externalID, that.externalID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, companyID, status, priority, 
                           ticketCategory, assignedResourceID, queueID, contactID, 
                           source, ticketType, dueDateTime, createDate, ticketNumber, externalID);
    }

    @Override
    public String toString() {
        return "AutotaskTicket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", companyID=" + companyID +
                ", status=" + status +
                ", priority=" + priority +
                ", ticketCategory=" + ticketCategory +
                ", assignedResourceID=" + assignedResourceID +
                ", queueID=" + queueID +
                ", contactID=" + contactID +
                ", source=" + source +
                ", ticketType=" + ticketType +
                ", dueDateTime='" + dueDateTime + '\'' +
                ", createDate='" + createDate + '\'' +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", externalID='" + externalID + '\'' +
                '}';
    }
}