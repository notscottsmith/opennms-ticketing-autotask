package org.opennms.plugins.autotask.shell;

import java.util.concurrent.TimeUnit;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.opennms.plugins.autotask.AlarmForwarder;

@Command(scope = "autotask", name = "stats", description = "Show statistics for Autotask plugin.")
@Service
public class StatsCommand implements Action {

    @Reference
    private AlarmForwarder forwarder;

    @Override
    public Object execute() {
        final MetricRegistry metrics = forwarder.getMetrics();
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.report();
        return null;
    }
}