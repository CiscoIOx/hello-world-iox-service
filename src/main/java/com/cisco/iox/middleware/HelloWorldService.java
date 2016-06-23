package com.cisco.iox.middleware;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.iox.mlib.messaging.AsyncResponse;
import com.cisco.iox.mlib.messaging.Operation;
import com.cisco.iox.mlib.messaging.Param;
import com.cisco.iox.mlib.messaging.RPC;
import com.cisco.iox.mlib.messaging.Service;
import com.cisco.iox.mlib.messaging.ServiceException;
import com.cisco.iox.mlib.messaging.Topic;
import com.cisco.iox.mlib.messaging.util.ThreadPoolExecutor;
import com.cisco.iox.servicesdk.AbstractService;
import com.codahale.metrics.Counter;

@Service(HelloWorldService.SERVICE_NAME)
public class HelloWorldService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(HelloWorldService.class);

	
	static final String SERVICE_NAME = "urn:cisco:custom:service:hello-world";


	private Counter publishCounter;


	private Counter greetingCounter;

	public HelloWorldService() {
		super(SERVICE_NAME);
	}

	/**
	 * @return
	 */
	@Operation("say-greeting")
    public String sayGreeting(){
		greetingCounter.inc();
        return "Oh Hi there! Welcome to Middleware Services! ";
	}
	
	@Operation("findPrimeNumber")
	public AsyncResponse<Long> findPrimeNumber(final @Param("n") int n){
	    final AsyncResponse<Long> asyncResponse = RPC.createAsyncResponse(Long.class);
	    ThreadPoolExecutor.getInstance().submit(new Runnable(){
	    	
	        public void run(){
	            try{
	                long nthPrime = 0l;
	                // compute n-th prime number
	                asyncResponse.complete(nthPrime);
	            }catch(Exception ex){
	                asyncResponse.completeExceptionally(ex);
	            }
	        }
	    });
	    return asyncResponse;
	}
	
	/**
	 * @param message
	 */
	@Operation("publish-greeting")
    public void publishGreeting(final @Param("message") String message){
		Topic topic = getPubSub().getTopic("greet");
		getPubSub().publish(topic, message);
		publishCounter.inc();
	}
	
	@Override
	protected void onServiceStart() throws ServiceException {
		log.info("Starting Hello World Service");
		// create counter for service metrics
		
		// publishCounter is used whenever publish to a topic is done
		publishCounter = getMetricsRegistry().counter("publishGreeting");
		
		// greeting counter is used whenever sayGreeting method is invoked
		greetingCounter = getMetricsRegistry().counter("sayGreeting");
	}

	@Override
	protected void onServiceInit() throws ServiceException {
		log.info("Initializing Hello World Service");
		Map<String, Map<String, String>> configProperties = getConfigProperties();
	}

	@Override
	protected void onServiceStop() throws ServiceException {
		log.info("Stopping Hello World Service");
	}
}
