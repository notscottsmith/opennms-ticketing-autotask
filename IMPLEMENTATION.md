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

### Project Structure
This is a multi-module Maven project with the following structure:
- **Parent** (`org.opennms.plugins.opa.ticketing.autotask`) - Root POM with shared configuration
- **Plugin** (`org.opennms.plugins.opa.ticketing.autotask.plugin`) - Core plugin implementation
- **Karaf Features** (`org.opennms.plugins.opa.ticketing.autotask.karaf-features`) - OSGi feature definitions
- **Assembly** (`org.opennms.plugins.opa.ticketing.autotask.assembly`) - Packaging parent
- **Assembly KAR** (`org.opennms.plugins.opa.ticketing.autotask.assembly.kar`) - Final KAR artifact

### Naming Conventions
- **Maven Names**: Follow pattern "OpenNMS :: Plugins :: Autotask :: \<Component\>"
- **Package Structure**: `org.opennms.plugins.autotask.*`
- **Configuration**: Standard OpenNMS properties file: `autotask.properties`
- **Karaf Commands**: Simplified scope "autotask" (e.g., `autotask:stats`)

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
- **Alert** - Represents alert/alarm data structure for forwarding
- **Topology** - Network topology data model for Autotask integration  
- **Link** - Network link information for topology mapping

*Note: Dedicated AutotaskTicket and AutotaskTicketResponse models will be implemented as part of the full TicketingPlugin interface completion.*

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
- Property placeholder with `persistent-id="autotask"` for configuration file
- Bean definitions for plugin components  
- Service registration for TicketingPlugin interface
- Shell command registration with scope="autotask"
- Legacy forwarder services for backwards compatibility

### Configuration File
Place configuration in `$OPENNMS_HOME/etc/autotask.properties`:

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
   mvn clean install -DskipTests
   ```

2. Deploy the KAR file to OpenNMS:
   ```bash
   cp assembly/kar/target/opennms-autotask-ticket-plugin.kar $OPENNMS_HOME/deploy/
   ```

3. Create configuration file:
   ```bash
   cp plugin/src/main/resources/autotask.properties.example $OPENNMS_HOME/etc/autotask.properties
   ```

4. Edit configuration with your Autotask credentials and defaults

5. Configure OpenNMS to use the ticketing plugin:
   ```bash
   echo "opennms.ticketer.plugin=autotask" >> $OPENNMS_HOME/etc/opennms.properties
   ```

6. Restart OpenNMS

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
org.opennms.plugins.autotask/
├── AutotaskTicketingPlugin.java     # Main plugin implementation
├── AutotaskTicketingConfiguration.java # Configuration management
├── AutotaskApiClient.java           # HTTP API client (legacy)
├── AlarmForwarder.java              # Legacy alarm forwarding
├── TopologyForwarder.java           # Legacy topology forwarding
├── EventConfExtension.java          # Event configuration extension
├── WebhookHandler.java              # REST webhook interface
├── WebhookHandlerImpl.java          # REST webhook implementation
├── model/
│   ├── Alert.java                   # Alert data model
│   ├── Link.java                    # Network link model
│   └── Topology.java                # Topology data model
└── shell/
    ├── StatsCommand.java            # Karaf shell stats command
    └── TopologyCommand.java         # Karaf shell topology command
```

### Additional Components

#### Shell Commands
- **StatsCommand** (`autotask:stats`) - Display plugin statistics and status
- **TopologyCommand** (`autotask:push-topology`) - Push network topology to Autotask

#### Legacy Components
The plugin maintains backward compatibility with existing components:
- **AlarmForwarder** - Legacy alarm forwarding logic with AlarmLifecycleListener
- **TopologyForwarder** - Network topology forwarding capabilities  
- **WebhookHandler/WebhookHandlerImpl** - REST endpoint for incoming webhooks
- **EventConfExtension** - Custom event definitions bundled with the plugin
- These provide additional functionality beyond core ticketing

### Dependencies
- **OpenNMS Integration API v1** - For TicketingPlugin interface and core services
- **OkHttp3** - HTTP client for API communication  
- **Jackson** - JSON serialization/deserialization
- **SLF4J** - Logging framework
- **Apache Karaf** - OSGi runtime and feature management

### Maven Coordinates
```xml
<groupId>org.opennms.plugins.opa.ticketing</groupId>
<artifactId>org.opennms.plugins.opa.ticketing.autotask</artifactId>  
<version>0.1.0-SNAPSHOT</version>
```

### Deployment Artifact
The final deployable artifact is a Karaf KAR (Karaf Archive) file:
- **File**: `opennms-autotask-ticket-plugin.kar`
- **Location**: `assembly/kar/target/opennms-autotask-ticket-plugin.kar`
- **Deployment**: Copy to `$OPENNMS_HOME/deploy/`

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
<Logger name="org.opennms.plugins.autotask" level="DEBUG"/>
```

### Karaf Shell Commands
Test and monitor the plugin using Karaf shell commands:
```bash
# View plugin statistics
karaf@opennms> autotask:stats

# Push topology data to Autotask  
karaf@opennms> autotask:push-topology

# Check loaded features
karaf@opennms> feature:list | grep autotask

# Verify bundle status
karaf@opennms> bundle:list | grep autotask
```

## Current Implementation Status

### ✅ Completed
- Java 21 LTS upgrade and build configuration
- Multi-module Maven project structure
- Package restructuring to `org.opennms.plugins.autotask`
- Simplified naming conventions following OpenNMS standards
- Configuration management with `autotask.properties`
- OSGi Blueprint configuration with hot-reload support
- Karaf shell commands (`autotask:stats`, `autotask:push-topology`)
- KAR packaging for deployment
- Legacy forwarder components (AlarmForwarder, TopologyForwarder)
- REST webhook handler infrastructure
- Event configuration extensions

### 🚧 In Progress / Future Work
- Complete TicketingPlugin interface implementation
- AutotaskTicket and AutotaskTicketResponse model classes
- Full Autotask REST API integration
- Comprehensive ticket lifecycle management
- Status and priority mapping implementation
- Error handling and retry logic
- Unit and integration tests

## Future Enhancements

Potential improvements for future releases:
- Support for custom field mappings
- Bulk ticket operations
- Ticket attachments support
- Advanced filtering and search capabilities
- Integration with Autotask webhooks for real-time updates
- Custom Autotask API endpoint configuration
- Enhanced monitoring and metrics collection