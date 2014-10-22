package de.cronosx.papiertaschentuch;

import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import javax.script.*;

public class Scripts {
	private final ScriptEngineManager scriptEngineManager;
	private final ScriptEngine scriptEngine;
	private final ScriptSecurityManager securityManager;
	public Scripts() {
		scriptEngineManager = new ScriptEngineManager();
		scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
		securityManager = new ScriptSecurityManager();
		System.setSecurityManager(securityManager);
		
	}
	
	public void runScript(String script) throws ScriptException {
		securityManager.enableSandbox();
		scriptEngine.eval(script);
		securityManager.disableSandbox();
	}
	
	public static void main(String[] args) throws ScriptException {
		Scripts scripts = new Scripts();
		scripts.runScript("new java.lang.File('test.txt')");
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
			if(sandbox) {
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
				}
				else if(perm instanceof ReflectPermission) {
					if(perm.getName().equals("suppressAccessChecks")) return;
				}
				throw new SecurityException("Script tried to violate security: " + perm.getName() + ", " + perm.getActions() + ", " + perm.getClass());
			}
		}
	}
}
