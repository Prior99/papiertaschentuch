package de.cronosx.papiertaschentuch;

import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.PropertyPermission;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LoggingPermission;
import javax.script.*;

public class Scripts {
	private final ScriptEngineManager scriptEngineManager;
	private final ScriptEngine scriptEngine;
	private final ScriptSecurityManager securityManager;
	private final Bindings bindings;
		
	public Scripts(){
		scriptEngineManager = new ScriptEngineManager();
		scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
		bindings = scriptEngine.createBindings();
		securityManager = new ScriptSecurityManager();
		System.setSecurityManager(securityManager);
	}
	
	public Scripts addBinding(String key, Object obj) {
		bindings.put(key, obj);
		return this;
	}
	
	public Scripts finalizeBindings() {
		scriptEngine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
		return this;
	}
	
	public Scripts loadDirectory(String folder) {
		loadDirectory(new File(folder));
		return this;
	}
	
	private void loadDirectory(File dir) {
		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles((File pathname) -> {
				return pathname.isDirectory() || pathname.getName().toLowerCase().endsWith(".js");
			});
			for(File file : files) {
				if(file.isDirectory()) {
					loadDirectory(file);
				}
				else {
					loadScript(file);
				}
			}
		}
		else {
			Log.error("Scripts directory \"" + dir + "\" either does not exists or is not a directory.");
		}
	}
	
	private void loadScript(File file) {
		try {
			FileReader fr = new FileReader(file);
			securityManager.enableSandbox();
			scriptEngine.eval(fr);
		}
		catch (ScriptException ex) {
			Log.error("Error in script: " + ex.getMessage());
		}		
		catch(SecurityException ex) {
			Log.error("Script caused SecurityException: " + ex.getMessage());
		} 
		catch (FileNotFoundException ex) {
			Log.fatal("An internal error occured. A file was not found that was previously listed." + ex.getMessage());
		}
		finally {
			securityManager.disableSandbox();
		}
	}
	
	public Scripts runScript(String script) {
		try {
			securityManager.enableSandbox();
			scriptEngine.eval(script);
		}
		catch (ScriptException ex) {
			Log.error("Error in script: " + ex.getMessage());
		}		
		catch(SecurityException ex) {
			Log.error("Script caused SecurityException: " + ex.getMessage());
		}
		finally {
			securityManager.disableSandbox();
		}
		return this;
	}
	
	private static class ScriptSecurityManager extends SecurityManager {
		private boolean sandbox;
		
		public ScriptSecurityManager() {
			sandbox = false;
		}
		
		public void enableSandbox() {
			sandbox = true;
		}
		
		public void disableSandbox() {
			sandbox = false;
		}
		
		@Override
		public void checkPermission(Permission perm) {
			if(false) {
				if(perm instanceof FilePermission) {
					if(perm.getName().endsWith("nashorn.jar")) return;
					if(perm.getName().endsWith("rt.jar")) return;
				}
				else if(perm instanceof RuntimePermission) {
					if(perm.getName().equals("createClassLoader")) return;
					if(perm.getName().startsWith("accessClassInPackage.jdk")) return;
					if(perm.getName().startsWith("accessClassInPackage.sun")) return;
					if(perm.getName().equals("accessDeclaredMembers")) return;
					if(perm.getName().equals("getClassLoader")) return;
					if(perm.getName().equals("loadLibrary")) return;
				}
				else if(perm instanceof LoggingPermission) return;
				else if(perm instanceof PropertyPermission) {
					if(perm.getActions().equals("read")) return; 
				}
				else if(perm instanceof ReflectPermission) {
					if(perm.getName().equals("suppressAccessChecks")) return;
				}
				throw new SecurityException("Tried to violate security: " + perm.getName() + ", " + perm.getActions() + ", " + perm.getClass());
			}
		}
	}
}
