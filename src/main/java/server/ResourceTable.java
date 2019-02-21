package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import server.resource.Resource;

public class ResourceTable {
	
	private Map<ResourceKey, Resource> resource_map;
	private static final int RESOURCE_MAX_LIMITED = 100;
	
	public ResourceTable() {
		resource_map = Collections.synchronizedMap(new HashMap<ResourceKey, Resource>());
	}
	
	public synchronized boolean addResource(ResourceKey k, Resource v) {
		
		if (resource_map.size() < RESOURCE_MAX_LIMITED) {
			resource_map.put(k, v);
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean removeResource(ResourceKey k) {
		
		if (resource_map.containsKey(k)) {
			resource_map.remove(k);
			return true;
		}
		
		return false;
	}
	
	public synchronized Resource getResource(ResourceKey k) {

		if (resource_map.containsKey(k)) {
			return resource_map.get(k);
		}

		return null;
	}
}
