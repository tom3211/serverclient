package com.baton.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;

public final class ServerClientConfig {
	private static Configuration config = null;

	private ServerClientConfig() {
		loadParameters() ;
	}
	
	public static Configuration getConfiguration() {
		if(config == null)
			loadParameters() ;
		return config ;
	}
	private static void loadParameters() {
		synchronized(ServerClientConfig.class) {
			if(config == null)
				loadConfiguration() ;
		}
	}

	private static void loadConfiguration() {
		String configFilePath = "config/server.config" ;
		try {
			config = new PropertiesConfiguration(configFilePath) ;
		}
		catch (ConfigurationException e) {
			e.printStackTrace(); 
			System.out.println("Unable to load configuration file " + configFilePath) ;
			throw new RuntimeException() ;
		} catch (Exception e) {
			e.printStackTrace(); 
			System.out.println("IOException " + configFilePath) ;
			throw new RuntimeException() ;
		}
	}
}
