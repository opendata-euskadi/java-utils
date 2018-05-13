package r01f.cache;

import java.lang.management.ManagementFactory;
import java.util.Set;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;
import r01f.util.types.Strings;

@Slf4j
public class HazelcastManager {
//////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
//////////////////////////////////////////////////////////////////////////////////////////
	public static final String R01_PREFIX = "R01.HAZELCAST.";

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns an existing Hazelcast instance or creates a new one using the config
	 * at the given properties file
	 * @param props
	 * @return
	 */
	public static HazelcastInstance getOrCreateeHazelcastInstance(final DistributedCacheConfig cfg) {
		return HazelcastManager.getOrCreateHazelcastInstance(cfg.getHZConfig());
	}
	/**
	 * Returns an existing Hazelcast instance or creates a new one using the given config
	 * @param config
	 * @return
	 */
	@SuppressWarnings("unused")
	public static HazelcastInstance getOrCreateHazelcastInstance(final Config config) {
		if (config == null) throw new IllegalArgumentException("Configuration for creating HazelCast instance is null");
		
		String instanceName = Strings.customized("{}{}",
												 R01_PREFIX,ManagementFactory.getRuntimeMXBean().getName());
		log.warn("Get or create a hazelcast instance with name={}",
				  instanceName);
		HazelcastInstance outHZInstance = Hazelcast.getHazelcastInstanceByName(instanceName);
		if (outHZInstance == null) {
			log.warn("\t...No instance with name {} found, so a NEW HazelCast instance will be created for RuntimeProcess ID: {}" ,
					  instanceName,ManagementFactory.getRuntimeMXBean().getName());
			config.setInstanceName(instanceName);
			try {
				outHZInstance = Hazelcast.newHazelcastInstance(config);
			} catch(Throwable ex) {
				log.error("Error while creating the Hazelcast instance: {} ",
						  ex.getMessage(),ex );

			}
		} else {
			log.warn("\t\t... there already exists a Hazelcast instance with name {}, so this one will be returned",
					  instanceName);
		}
		_debugHazelcastInstances();
		return outHZInstance;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  PRIVATE METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void _debugHazelcastInstances() {
		Set<HazelcastInstance>  hzInstances  = Hazelcast.getAllHazelcastInstances();
		log.debug("Hazelcast instances: {}",hzInstances.size());
		for (HazelcastInstance hzInst : hzInstances ) {
			log.debug("Instance name={} cluster size={}",
					  hzInst.getName(),hzInst.getCluster().getMembers().size());
		}
	}
}
