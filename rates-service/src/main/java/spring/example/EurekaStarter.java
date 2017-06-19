package spring.example;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 *
 * @author dmihalishin@gmail.com
 */
@Component
public class EurekaStarter {

	private final ApplicationInfoManager applicationInfoManager;
	private final EurekaClient eurekaClient;

	@Autowired
	public EurekaStarter(ApplicationInfoManager applicationInfoManager,
									  EurekaClient eurekaClient) {
		this.applicationInfoManager = applicationInfoManager;
		this.eurekaClient = eurekaClient;
	}


	@PostConstruct
	public void init(){
		applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
	}

	@PreDestroy
	public void destroy() {
		applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
		eurekaClient.shutdown();
	}

}
