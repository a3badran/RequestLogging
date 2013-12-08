==========================================
 Introduction
==========================================
RequestLogging is a library that aims at providing an easy and non-intrusive way to log requests consistently.  
In today's world of web services it is hard to troubelshoot by looking at normal (e.g. log4j) log files to 
track requests and understand what happend when.  This library is meant to help with logging consistent data 
per request including sub requests as one unit along with basic timing and profiling.  It works at a request 
level (per thread).  It is not intended 
for low level profiling, but rather for collecting metrics and data pertaining to a request at a high level.  
For example, you will start a scope at the top HTTP servlet handler level, then add other scopes to capture 
expensive operations (i.e. database calls, remote services calls, computationally expensive functions, etc).  
This should help with understanding where time get spent during a request as well as capturing the type, params, 
time and volume of requests your service gets.

==========================================
 Licence
==========================================
See LICENCE.txt

==========================================
 Maven Use
==========================================
All you need is to include the following dependency in your pom.xml

    <dependency>
         <groupId>org.a3badran.platform</groupId>
         <artifactId>request-logging</artifactId>
         <version>${a3badran.version}</version>
    </dependency>

==========================================
 Using
==========================================
There are two ways to use the library.
1.  Through aspect programing
2.  Through direct java method calls

Aspect Programing
------------------------------------------
Initialize LogAspect within your AOP framework.  Then add @LogRequest annotation to methods you want to log.

  @LogRequest("createCustomerOrder")
  public CustomerOrder createCustomerOrder(CustomerOrder order)

You could also use @LogParam annotation to include the values of some params into the request log

  @LogRequest("getCustomerById")
  public Customer getCustomerById(@LogParam("customerId") String customerId)
 
Java Programing
-------------------------------------------
Alternativly you can use direct java code

  public Customer getCustomerById(string customerId) {
     RequestScope scope = RequestLogger.startScope("getCustomerById");
     try {
         RequestLogger.addInfo("customerId", customerId);

         // actual code goes here

     }
     finally {
         RequestLogger.endScope(scope);
     }

Example Log
-------------------------------------------
The getCustomerById examples above will produce the following log (using default writer LogWriter)

    ----------------------------------------------
    StartTime: Sun Dec 08 11:54:23 PST 2011
    EndTime: Sun Dec 08 11:54:23 PST 2011
    Time (ms): 250
    SubRequests: 
    customerId: 1234567890
    Request: getCustomerById

The example above shows the time when the method was called and when it returned.  How long it took 
(in milliseconds).  As well as the customerId that was passed as param to the method.  SubRequests is
empty since no methods (that are annotated with @LogRequest) are called within the scope of getCustomerById.
Let us assume that getCustomerById calls a remote service via method named 
   
    @LogRequest("authenticate")
    public boolean authenticate() 

and makes a DB call via a method named

    @LogReuest("db.getCustomer")
    public CustomerDAO getCustomer(customerId)

After getCustomerById has completed the following is an example of what the log might look like

    ----------------------------------------------
    StartTime: Sun Dec 08 11:54:23 PST 2011
    EndTime: Sun Dec 08 11:54:63 PST 2011
    Time (ms): 4000
    SubRequests: authenticate 1500/1, db.getCustomer 3000/1
    customerId: 1234567890
    Request: getCustomerById

SubRequests captured both calls indicating that authenticate() took about 1.5 second and was called once.
While db.getCustomer took 3 seconds and was also called once.

===========================================
 Logging with Log4j
===========================================
The default writer is LogWriter, which uses common-logging.  To get log4j to work, you simply 
could add the following to your log4j.properties file

  log4j.appender.requestLogger=org.apache.log4j.DailyRollingFileAppender
  log4j.appender.requestLogger.File=${log.dir}/requests.log
  log4j.appender.requestLogger.layout=org.apache.log4j.PatternLayout
  log4j.appender.requestLogger.layout.ConversionPattern=%m
  log4j.logger.requestLogger=INFO, requestLogger
  log4j.additivity.requestLogger=false 
