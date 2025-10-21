package org.opennms.plugins.autotask;

import org.opennms.integration.api.v1.ticketing.Ticket;
import org.opennms.integration.api.v1.ticketing.TicketingPlugin;
import org.opennms.integration.api.v1.ticketing.immutables.ImmutableTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opennms.plugins.autotask.model.AutotaskTicket;
import org.opennms.plugins.autotask.model.AutotaskTicketResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Autotask Ticketing Plugin for OpenNMS
 * 
 * Integrates OpenNMS with Autotask PSA by implementing the TicketingPlugin interface.
 * This plugin handles creating, updating, and retrieving tickets in Autotask based on
 * OpenNMS alarms following the same patterns as existing JIRA, Remedy, and TSRM plugins.
 */
public class AutotaskTicketingPlugin implements TicketingPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(AutotaskTicketingPlugin.class);

    private AutotaskApiClient autotaskClient;
    private AutotaskTicketingConfiguration config;
    private ExecutorService executorService;

    // OpenNMS to Autotask status mappings
    private static final int AUTOTASK_STATUS_NEW = 1;
    private static final int AUTOTASK_STATUS_COMPLETE = 8;

    public AutotaskTicketingPlugin() {
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Initialize the plugin with configuration
     */
    public void init() {
        LOG.info("Initializing Autotask Ticketing Plugin");
        
        if (config == null) {
            LOG.error("AutotaskTicketingConfiguration is not set");
            throw new IllegalStateException("Configuration must be set before initialization");
        }
        
        this.autotaskClient = new AutotaskApiClient(
            config.getBaseUrl(),
            config.getApiIntegrationCode(),
            config.getUserName(),
            config.getSecret()
        );
        
        LOG.info("Autotask Ticketing Plugin initialized successfully");
    }

    /**
     * Cleanup resources
     */
    public void destroy() {
        LOG.info("Shutting down Autotask Ticketing Plugin");
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    @Override
    public Ticket get(String ticketId) {
        LOG.debug("Getting ticket with ID: {}", ticketId);
        
        try {
            AutotaskTicketResponse response = autotaskClient.getTicket(ticketId).get();
            if (response != null && response.getItem() != null) {
                return convertToOpenNMSTicket(response.getItem());
            } else {
                LOG.warn("No ticket found for ID: {}", ticketId);
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error retrieving ticket {}: {}", ticketId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve ticket: " + ticketId, e);
        }
    }

    @Override
    public String saveOrUpdate(Ticket ticket) {
        LOG.debug("Saving or updating ticket: {}", ticket.getId());
        
        try {
            if (ticket.getId() == null || ticket.getId().isEmpty()) {
                // Create new ticket
                return createNewTicket(ticket);
            } else {
                // Update existing ticket
                return updateExistingTicket(ticket);
            }
        } catch (Exception e) {
            LOG.error("Error processing ticket {}: {}", ticket.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process ticket", e);
        }
    }

    /**
     * Create a new ticket in Autotask
     */
    private String createNewTicket(Ticket openNMSTicket) throws Exception {
        LOG.info("Creating new Autotask ticket for alarm: {}", openNMSTicket.getSummary());
        
        AutotaskTicket autotaskTicket = new AutotaskTicket();
        autotaskTicket.setTitle(openNMSTicket.getSummary());
        autotaskTicket.setDescription(buildTicketDescription(openNMSTicket));
        autotaskTicket.setCompanyID(config.getDefaultCompanyId());
        autotaskTicket.setStatus(mapOpenNMSStatusToAutotask(openNMSTicket.getState()));
        autotaskTicket.setPriority(mapSeverityToPriority(openNMSTicket));
        autotaskTicket.setTicketCategory(config.getDefaultTicketCategory());
        autotaskTicket.setQueueID(config.getDefaultQueueId());
        autotaskTicket.setSource(config.getDefaultSource());
        autotaskTicket.setTicketType(config.getDefaultTicketType());
        
        // Set external ID to OpenNMS alarm ID for tracking
        if (openNMSTicket.getAttributes() != null && openNMSTicket.getAttributes().containsKey("alarmId")) {
            autotaskTicket.setExternalID(openNMSTicket.getAttributes().get("alarmId"));
        }

        AutotaskTicketResponse response = autotaskClient.createTicket(autotaskTicket).get();
        if (response != null && response.getItem() != null) {
            String ticketId = String.valueOf(response.getItem().getId());
            LOG.info("Successfully created Autotask ticket {} for alarm: {}", 
                     ticketId, openNMSTicket.getSummary());
            return ticketId;
        } else {
            throw new RuntimeException("Failed to create ticket - no response from Autotask API");
        }
    }

    /**
     * Update an existing ticket in Autotask
     */
    private String updateExistingTicket(Ticket openNMSTicket) throws Exception {
        LOG.info("Updating Autotask ticket: {}", openNMSTicket.getId());
        
        // Get current ticket
        AutotaskTicketResponse response = autotaskClient.getTicket(openNMSTicket.getId()).get();
        if (response == null || response.getItem() == null) {
            LOG.error("Cannot update ticket {} - ticket not found", openNMSTicket.getId());
            throw new RuntimeException("Ticket not found: " + openNMSTicket.getId());
        }

        AutotaskTicket autotaskTicket = response.getItem();
        
        // Update fields based on OpenNMS ticket changes
        autotaskTicket.setStatus(mapOpenNMSStatusToAutotask(openNMSTicket.getState()));
        autotaskTicket.setPriority(mapSeverityToPriority(openNMSTicket));
        
        // Add update note to description if provided
        if (openNMSTicket.getDetails() != null && !openNMSTicket.getDetails().trim().isEmpty()) {
            String updatedDescription = autotaskTicket.getDescription() + "\n\nUpdate: " + openNMSTicket.getDetails();
            autotaskTicket.setDescription(updatedDescription);
        }

        AutotaskTicketResponse updateResponse = autotaskClient.updateTicket(autotaskTicket).get();
        if (updateResponse != null && updateResponse.getItem() != null) {
            LOG.info("Successfully updated Autotask ticket: {}", openNMSTicket.getId());
            return openNMSTicket.getId();
        } else {
            throw new RuntimeException("Failed to update ticket - no response from Autotask API");
        }
    }

    /**
     * Convert Autotask ticket to OpenNMS ticket
     */
    private Ticket convertToOpenNMSTicket(AutotaskTicket autotaskTicket) {
        return ImmutableTicket.newBuilder()
                .setId(String.valueOf(autotaskTicket.getId()))
                .setSummary(autotaskTicket.getTitle())
                .setDetails(autotaskTicket.getDescription())
                .setState(mapAutotaskStatusToOpenNMS(autotaskTicket.getStatus()))
                .build();
    }

    /**
     * Map OpenNMS ticket state to Autotask status
     */
    private Integer mapOpenNMSStatusToAutotask(Ticket.State state) {
        if (state == null) {
            return AUTOTASK_STATUS_NEW;
        }
        
        switch (state) {
            case OPEN:
                return AUTOTASK_STATUS_NEW;
            case CLOSED:
                return AUTOTASK_STATUS_COMPLETE;
            case CANCELLED:
                return AUTOTASK_STATUS_COMPLETE;
            default:
                return AUTOTASK_STATUS_NEW;
        }
    }

    /**
     * Map Autotask status to OpenNMS ticket state
     */
    private Ticket.State mapAutotaskStatusToOpenNMS(Integer autotaskStatus) {
        if (autotaskStatus == null) {
            return Ticket.State.OPEN;
        }
        
        // Based on common Autotask status values
        switch (autotaskStatus) {
            case 1: // New
                return Ticket.State.OPEN;
            case 5: // In Progress
            case 6: // Waiting Customer
            case 7: // Waiting Vendor
                return Ticket.State.OPEN; // Keep as open since OpenNMS only has OPEN, CLOSED, CANCELLED
            case 8: // Complete
                return Ticket.State.CLOSED;
            case 9: // Cancelled
                return Ticket.State.CANCELLED;
            default:
                return Ticket.State.OPEN;
        }
    }

    /**
     * Map alarm severity to Autotask priority
     */
    private Integer mapSeverityToPriority(Ticket ticket) {
        // Use configuration defaults or map based on severity attributes
        if (ticket.getAttributes() != null && ticket.getAttributes().containsKey("severity")) {
            String severity = ticket.getAttributes().get("severity");
            switch (severity.toLowerCase()) {
                case "critical":
                case "major":
                    return 1; // High priority
                case "minor":
                    return 2; // Medium priority
                case "warning":
                case "cleared":
                case "normal":
                    return 3; // Low priority
                default:
                    return config.getDefaultPriority();
            }
        }
        return config.getDefaultPriority();
    }

    /**
     * Build detailed ticket description from OpenNMS ticket
     */
    private String buildTicketDescription(Ticket openNMSTicket) {
        StringBuilder description = new StringBuilder();
        
        description.append("OpenNMS Alarm Details:\n");
        description.append("Summary: ").append(openNMSTicket.getSummary()).append("\n");
        
        if (openNMSTicket.getDetails() != null && !openNMSTicket.getDetails().trim().isEmpty()) {
            description.append("Details: ").append(openNMSTicket.getDetails()).append("\n");
        }
        
        if (openNMSTicket.getAttributes() != null) {
            description.append("\nAlarm Attributes:\n");
            openNMSTicket.getAttributes().forEach((key, value) -> 
                description.append("- ").append(key).append(": ").append(value).append("\n")
            );
        }
        
        return description.toString();
    }

    // Dependency injection setters
    public void setConfig(AutotaskTicketingConfiguration config) {
        this.config = config;
    }

    public AutotaskTicketingConfiguration getConfig() {
        return config;
    }
}