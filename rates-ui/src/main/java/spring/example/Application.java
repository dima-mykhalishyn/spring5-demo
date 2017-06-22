package spring.example;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * UI Runner
 *
 * @author dmihalishin@gmail.com
 */
@SpringBootApplication(scanBasePackages = "spring.example")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public EurekaClientConfig eurekaInstanceConfig() {
		return new DefaultEurekaClientConfig();
	}

	@Bean
	public EurekaInstanceConfig instanceConfig() {
		return new MyDataCenterInstanceConfig();
	}

	@Bean
	public ApplicationInfoManager applicationInfoManager(EurekaInstanceConfig instanceConfig) {
		final InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
		return new ApplicationInfoManager(instanceConfig, instanceInfo);
	}

	@Bean
	public EurekaClient eurekaClient(final ApplicationInfoManager applicationInfoManager, final EurekaClientConfig clientConfig) {
		return new DiscoveryClient(applicationInfoManager, clientConfig);
	}

	@Bean
	public ZoneAwareLoadBalancer<DiscoveryEnabledServer> ratesServiceLoadBalancer(final EurekaClient eurekaClient,
																											@Value("${eurika.rates.service.name}") final String name) {
		final IRule rule = new AvailabilityFilteringRule();
		ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList(name, () -> eurekaClient);
		ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<>();
		return LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
				.withDynamicServerList(list)
				.withRule(rule)
				.withServerListFilter(filter)
				.buildDynamicServerListLoadBalancer();
	}
}
