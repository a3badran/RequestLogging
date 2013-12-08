/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Metrics writer to track statistics about request.
 *
 * @author a3badran
 */
public class MetricsWriter implements Writer {

    private static final Log log = LogFactory.getLog(Writer.LOGGER);

    private static final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

    private String appName = "app";
    private int sampleWindow = 100;
    private float sampleRate = 0.5f;
    private boolean writeSubScopes = false;
    private final ConcurrentHashMap<String, AtomicLong> appTotalMetrics = new ConcurrentHashMap<String, AtomicLong>();
    private final ConcurrentHashMap<String, AtomicLong> scopeTotalMetrics = new ConcurrentHashMap<String, AtomicLong>();
    private final ConcurrentHashMap<String, DescriptiveStatistics> sampleMetrics = new ConcurrentHashMap<String, DescriptiveStatistics>();
    private final Random random = new Random(System.currentTimeMillis());

    //---------------------------------------------------------
    // Constructor
    //---------------------------------------------------------

    public MetricsWriter() {
        // empty
    }

    //---------------------------------------------------------
    // Public Implementation
    //---------------------------------------------------------

    @Override
    public void write(RequestScope scope) {
        // this method must not throw any exceptions
        try {
            updateMetrics(scope, appName, scope.getEndTime() - scope.getStartTime(), 1);

            if (writeSubScopes && scope.getSubScopes() != null && !scope.getSubScopes().isEmpty() ) {
                for (Map.Entry<String, RequestScope> entry : scope.getSubScopes().entrySet()) {
                    RequestScope subScope = entry.getValue();
                    updateMetrics(subScope, appName + "." + scope.getName(), subScope.getTotalTime(), subScope.getCount());
                }
            }
        } catch (RuntimeException e) {
            log.warn("Error updating metrics", e);
        }
    }

    /**
     * Example in json of what this will return:
     *
     * [
     *   app.getCustomer: {
     *      50p: 972,
     *      90p: 1515,
     *      99p: 1579,
     *      avg: 798,
     *      max: 1580,
     *      min: 7,
     *      sampleCount: 100,
     *      totalCount: 795,
     *      totalTime: 509371
     *   },
     *   app.getOrder: {
     *      50p: 2,
     *      90p: 601,
     *      99p: 601,
     *      avg: 201,
     *      max: 601,
     *      min: 2,
     *      sampleCount: 3,
     *      totalCount: 3,
     *      totalTime: 605
     *   }
     * ]
     * @param name
     * @return metrics for the given name, if null or empty, it returns all metrics
     *
     */
    public Map<String, Map<String, Long>> getMetrics() {
        return getMetricsByName(null);
    }
    
    public Map<String, Map<String, Long>> getMetricsByName(String name) {
        Map<String, Map<String, Long>> groupedMetrics = new HashMap<String, Map<String, Long>>();
        Map<String, Long> metrics = getAllMetrics();

        for (Entry<String, Long> entry : metrics.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("\\.");

            if (parts.length < 2) {
                continue;
            }

            String newKey = key.substring(0, key.length() - (parts[parts.length -1].length() + 1));

            if (!Strings.isNullOrEmpty(name) && !newKey.equalsIgnoreCase(name)) {
                continue;
            }

            if (groupedMetrics.get(newKey) == null) {
                Map<String, Long> newValue = new TreeMap<String, Long>();
                newValue.put(parts[parts.length -1], entry.getValue());
                groupedMetrics.put(newKey, newValue);
            } else {
                groupedMetrics.get(newKey).put(parts[parts.length -1], entry.getValue());
            }
        }

        return groupedMetrics;
    }

    public Map<String, Long> getAppTotalMetrics() {
        Map<String,Long> map = Maps.newHashMap();
        for (Entry<String,AtomicLong> entry : appTotalMetrics.entrySet()) {
               map.put(entry.getKey(), entry.getValue().longValue());
        }

        return map;
    }
    
    public void resetTotalMetrics() {
        this.scopeTotalMetrics.clear();
        this.appTotalMetrics.clear();
    }

    public void resetSampleMetrics() {
        this.sampleMetrics.clear();
    }

