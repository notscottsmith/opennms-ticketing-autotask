# OpenNMS Autotask Ticketing Plugin - Deployment Guide

## 🚀 Complete Deployment Instructions

### Prerequisites
- OpenNMS Horizon 31.0.0+ or Meridian 2023.1.0+
- Autotask PSA account with API access
- Administrative access to OpenNMS server

---

## 📦 Step 1: Deploy the KAR File

### 1.1 Copy KAR to OpenNMS
```bash
# Copy the built KAR file to OpenNMS deploy directory
cp assembly/kar/target/opennms-autotask-ticket-plugin.kar $OPENNMS_HOME/deploy/
```

### 1.2 Verify Installation
Check that the plugin was deployed successfully:
```bash
# Check OpenNMS logs
tail -f $OPENNMS_HOME/logs/karaf.log

# Look for messages like:
# "Bundle org.opennms.plugins.opa.ticketing.autotask.plugin [XXX] started"
```

---

## ⚙️ Step 2: Configure Autotask Connection

### 2.1 Create Configuration File
```bash
# Copy the example configuration
cp autotask.properties.example $OPENNMS_HOME/etc/autotask.properties

# Edit with your Autotask credentials
vi $OPENNMS_HOME/etc/autotask.properties
```

### 2.2 Required Configuration Parameters
Edit `$OPENNMS_HOME/etc/autotask.properties`:

```properties
# Autotask API Connection (Required)
autotask.baseUrl=https://webservices6.autotask.net/ATServicesRest/v1.0/
autotask.apiIntegrationCode=YOUR_API_INTEGRATION_CODE
autotask.userName=YOUR_USERNAME
autotask.secret=YOUR_SECRET

# Default Ticket Settings (Required)
autotask.defaultCompanyId=YOUR_COMPANY_ID
autotask.defaultQueueId=YOUR_QUEUE_ID  
autotask.defaultContactId=YOUR_CONTACT_ID

# Optional Settings (customize as needed)
autotask.defaultTicketCategory=1
autotask.defaultSource=6
autotask.defaultTicketType=1
autotask.defaultPriority=3
autotask.autoClose=false
autotask.updateDescription=true
autotask.timeoutSeconds=30
```

---

## 🎯 Step 3: Enable Ticketing in OpenNMS

### 3.1 Configure OpenNMS Properties
Edit `$OPENNMS_HOME/etc/opennms.properties`:

```properties
# Enable ticketing system
opennms.ticketer.plugin=autotask

# Optional: Enable ticketing for alarms
opennms.alarmTroubleTicketEnabled=true
```

### 3.2 Configure Ticketing Options (Optional)
Edit `$OPENNMS_HOME/etc/ticketd-configuration.xml` if you want to customize ticketing behavior:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ticketd-configuration>
    <!-- Enable automatic ticket creation for critical alarms -->
    <enabled>true</enabled>
    
    <!-- Ticket creation delay to avoid ticket spam -->
    <delay>5m</delay>
    
    <!-- Maximum number of tickets per alarm -->
    <max-concurrent-tickets>1</max-concurrent-tickets>
</ticketd-configuration>
```

---

## 🔄 Step 4: Restart OpenNMS

```bash
# Restart OpenNMS to apply configuration changes
systemctl restart opennms

# Or using service command
service opennms restart

# For Docker deployments
docker restart opennms
```

---

## ✅ Step 5: Verify Installation

### 5.1 Check Plugin Status
```bash
# Connect to Karaf shell
ssh -p 8101 admin@localhost

# In Karaf shell, verify the plugin is loaded:
karaf@opennms> bundle:list | grep autotask
karaf@opennms> feature:list | grep autotask
```

### 5.2 Test Shell Commands
```bash
# Test the Autotask shell commands
karaf@opennms> autotask:stats
karaf@opennms> autotask:push-topology
```

### 5.3 Check Ticketing Service
```bash
# Verify ticketing plugin is registered
karaf@opennms> service:list | grep TicketingPlugin
```

---

## 🎫 Step 6: Test Ticket Creation

### 6.1 Manual Ticket Test
Create a test alarm and verify ticket creation:

```bash
# Create a test event/alarm via REST API
curl -X POST \
  http://localhost:8980/opennms/rest/events \
  -H 'Content-Type: application/xml' \
  -d '<event>
    <uei>uei.opennms.org/test/autotask</uei>
    <source>REST-API</source>
    <severity>Major</severity>
    <logmsg>Test alarm for Autotask ticketing</logmsg>
    <descr>Testing Autotask integration</descr>
  </event>'
```

### 6.2 Verify in Autotask
- Log into your Autotask PSA
- Check Service Desk > Tickets
- Look for automatically created tickets

---

## 🔧 Troubleshooting

### Check Logs
```bash
# OpenNMS main log
tail -f $OPENNMS_HOME/logs/opennms.log

# Karaf log for plugin messages
tail -f $OPENNMS_HOME/logs/karaf.log

# Look for Autotask-related messages
grep -i autotask $OPENNMS_HOME/logs/*.log
```

### Common Issues

1. **"Plugin not found" error**
   - Verify KAR file is in `$OPENNMS_HOME/deploy/`
   - Check `karaf.log` for deployment errors

2. **"Configuration not found" error**
   - Ensure `autotask.properties` exists in `$OPENNMS_HOME/etc/`
   - Verify all required properties are set

3. **"Authentication failed" error**
   - Verify Autotask API credentials
   - Check Autotask API integration is active
   - Confirm baseUrl is correct for your zone

4. **"Timeout" errors**
   - Increase `autotask.timeoutSeconds` in configuration
   - Check network connectivity to Autotask

### Plugin Commands
```bash
# View plugin statistics
karaf@opennms> autotask:stats

# Push topology data to Autotask
karaf@opennms> autotask:push-topology

# Check configuration reload
karaf@opennms> config:list | grep autotask
```

---

## 🔄 Configuration Updates

The plugin supports hot-reload of configuration:
- Edit `$OPENNMS_HOME/etc/autotask.properties`
- Changes are automatically detected and applied
- No restart required for configuration changes

---

## 📋 Summary Checklist

- [ ] KAR file deployed to `$OPENNMS_HOME/deploy/`
- [ ] Configuration file created: `$OPENNMS_HOME/etc/autotask.properties`
- [ ] OpenNMS configured with `opennms.ticketer.plugin=autotask`
- [ ] OpenNMS restarted
- [ ] Plugin bundle loaded and started
- [ ] Shell commands working: `autotask:stats`
- [ ] Test ticket created successfully
- [ ] Autotask integration verified

Your Autotask ticketing plugin is now ready for production use! 🎉