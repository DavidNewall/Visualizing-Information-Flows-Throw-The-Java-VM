package instrumentation;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jdi.Bootstrap;
import org.eclipse.jdi.internal.connect.ConnectorImpl;
import org.eclipse.jdi.internal.connect.SocketLaunchingConnectorImpl;
//import org.eclipse.jdi.internal.VirtualMachineImpl;

import treemap.Node;
import treemap.Treemap;

import com.javamex.classmexer.MemoryUtil;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;

public class Instrumentor {
	
	// instance variables
	//private Connector connector = null;
	
	private ConnectorImpl connector = null;
	public static void main(String args[]){
		Instrumentor instru = new Instrumentor();
	}

	
	/*private void launch(){
		String[] args = new String[2];
		
		try {
			start("test.code.RandomNumberGenerator", args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("It was successful in launching the other program");
	}*/
	
	public Instrumentor(){
		
		System.out.println("It makes it to the instrumentor method");
		
		connectToVM();
		Treemap map = new Treemap();
		
		Node root = map.getRoot();
		
		long noBytes = MemoryUtil.memoryUsageOf(root);
		
		System.out.println("The number of bytes to store the root is: " + noBytes);
		
	}
	
	private int getObjectTime(){
		return 0;
	}
	
	private void connectToVM(){
		
		// this piece of code looks for the correct connector
		
		List connectors = Bootstrap.virtualMachineManager().allConnectors();
		//List connectors = Bootstrap.virtualMachineManager().attachingConnectors();
		Iterator iter = connectors.iterator();
		
		/*while (iter.hasNext()){
			
			ConnectorImpl theConnector = (ConnectorImpl) iter.next();
			
			System.out.println(theConnector.getClass());
			System.out.println(theConnector.name());
			
			
			System.out.println("Here are the connectors: ");
			System.out.println(theConnector);
			System.out.println();
		}*/
		
		while (iter.hasNext()){
			
			String nameCheck = iter.next().toString();
			
			if (nameCheck.contains("SocketLanuchingConnectorImpl")){
				
			}
			
			Object check = iter.next();
			
			System.out.println("This is what the iterator contains:");
			System.out.println(check.toString());
			System.out.println();
			
			ConnectorImpl thisConnector = (ConnectorImpl) check;
			//Connector thisConnector = (Connector) check;
			
			System.out.println(thisConnector.getClass());
			System.out.println("The name of the current connector is : " + thisConnector.name());
			
			System.out.println("\n The Arguments are: ");
			Map arguments = thisConnector.defaultArguments();
			for (Iterator itr = arguments.keySet().iterator(); itr.hasNext();){
				ConnectorImpl.ArgumentImpl argument = (ConnectorImpl.ArgumentImpl) arguments.get(itr.next());
				//Connector.Argument argument = (Connector.Argument) arguments.get(itr.next());
				System.out.println(argument.description());
			}
			System.out.println();
			
			if (thisConnector.name().equals("com.sun.jdi.CommandLineLaunch"))
				System.out.println("Found It!");
				connector = thisConnector;
				break;
		}
		
		
		System.out.println("\n The Arguments are: ");
		Map arguments = connector.defaultArguments();
		for (Iterator itr = arguments.keySet().iterator(); itr.hasNext();){
			ConnectorImpl.ArgumentImpl argument = (ConnectorImpl.ArgumentImpl) arguments.get(itr.next());
			System.out.println(argument.name());
			System.out.println(argument.description());
		}
		
		// this is code for using the attaching connector
		
		/*Connector.Argument host = (Connector.Argument) arguments.get("hostname");
		Connector.Argument port = (Connector.Argument) arguments.get("port");
		host.setValue("127.0.0.1");
		port.setValue("8080");
		
		AttachingConnector attacher = (AttachingConnector) connector;*/
		
		ConnectorImpl.ArgumentImpl opts =(ConnectorImpl.ArgumentImpl) arguments.get("options");
		ConnectorImpl.ArgumentImpl main =(ConnectorImpl.ArgumentImpl) arguments.get("main");
		ConnectorImpl.ArgumentImpl susp =(ConnectorImpl.ArgumentImpl) arguments.get("suspend");
		String classpath = System.getProperty("java.class.path");
		
		String currentDir = System.getProperty("user.dir");
		
		currentDir = currentDir + "\\src\\";
		
		//System.out.println(currentDir);
		
		/*System.out.println(classpath + "\n");
		
		while (classpath.contains("\\")){
			classpath = classpath.replace("\\", "/");
		}
		
		System.out.println(classpath);*/
		
		classpath = classpath + ";" + currentDir;
		
		//System.out.println("The file descriptor is: " + System.getProperty("file.separator"));
		System.out.println("The classpath is: " + classpath + " \n\n\n\n");
		

		opts.setValue("-classpath \"" + classpath + "\"");
		//main.setValue("C:/Users/David/Desktop/RandomNumberGenerator args1 args2");
		main.setValue("RandomNumberGenerator args1 args2");
		susp.setValue("false");

		SocketLaunchingConnectorImpl launcher = (SocketLaunchingConnectorImpl) connector;

		Process process = null;
		
		VirtualMachine vm = null;
		try {
			vm = launcher.launch(arguments);
			process = vm.process();
			displayRemoteOutput(process.getErrorStream());
			displayRemoteOutput(process.getInputStream());
		
		} catch (Exception e){
			System.out.println("There is an issue with trying to attach to the vm");
			e.printStackTrace();
			return;
		}
		
		
		//System.out.println("\n\nIt is about to process the input stream.\n\n");
		//processInputStream(process.getInputStream());
		//System.out.println("No processing is happening yo");
		
		//System.out.println();
		//launch();
		//System.out.println();
		
		System.out.println ("So far so good!!!");
		
		
		List classes = vm.allClasses();
		
		System.out.println("\n The classes in running in the vm are: ");
		
		for (Object c : classes){
			System.out.println(c.toString());
		}
		
		
		EventRequestManager eventManager = vm.eventRequestManager();
		MethodEntryRequest methodEntry = eventManager.createMethodEntryRequest();
		methodEntry.addClassFilter("instrumentation.*");
		methodEntry.enable();
		
		/*<MethodEntryRequest> mEntry = eventManager.methodEntryRequests();
		
		System.out.println("\n\nHere are the method entry requests");
		
		for (MethodEntryRequest  m : mEntry){
			System.out.println(m.toString());
		}*/
		
		EventQueue eventQ = vm.eventQueue(); 


		EventSet eventSet = null;
		
		
		
		
		try { 
			eventSet = eventQ.remove(); 
			System.out.println("\n\nIT MAKES IT HERE");
			
		}
		catch (Exception e) { 
			e.printStackTrace();
		}

		
		while (!eventSet.isEmpty()){
		
			System.out.println(eventSet);
			System.out.println("\n\nThe events are: ");

			EventIterator eventIterator = eventSet.eventIterator();
			while (eventIterator.hasNext())
			{
				Event event = eventIterator.nextEvent();

				System.out.println(event.toString());

				if (event instanceof MethodEntryEvent) { // process this event }

					String methodString = ((MethodEntryEvent) event).method().toString();
					ThreadReference thread =((StackFrame) event).thread();
					List stackList = null;
					try {
						stackList = thread.frames();
					} catch (IncompatibleThreadStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int level = 0;

					for (Iterator it = stackList.iterator(); it.hasNext();){
						StackFrame stackFrame =(StackFrame) it.next();
						ObjectReference thisObject=stackFrame.thisObject();
						if (level == 0) {
							String calleeClass = thisObject.referenceType().toString();
							System.out.println("The callee class is: " + calleeClass);
						}
						else if (level == 1){
							String callerClass = thisObject.referenceType().toString(); 
							System.out.println("The calling class is: " + callerClass);
						}
					}

					vm.resume();
				}
			}

			try { 
				System.out.println("It is using remove again");
				eventSet = eventQ.remove(1000); 
				System.out.println("\n\nIT MAKES IT HERE");

			}
			catch (Exception e) { 
				//e.printStackTrace();
			}

		}
			
		
		/*Connector.Argument opts=(Connector.Argument) arguments.get("options");
		Connector.Argument main=(Connector.Argument) arguments.get("main");
		Connector.Argument susp=(Connector.Argument) arguments.get("suspend");


		main.setValue("MyApplication arg1 arg2");
		susp.setValue("false");


		LaunchingConnector launcher =(LaunchingConnector) connector;

		Process process = null;
		
		try
		{
		  vm = launcher.launch(arguments);
		  process = vm.process();
		  //displayRemoteOutput(process.getErrorStream());
		  //displayRemoteOutput(process.getInputStream());
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/

		
	}
	
	private void displayRemoteOutput(InputStream in){
		LogStreamReader lsr = new LogStreamReader(in);
		Thread thread = new Thread(lsr, "LogStreamReader");
		thread.start();
	}
	
	private void processInputStream(InputStream i){
		Scanner scan = new Scanner(i);
		
		//BufferedReader reader = new BufferedReader(new InputStreamReader(i));
		
		while (scan.hasNext()){
			String input = scan.nextLine();
			System.out.println(input);
		}
		
	}
	
	// code from here http://stackoverflow.com/questions/6055725/programmatically-running-a-java-program
	
	// Use like start("com.example.myApp", "param1", "param2");
	
	public static void start(final String classname, final String args[]) throws Exception {
		final Class<?> clazz = Class.forName(classname);
		final Method main = clazz.getMethod("main", String[].class);
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				try{
					main.invoke(null, new Object[]{args});
				} catch (Exception e){
					throw new AssertionError(e);
				}
			}
		}).start();
	}
}




// code from http://www2.sys-con.com/itsg/virtualcd/java/archives/0603/loton/index.html#s6
// this was the code that i used to set up the connection

// this is different code

/*private void alternateConnection(){
ILaunchConfigurationWorkingCopy remoteDebugLaunchConfig = createRemoteDebugLaunchConfiguration("ProjectNameThatHasTheSourceCode", "8000");
DebugUITools.launch(remoteDebugLaunchConfig, ILaunchManager.DEBUG_MODE);
}

private ILaunchConfigurationWorkingCopy createRemoteDebugLaunchConfiguration(final String projectName, final String port) throws CoreException {
ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION);

final ILaunchConfigurationWorkingCopy remoteDebugConfig = type.newInstance(null, "remote debug");

// Set project
remoteDebugConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);

// Set JVM debugger connection parameters
Map<String, String> connectionParameters = new HashMap<String, String>();
connectionParameters.put("hostname", "localhost");
connectionParameters.put("port", port);
remoteDebugConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, connectionParameters);
remoteDebugConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
        "org.eclipse.jdt.launching.socketAttachConnector");
return remoteDebugConfig;
}*/