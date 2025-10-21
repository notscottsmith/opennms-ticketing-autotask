package com.company.autotask;

import java.util.Objects;

/**
 * Configuration class for Autotask Ticketing Plugin
 * 
 * Holds all configuration parameters needed to connect to and interact with
 * the Autotask PSA system, including authentication details and default values.
 */
public class AutotaskTicketingConfiguration {

    // Authentication settings
    private String baseUrl = "https://webservices6.autotask.net/ATServicesRest/v1.0/";
    private String apiIntegrationCode;
    private String userName;
    private String secret;

    // Default ticket settings
    private Long defaultCompanyId;
    private Integer defaultTicketCategory = 1; // Default to first category
    private Integer defaultQueueId;
    private Integer defaultSource = 6; // API source
    private Integer defaultTicketType = 1; // Service Request
    private Integer defaultPriority = 3; // Medium priority
    private Long defaultContactId;
    private Long defaultResourceId; // For assignment

    // Plugin behavior settings
    private boolean autoClose = false;
    private boolean updateDescription = true;
    private int timeoutSeconds = 30;

    // Constructors
    public AutotaskTicketingConfiguration() {}

    // Getters and Setters for Authentication
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiIntegrationCode() {
        return apiIntegrationCode;
    }

    public void setApiIntegrationCode(String apiIntegrationCode) {
        this.apiIntegrationCode = apiIntegrationCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    // Getters and Setters for Default Values
    public Long getDefaultCompanyId() {
        return defaultCompanyId;
    }

    public void setDefaultCompanyId(Long defaultCompanyId) {
        this.defaultCompanyId = defaultCompanyId;
    }

    public Integer getDefaultTicketCategory() {
        return defaultTicketCategory;
    }

    public void setDefaultTicketCategory(Integer defaultTicketCategory) {
        this.defaultTicketCategory = defaultTicketCategory;
    }

    public Integer getDefaultQueueId() {
        return defaultQueueId;
    }

    public void setDefaultQueueId(Integer defaultQueueId) {
        this.defaultQueueId = defaultQueueId;
    }

    public Integer getDefaultSource() {
        return defaultSource;
    }

    public void setDefaultSource(Integer defaultSource) {
        this.defaultSource = defaultSource;
    }

    public Integer getDefaultTicketType() {
        return defaultTicketType;
    }

    public void setDefaultTicketType(Integer defaultTicketType) {
        this.defaultTicketType = defaultTicketType;
    }

    public Integer getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority(Integer defaultPriority) {
        this.defaultPriority = defaultPriority;
    }

    public Long getDefaultContactId() {
        return defaultContactId;
    }

    public void setDefaultContactId(Long defaultContactId) {
        this.defaultContactId = defaultContactId;
    }

    public Long getDefaultResourceId() {
        return defaultResourceId;
    }

    public void setDefaultResourceId(Long defaultResourceId) {
        this.defaultResourceId = defaultResourceId;
    }

    // Getters and Setters for Behavior Settings
    public boolean isAutoClose() {
        return autoClose;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public boolean isUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(boolean updateDescription) {
        this.updateDescription = updateDescription;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Validate that required configuration parameters are set
     * @throws IllegalStateException if required parameters are missing
     */
    public void validate() throws IllegalStateException {
        if (apiIntegrationCode == null || apiIntegrationCode.trim().isEmpty()) {
            throw new IllegalStateException("API Integration Code is required");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalStateException("User Name is required");
        }
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("Secret is required");
        }
        if (defaultCompanyId == null || defaultCompanyId <= 0) {
            throw new IllegalStateException("Default Company ID is required and must be positive");
        }
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalStateException("Base URL is required");
        }
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            throw new IllegalStateException("Base URL must start with http:// or https://");
        }
    }

    // Equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutotaskTicketingConfiguration that = (AutotaskTicketingConfiguration) o;
        return autoClose == that.autoClose &&
                updateDescription == that.updateDescription &&
                timeoutSeconds == that.timeoutSeconds &&
                Objects.equals(baseUrl, that.baseUrl) &&
                Objects.equals(apiIntegrationCode, that.apiIntegrationCode) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(secret, that.secret) &&
                Objects.equals(defaultCompanyId, that.defaultCompanyId) &&
                Objects.equals(defaultTicketCategory, that.defaultTicketCategory) &&
                Objects.equals(defaultQueueId, that.defaultQueueId) &&
                Objects.equals(defaultSource, that.defaultSource) &&
                Objects.equals(defaultTicketType, that.defaultTicketType) &&
                Objects.equals(defaultPriority, that.defaultPriority) &&
                Objects.equals(defaultContactId, that.defaultContactId) &&
                Objects.equals(defaultResourceId, that.defaultResourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, apiIntegrationCode, userName, secret, 
                           defaultCompanyId, defaultTicketCategory, defaultQueueId, 
                           defaultSource, defaultTicketType, defaultPriority, 
                           defaultContactId, defaultResourceId, autoClose, 
                           updateDescription, timeoutSeconds);
    }

    @Override
    public String toString() {
        return "AutotaskTicketingConfiguration{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiIntegrationCode='" + (apiIntegrationCode != null ? "***" : "null") + '\'' +
                ", userName='" + userName + '\'' +
                ", secret='" + (secret != null ? "***" : "null") + '\'' +
                ", defaultCompanyId=" + defaultCompanyId +
                ", defaultTicketCategory=" + defaultTicketCategory +
                ", defaultQueueId=" + defaultQueueId +
                ", defaultSource=" + defaultSource +
                ", defaultTicketType=" + defaultTicketType +
                ", defaultPriority=" + defaultPriority +
                ", defaultContactId=" + defaultContactId +
                ", defaultResourceId=" + defaultResourceId +
                ", autoClose=" + autoClose +
                ", updateDescription=" + updateDescription +
                ", timeoutSeconds=" + timeoutSeconds +
                '}';
    }
}