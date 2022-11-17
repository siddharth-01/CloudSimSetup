package org.cloudbus.cloudsim.examples;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;

/**
 * A simple example showing how to create a data center with one host and run
 * one cloudlet on it.
 */
public class CloudSimExample1 {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
	/** The vmlist. */
	private static List<Vm> vmlist;
	
	/** Max value of Ram **/
	public final static Integer Max_RAM = 500;
	
	/** Max value of power consumption **/
	public final static Integer P_MAX = 6000;

	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample1...");

		try {
			// First step: Initialize the CloudSim package. It should be called before
			// creating any entities.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current
														// date and time.
			boolean trace_flag = false; // trace events

			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create one virtual machine
			vmlist = new ArrayList<Vm>();

			// VM description
			int vmid = 1;
			int mips = 1000;
			long size = 10000; // image size (MB)
			int ram = 512; // vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; // number of cpus
			String vmm = "Xen"; // VMM name

			// create VM
			Vm vm1 = new Vm(1, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			// Fifth step: Create one Cloudlet
			cloudletList = new ArrayList<Cloudlet>();

			// Cloudlet properties
			int id = 0;
			long length = 400000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel,
					utilizationModel, utilizationModel);
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(vmid);

			// add the cloudlet to the list
			cloudletList.add(cloudlet);

			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {

		List<Vm> virtualMachineList  = new ArrayList<>();
		// creation of VM
		int vmid = 1;
		int mips = 1000;
		long size = 10000; // image size (MB)
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		Vm vm1 = new Vm(1, 10, mips, pesNumber, 512, 10000, size, vmm, new CloudletSchedulerTimeShared());
		
		Vm vm2 = new Vm(2, 10, 900, 2, 600, 10000, 10500, vmm, new CloudletSchedulerTimeShared());

		Vm vm3 = new Vm(3, 10, 850, 3, 650, 10000, 10650, vmm, new CloudletSchedulerTimeShared());
		
		Vm vm4 = new Vm(4, 10, 800, 4, 700, 10000, 10700, vmm, new CloudletSchedulerTimeShared());
		
		// add the VM to the vmList
		virtualMachineList.add(vm1);
		virtualMachineList.add(vm2);
		virtualMachineList.add(vm3);
		virtualMachineList.add(vm4);
		
		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		// host creation of type HP ProLiant ML110 G4 - Xeon 3040 0
		List<Pe> hpProLiantML110G4peList = new ArrayList<Pe>();

		int mipsForG4 = 1000;

		// 3. Create PEs and add these into a list.
		hpProLiantML110G4peList.add(new Pe(1, new PeProvisionerSimple(mipsForG4))); // need to store Pe id and MIPS
																					// Rating

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
		int hostId = 1;
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

	   
		// HP ProLiant ML110 G4 - Xeon 3075 0 host creation. 
		Host hpProLiantML110G4 = new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage,
				hpProLiantML110G4peList, new VmSchedulerTimeShared(hpProLiantML110G4peList));
		hpProLiantML110G4.setHostType("HpProLiantML110G4");
		hpProLiantML110G4.setPowerConsumption(1000);
		hpProLiantML110G4.createVMList(virtualMachineList);
		hostList.add(hpProLiantML110G4); // This is our machine

		// HP ProLiant ML110 G5 - Xeon 3075 0 host creation.
		List<Pe> hpProLiantML110G5peList = new ArrayList<Pe>();
		int mipsforG5 = 750;
		hpProLiantML110G5peList.add(new Pe(2, new PeProvisionerSimple(mipsforG5))); // need to store Pe id and MIPS
																					// Rating
		Host hpProLiantML110G5 = new Host(2, new RamProvisionerSimple(1048), new BwProvisionerSimple(10500), 100000,
				hpProLiantML110G5peList, new VmSchedulerTimeShared(hpProLiantML110G5peList));
		hpProLiantML110G5.setHostType("HpProLiantML110G5");
		hpProLiantML110G5.setPowerConsumption(900);
		hpProLiantML110G5.createVMList(virtualMachineList);
		hostList.add(hpProLiantML110G5);
		
		// HP ProLiant DL360 G5 - Xeon 3075 0 host creation.
		Host hPProLiantDL360G7 = new Host(3, new RamProvisionerSimple(1048), new BwProvisionerSimple(10500), 100000,
				hpProLiantML110G5peList, new VmSchedulerTimeShared(hpProLiantML110G5peList));
		hPProLiantDL360G7.setHostType("HPProLiantDL360G7");
		hPProLiantDL360G7.setPowerConsumption(2000);
		hPProLiantDL360G7.createVMList(virtualMachineList);
		hostList.add(hPProLiantDL360G7);
		
		// HP ProLiant DL360 G9 - Xeon 3075 0 host creation.
		Host hPProLiantDL360G9 = new Host(3, new RamProvisionerSimple(1048), new BwProvisionerSimple(10500), 100000,
				hpProLiantML110G5peList, new VmSchedulerTimeShared(hpProLiantML110G5peList));
		hPProLiantDL360G9.setHostType("HPProLiantDL360G9");
		hPProLiantDL360G9.setPowerConsumption(2500);
		hPProLiantDL360G9.createVMList(virtualMachineList);
		hostList.add(hPProLiantDL360G9);

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
		// devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
				cost, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
			energyEfficientStrategy(datacenter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}

	/**
	 * Creation of excluded host list
	 * 
	 * @return excludedHostList
	 */
	public static List<Host> createxludedHostList() {

		List<Host> excludedHostList = new ArrayList<>();
		List<Pe> peList = new ArrayList<Pe>();
		peList.add(new Pe(1, new PeProvisionerSimple(500)));

		// host1 creation
		Host host1 = new Host(3, new RamProvisionerSimple(1048), new BwProvisionerSimple(10500), 100000, peList,
				new VmSchedulerTimeShared(peList));
		host1.setPowerConsumption(0);


		excludedHostList.add(host1);

		return excludedHostList;
	}
	
	/** Energy efficient strategy
	 * @param datacenter
	 */
	public static void energyEfficientStrategy(Datacenter datacenter) {
		
		List<Host> totalHostList = datacenter.getVmAllocationPolicy().getHostList();
		
		//step1 : UnderLoad hosts detection
		List<Host> underLoadedHosts = underLoadDetection(totalHostList);
	
	   //step2: OverLoad hosts detection
		List<Host> overLoadedHosts =  overloadedHostDetection(totalHostList);
		
	   //step3: MMTD VM selection
		List<Vm> vmListToMigrate = migrationTimeOverDeviationVMSelection(overLoadedHosts);
	
		//step4: Virtual machine placement
		virtualMachinePlacements(totalHostList,underLoadedHosts,overLoadedHosts, vmListToMigrate);
	}
	

	/** step1: Logic to detect underloaded hosts
	 * @param hostList
	 * @return
	 */
	public static List<Host> underLoadDetection(List<Host> hostList) {

			List<Host> underloadedhostlist=new ArrayList<Host>();

			for(Host host : hostList){

			Integer TL = host.getLowerThreshold();
			Integer utilization = host.getUtilizationOfCPU();
			
			if(utilization <= TL){	
			    host.setUnderLoaded(true);
				underloadedhostlist.add(host);
			}
		}
			System.out.print("Number of underLoad host = " + underloadedhostlist.size());
			return underloadedhostlist;
	}
	
	/** step2: Logic to detect underloaded hosts 
	 * @param hostList
	 * @return
	 */
	public static List<Host> overloadedHostDetection(List<Host> hostList){

		List<Host> overloadedhostlist = new  ArrayList<Host>();
		List<Host> excludedHostList = createxludedHostList();

		for(Host host : hostList){

		if(excludedHostList.contains(host)){
		 continue;
		}
		Integer utilization=host.getUtilizationOfCPU();
		Integer TU = host.getLevelOfObservedMaxPPR();

		System.out.println("util = " + utilization + " " + " tu= " + TU);
		if(utilization >= TU){
		host.setOverLoaded(true);
		overloadedhostlist.add(host);
		}
		}
        System.out.println("overloadedhostlist  = " + overloadedhostlist.size());
		return overloadedhostlist;

		}
	

	/** step3: logic for MMTD (Virtual machine selection)
	 * @param overLoadHosts
	 * @return
	 */
	public static List<Vm> migrationTimeOverDeviationVMSelection(List<Host> overLoadHosts) {
		
		List<Vm> vmListToMigrate = new ArrayList<>();
		
		for(Host overLoadedHost : overLoadHosts) {	
			int minRam = Max_RAM;
			Vm selectedVm = null;
			double overAllVmUtilization = 0.0;
			
			Integer hostUtilization = overLoadedHost.getUtilizationOfCPU();
			Integer deviation = (hostUtilization - overLoadedHost.getLevelOfObservedMaxPPR());
			List<Vm> vmList = overLoadedHost.getVirtualMachineList();
			
			for(Vm vm : vmList) {
				// checking the vm utilization for 5 hours
				double vmUtilization = vm.getTotalCPUtilization(3600);
				
				System.out.println("vmUtilization = " + vmUtilization);
				
				if(vmUtilization >= deviation) {
					if(vm.getRam() < minRam) {
						minRam = vm.getRam();
						selectedVm = vm;
					}
				}				
			}
			
			if(selectedVm != null) {
				vmListToMigrate.add(selectedVm);
			}else {
				
				for(Vm vm : vmList) {
					overAllVmUtilization = overAllVmUtilization + vm.getTotalUtilizationOfCpu(18000);
					vmListToMigrate.add(vm);
					
					if(overAllVmUtilization >= deviation) break;
				}
			}
			
		}
		System.out.println("vmListToMigrate " + vmListToMigrate.size());
		return vmListToMigrate;
	}
	
	
	
	/** step4: logic for virtual machine placement.
	 * @param totalHostList
	 * @param underLoadedHosts
	 * @param overLoadedHosts
	 * @param vmListToMigrate
	 */
	public static void virtualMachinePlacements(List<Host> totalHostList, List<Host> underLoadedHosts, List<Host> overLoadedHosts, List<Vm> vmListToMigrate) {
		
		List<Host> excludeHostListForFindingUnderUtilized = new  ArrayList<>();
		excludeHostListForFindingUnderUtilized.addAll(overLoadedHosts);
		excludeHostListForFindingUnderUtilized.addAll(createxludedHostList());
		
		List<Host> excludeHostListForFindingNewPlacement = new ArrayList<>();
		excludeHostListForFindingNewPlacement.addAll(totalHostList);
		excludeHostListForFindingNewPlacement.addAll(createxludedHostList());
		
		List<Host> allocation = new ArrayList<>();
		int index = 0;
		
		while(index < underLoadedHosts.size()) {
			
			if(totalHostList.size() == excludeHostListForFindingUnderUtilized.size()) 
				break;
			
			Host host  = underLoadedHosts.get(index);
			
			
			if(!excludeHostListForFindingNewPlacement.contains(host)) {
				excludeHostListForFindingNewPlacement.add(host);
			}
			
			for(Vm vm : host.getVirtualMachineList()) {
				
				
				int minPower = P_MAX;
				Host allocatedHost = null;
				
				for(Host h : totalHostList) {
					
					Integer unitilization = h.afterUtilization();
					
					if(unitilization > h.getLevelOfObservedMaxPPR()) 
						continue;
					
					Integer power = h.estimatePowerAfterAllocation();
					
					if(power < minPower) {
						allocatedHost = h;
						minPower = power;
					}
				}
				
				if(allocatedHost!=null) {
					if(!excludeHostListForFindingUnderUtilized.contains(allocatedHost)) {
						excludeHostListForFindingUnderUtilized.add(allocatedHost);
					}
					else {
						break;
					}
					allocation.add(allocatedHost);					
				}
			}
			
			index++;
		}
		System.out.println("Average energy consumption after allocation = " + allocation.get(0).averageEnergyConsumption());
		System.out.println("Allocation list " + allocation.size() + "type = " + allocation.get(0).getHostType());
	}

}