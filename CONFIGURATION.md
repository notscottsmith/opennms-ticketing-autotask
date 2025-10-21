# Autotask Ticketing Plugin Configuration

## Configuration File Location

The Autotask ticketing plugin uses standard OpenNMS configuration practices:

**Configuration file:** `$OPENNMS_HOME/etc/autotask.properties`

## Setup Instructions

1. Copy the example configuration file:
   ```bash
   cp autotask.properties.example $OPENNMS_HOME/etc/autotask.properties
   ```

2. Edit the configuration file with your Autotask credentials and settings:
   ```bash
   vi $OPENNMS_HOME/etc/autotask.properties
   ```

3. Restart OpenNMS after configuration changes:
   ```bash
   systemctl restart opennms
   ```

## Configuration Properties

The configuration file follows standard Java properties format with key=value pairs.

### Required Properties

| Property | Description | Example |
|----------|-------------|---------|
| `autotask.baseUrl` | Autotask REST API base URL | `https://webservices6.autotask.net/ATServicesRest/v1.0/` |
| `autotask.apiIntegrationCode` | Your API Integration Code | `YOUR_API_INTEGRATION_CODE` |
| `autotask.userName` | Autotask username | `YOUR_USERNAME` |
| `autotask.secret` | Autotask secret key | `YOUR_SECRET` |
| `autotask.defaultCompanyId` | Default company ID for tickets | `YOUR_COMPANY_ID` |
| `autotask.defaultQueueId` | Default queue ID for tickets | `YOUR_QUEUE_ID` |
| `autotask.defaultContactId` | Default contact ID for tickets | `YOUR_CONTACT_ID` |

### Optional Properties

| Property | Default | Description |
|----------|---------|-------------|
| `autotask.defaultTicketCategory` | `1` | Default ticket category |
| `autotask.defaultSource` | `6` | Default source (6=API) |
| `autotask.defaultTicketType` | `1` | Default ticket type (1=Service Request) |
| `autotask.defaultPriority` | `3` | Default priority (3=Medium) |
| `autotask.autoClose` | `false` | Auto-close resolved tickets |
| `autotask.updateDescription` | `true` | Update ticket description on changes |
| `autotask.timeoutSeconds` | `30` | API timeout in seconds |

## Karaf Shell Commands

Once configured, you can use these commands in the OpenNMS Karaf shell:

- `autotask:stats` - Show statistics for Autotask plugin
- `autotask:push-topology` - Push network topology to Autotask

## Configuration Updates

The plugin supports hot-reload of configuration. Changes to `autotask.properties` will be automatically detected and applied without requiring a restart.