    @Override
    public String toString() {
        return gson.toJson(getMetrics());
    }

    //---------------------------------------------------------
    // Private methods
    //---------------------------------------------------------

    private Map<String, Long> getAllMetrics() {
        Map<String, Long> metrics = new HashMap<String, Long>();
        for (Entry<String, DescriptiveStatistics> entry : sampleMetrics.entrySet()) {
            // create a copy to reduce locking
            DescriptiveStatistics stats = entry.getValue().copy();
            metrics.put(entry.getKey() + ".sampleCount", (long) stats.getN());
            metrics.put(entry.getKey() + ".max", (long) stats.getMax());
            metrics.put(entry.getKey() + ".min", (long) stats.getMin());
            metrics.put(entry.getKey() + ".avg", (long) stats.getMean());
            metrics.put(entry.getKey() + ".50p", (long) stats.getPercentile(50));
            metrics.put(entry.getKey() + ".90p", (long) stats.getPercentile(90));
            metrics.put(entry.getKey() + ".99p", (long) stats.getPercentile(99));
        }

        for (Entry<String, AtomicLong> entry : scopeTotalMetrics.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue().longValue());
        }

        for (Entry<String, AtomicLong> entry : appTotalMetrics.entrySet()) {
            metrics.put(entry.getKey(), entry.getValue().longValue());
        }

        return metrics;
    }
    
    private void updateMetrics(RequestScope scope, String prefixName, long tt, long count) {
        String name = Strings.isNullOrEmpty(prefixName) ? scope.getName() : prefixName + "." + scope.getName();

        String metricTotalCount = name + ".totalCount";
        scopeTotalMetrics.putIfAbsent(metricTotalCount, new AtomicLong(0));
        scopeTotalMetrics.get(metricTotalCount).addAndGet(count);

        String serviceTotalCount = appName + ".totalCount";
        appTotalMetrics.putIfAbsent(serviceTotalCount, new AtomicLong(0));
        appTotalMetrics.get(serviceTotalCount).addAndGet(count);

        if (!Strings.isNullOrEmpty(scope.getError())) {
            String metricErrorCount = name + ".errorCount";
            scopeTotalMetrics.putIfAbsent(metricErrorCount, new AtomicLong(0));
            scopeTotalMetrics.get(metricErrorCount).addAndGet(1);

            String serviceErrorCount = appName + ".errorCount";
            appTotalMetrics.putIfAbsent(serviceErrorCount, new AtomicLong(0));
            appTotalMetrics.get(serviceErrorCount).addAndGet(1);
        }
        else if (!Strings.isNullOrEmpty(scope.getWarninge())) {
            String metricWarningCount = name + ".warningCount";
            scopeTotalMetrics.putIfAbsent(metricWarningCount, new AtomicLong(0));
            scopeTotalMetrics.get(metricWarningCount).addAndGet(1);

            String serviceWarningCount = appName + ".warningCount";
            appTotalMetrics.putIfAbsent(serviceWarningCount, new AtomicLong(0));
            appTotalMetrics.get(serviceWarningCount).addAndGet(1);
        }

        String metricTotalTime = name + ".totalTime";
        scopeTotalMetrics.putIfAbsent(metricTotalTime, new AtomicLong(0));
        scopeTotalMetrics.get(metricTotalTime).addAndGet(tt);

        // sample data
        if (random.nextFloat() <= sampleRate) {
            sampleMetrics.putIfAbsent(name, new SynchronizedDescriptiveStatistics(sampleWindow));
            sampleMetrics.get(name).addValue(tt);
        }
    }

    //---------------------------------------------------------
    // IoC
    //---------------------------------------------------------

    public String getServiceName() {
        return appName;
    }

    public void setServiceName(String serviceName) {
        this.appName = serviceName;
    }

    public int getSampleWindow() {
        return sampleWindow;
    }

    public void setSampleWindow(int sampleWindow) {
        this.sampleWindow = sampleWindow;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public boolean isRecordSubScopes() {
        return writeSubScopes;
    }

    public void setRecordSubScopes(boolean recordSubScopes) {
        this.writeSubScopes = recordSubScopes;
    }

}
