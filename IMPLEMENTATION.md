# Autotask Ticketing Plugin for OpenNMS

## Overview

This plugin provides integration between OpenNMS and Autotask PSA (Professional Services Automation) system. It implements the OpenNMS TicketingPlugin interface to automatically create, update, and retrieve tickets in Autotask based on OpenNMS alarms.

## Features

- **Automatic Ticket Creation**: Creates tickets in Autotask when OpenNMS alarms are triggered
- **Ticket Updates**: Updates existing tickets when alarm status changes
- **Ticket Retrieval**: Retrieves ticket information from Autotask
- **Status Mapping**: Maps OpenNMS alarm states to appropriate Autotask ticket statuses
- **Priority Mapping**: Maps alarm severity to Autotask ticket priority levels
- **Configurable Defaults**: Supports configuration of default values for company, queue, contact, etc.

## Implementation Details

### Java Runtime
- **Upgraded from Java 11 to Java 21 LTS** - Latest long-term support version with modern language features
- Maven compiler plugin configured with `--release` flag for Java 21 compatibility
- All dependencies updated to support Java 21

### Core Components

#### 1. AutotaskTicketingPlugin
The main plugin class implementing the OpenNMS `TicketingPlugin` interface:
- `get(String ticketId)` - Retrieves a ticket from Autotask
- `saveOrUpdate(Ticket ticket)` - Creates new tickets or updates existing ones
- Handles async operations and error management
- Maps between OpenNMS and Autotask data models

#### 2. AutotaskApiClient  
HTTP client for Autotask REST API communication:
- Uses OkHttp3 for reliable HTTP connections
- Implements proper Autotask authentication headers
- Supports all CRUD operations (Create, Read, Update)
- Returns CompletableFuture for async operations

#### 3. Model Classes
- **AutotaskTicket** - Represents Autotask ticket structure with JSON serialization
- **AutotaskTicketResponse** - Wrapper for API responses including pagination
- Both classes include comprehensive field mappings for Autotask API

#### 4. AutotaskTicketingConfiguration
Configuration management class for all plugin settings:
- Authentication parameters (API integration code, username, secret)
- Default ticket values (company ID, queue ID, priority, etc.)
- Plugin behavior settings (timeouts, auto-close options)

### API Integration

#### Authentication
Autotask REST API requires three authentication headers:
```
ApiIntegrationcode: YOUR_API_INTEGRATION_CODE
UserName: YOUR_USERNAME  
Secret: YOUR_SECRET_KEY
```

#### Base URL
```
https://webservices6.autotask.net/ATServicesRest/v1.0/
```

#### Supported Operations
- **GET** `/Tickets/{id}` - Retrieve ticket details
- **POST** `/Tickets` - Create new ticket
- **PATCH** `/Tickets` - Update existing ticket

### Status Mapping

#### OpenNMS to Autotask
- `OPEN` → Status 1 (New)
- `CLOSED` → Status 8 (Complete)  
- `CANCELLED` → Status 8 (Complete)

#### Autotask to OpenNMS
- Status 1 (New) → `OPEN`
- Status 5-7 (In Progress/Waiting) → `OPEN`
- Status 8 (Complete) → `CLOSED`
- Status 9 (Cancelled) → `CANCELLED`

### Priority Mapping
Based on OpenNMS alarm severity attributes:
- `critical`/`major` → Priority 1 (High)
- `minor` → Priority 2 (Medium)  
- `warning`/`normal`/`cleared` → Priority 3 (Low)

## Configuration

### Blueprint Configuration
The plugin is configured via OSGi Blueprint in `blueprint.xml`:
- Property placeholder for configuration file
- Bean definitions for plugin components  
- Service registration for TicketingPlugin interface

### Configuration File
Place configuration in `$OPENNMS_HOME/etc/com.company.autotaskticketplugin.cfg`:

```properties
# Authentication
autotask.baseUrl=https://webservices6.autotask.net/ATServicesRest/v1.0/
autotask.apiIntegrationCode=YOUR_API_INTEGRATION_CODE
autotask.userName=YOUR_USERNAME
autotask.secret=YOUR_SECRET

# Required Defaults
autotask.defaultCompanyId=YOUR_COMPANY_ID
autotask.defaultQueueId=YOUR_QUEUE_ID  
autotask.defaultContactId=YOUR_CONTACT_ID

# Optional Settings
autotask.defaultTicketCategory=1
autotask.defaultSource=6
autotask.defaultTicketType=1
autotask.defaultPriority=3
autotask.autoClose=false
autotask.updateDescription=true
autotask.timeoutSeconds=30
```

## Installation

1. Build the plugin:
   ```bash
   mvn clean package -DskipTests
   ```

2. Copy the JAR file to OpenNMS:
   ```bash
   cp plugin/target/autotask-ticket-plugin-plugin-0.1.0-SNAPSHOT.jar $OPENNMS_HOME/deploy/
   ```

3. Create configuration file:
   ```bash
   cp plugin/src/main/resources/com.company.autotaskticketplugin.cfg.example $OPENNMS_HOME/etc/com.company.autotaskticketplugin.cfg
   ```

4. Edit configuration with your Autotask credentials and defaults

5. Restart OpenNMS

## Usage

Once installed and configured, the plugin will automatically:

1. **Create tickets** when new alarms are generated in OpenNMS
2. **Update tickets** when alarm status changes (acknowledged, cleared, etc.)
3. **Retrieve tickets** when OpenNMS queries for ticket information

### Alarm-to-Ticket Mapping

- **Ticket Title**: Alarm summary/description
- **Ticket Description**: Detailed alarm information including attributes
- **External ID**: OpenNMS alarm ID for correlation
- **Company/Queue/Contact**: From configuration defaults
- **Priority**: Mapped from alarm severity
- **Status**: Mapped from alarm state

## Development Notes

### Package Structure
```
com.company.autotaskticketplugin/
├── AutotaskTicketingPlugin.java     # Main plugin implementation
├── AutotaskApiClient.java           # HTTP API client
├── AutotaskTicketingConfiguration.java # Configuration management
└── model/
    ├── AutotaskTicket.java          # Ticket data model
    └── AutotaskTicketResponse.java  # API response model
```

### Legacy Components
The plugin maintains backward compatibility with existing components:
- `AlarmForwarder` - Original alarm forwarding logic
- `TopologyForwarder` - Network topology forwarding
- `WebhookHandler` - REST endpoint for webhooks
- These can be removed in future versions if not needed

### Dependencies
- OpenNMS Integration API v1 - For TicketingPlugin interface
- OkHttp3 - HTTP client for API communication
- Jackson - JSON serialization/deserialization
- SLF4J - Logging framework

## Troubleshooting

### Common Issues

1. **Authentication Failures**
   - Verify API integration code, username, and secret
   - Check Autotask API permissions

2. **Configuration Errors** 
   - Ensure all required fields are configured
   - Validate company/queue/contact IDs exist in Autotask

3. **Network Issues**
   - Check connectivity to `webservices6.autotask.net`
   - Verify firewall rules allow HTTPS traffic

### Logging
Enable debug logging by adding to `$OPENNMS_HOME/etc/log4j2.xml`:
```xml
<Logger name="com.company.autotaskticketplugin" level="DEBUG"/>
```

## Future Enhancements

Potential improvements for future releases:
- Support for custom field mappings
- Bulk ticket operations
- Ticket attachments support
- Advanced filtering and search capabilities
- Integration with Autotask webhooks for real-time updates
- Custom Autotask API endpoint configuration