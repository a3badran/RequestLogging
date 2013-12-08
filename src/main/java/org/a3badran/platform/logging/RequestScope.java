/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * RequestrScope is a class to capture request data and timing within an execution of a thread. See <code>RequestLogger</code> to learn
 * how to log requests in your application using this class.  This class is package protected because you never want
 * to create an instance of this class directly, rather you should use <code>RequestLogger.startScope(String name)</code>
 *
 * This class is not thread safe.
 *
 * @author a3badran
 *
 */
public class RequestScope {

    /* unique name identifying this scope */
    private String scopeName;

    /* start time of the request */
    private long startTime;
    
    /* end time of the request */
    private long endTime;

    /* Total time elapsed between start and end of a scope */
    private long totalTime;

    /* Total count of how many times this scope is called */
    private long count;

    /* Using LinkedHashMap to maintain key insertion order - helps to visualize call order */
    private Map<String, RequestScope> subScopes;

    /* Maintain additional info within a scope */
    private List<String> info;
    
    /* Error message */
    private String error;

    /* Warning message*/
    private String warning;
    
    /* to print object in JSON format */
    private static final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    
    /**
     * package protected constructor
     * @param scopeName
     */
    RequestScope(String scopeName) {
        this.scopeName = scopeName;
        this.startTime = System.currentTimeMillis();
    }

    public String getName() {
        return this.scopeName;
    }

    void setName(String newName) {
        this.scopeName = newName;
    }

    public long getStartTime() {
        return this.startTime;
    }

    void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public long getEndTime() {
        return this.endTime;
    }

    public long getCount() {
        return this.count;
    }

    public List<String> getInfo() {
        return info;
    }

    public String getError() {
        return error;
    }

    public void addError(String errorMessage) {
        this.error = errorMessage;
    }

    public String getWarninge() {
        return warning;
    }

    public void addWarning(String warningMessage) {
        this.warning = warningMessage;
    }

    void incrementCount() {
        this.count++;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public Map<String, RequestScope> getSubScopes() {
        return subScopes;
    }

    void incrementTotalTime() {
        this.totalTime += this.endTime - this.startTime;
    }

    void addInfo(String key, String value) {
        if (this.info ==  null) {
            this.info = new LinkedList<String>();
        }
        this.info.add(key + ": " + value);
    }

    /**
     * Add sub scopes. Track all calls to the same scope
     * to get call count and average time.
     * @param scope
     */
    void addSubScope(RequestScope scope) {
        if (this.subScopes == null) {
            this.subScopes = new LinkedHashMap<String, RequestScope>();
        }

        scope.incrementCount();
        scope.incrementTotalTime();

        if (!this.subScopes.containsKey(scope.getName())) {
            this.subScopes.put(scope.getName(), scope);
        }
    }

    /**
     * Return subscope with the given name if one exist, otherwise
     * return null.
     *
     * @param name
     * @return
     */
    public RequestScope getSubScope(String name) {
        return (this.subScopes == null) ? null : this.subScopes.get(name);
    }

    void endScope() {
        this.endTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
