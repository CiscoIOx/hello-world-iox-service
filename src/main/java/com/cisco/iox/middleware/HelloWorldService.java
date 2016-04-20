package com.cisco.iox.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.iox.mlib.messaging.Operation;
import com.cisco.iox.mlib.messaging.Param;
import com.cisco.iox.mlib.messaging.PubSub;
import com.cisco.iox.mlib.messaging.Service;
import com.cisco.iox.mlib.messaging.ServiceException;
import com.cisco.iox.mlib.messaging.Topic;
import com.cisco.iox.servicesdk.AbstractService;
import com.codahale.metrics.Counter;

@Service(HelloWorldService.SERVICE_NAME)
public class HelloWorldService extends AbstractService {

	private static final Logger log = LoggerFactory.getLogger(HelloWorldService.class);

	
	static final String SERVICE_NAME = "custom:service:hello-world";


	private Counter publishCounter;


	private Counter greetingCounter;

	public HelloWorldService() {
		super(SERVICE_NAME);
	}

	@Operation("say-greeting")
    public String sayGreeting(@Param("userName") String userName){
		greetingCounter.inc();
        return "Hello "+userName;
	}
	
	@Operation("publish")
    public void publishGreeting(@Param("userName") String userName){
		Topic topic = PubSub.getTopic("hello");
		PubSub.publish(topic, "Hello "+userName);
		publishCounter.inc();
	}

	@Override
	protected void onServiceStart() throws ServiceException {
		log.info("Starting Hello World Service");
		// create counter for service metrics
		
		// publishCounter is used whenever publish to a topic is done
		publishCounter = getMetricsRegistry().counter("hello");
		
		// greeting counter is used whenever sayGreeting method is invoked
		greetingCounter = getMetricsRegistry().counter("greeting");
	}

	@Override
	protected void onServiceInit() throws ServiceException {
		log.info("Initializing Hello World Service");
	}

	@Override
	protected void onServiceStop() throws ServiceException {
		log.info("Stopping Hello World Service");
	}
}
