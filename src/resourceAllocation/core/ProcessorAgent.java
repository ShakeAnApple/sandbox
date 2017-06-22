package resourceAllocation.core;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessorAgent extends Agent {

	private static final long serialVersionUID = -8419853254184621035L;
	private String _serviceType = "processor";
	
	public List<Worker> workers;

	public void showStatistics() {
		float processingTimeTotal = 0;
		float waitingTimeTotal = 0;
		float fullTimeTotal = 0;

		for (Worker worker : workers) {
			processingTimeTotal += worker.processingTime;
			waitingTimeTotal += worker.waitingTime;
			fullTimeTotal += worker.processingTime + worker.waitingTime;

			System.out.println(worker._agent.getName() + " started at " + worker.waitingTime + " and lasted " + worker.processingTime);
		}

		float processingTimeAverage = processingTimeTotal / workers.size();
		float waitingTimeAverage = waitingTimeTotal / workers.size();
		float fullTimeAverage = fullTimeTotal / workers.size();

		System.out.println(String.format("Processing time - average: %4.1f total: %4.1f", processingTimeAverage, processingTimeTotal));
		System.out.println(String.format("Waiting time    - average: %4.1f total: %4.1f", waitingTimeAverage, waitingTimeTotal));
		System.out.println(String.format("Full time       - average: %4.1f total: %4.1f", fullTimeAverage, fullTimeTotal));
	}

	@Override
	public void setup() {
		ServiceDescription sd = new ServiceDescription();
		sd.setType(_serviceType);
		sd.setName("ProcessingService");

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		addBehaviour(new AgentFinderBehaviour(this, 1000));
	}

	public abstract void serve(List<Worker> agents);

	private class AgentFinderBehaviour extends WakerBehaviour {

		private static final long serialVersionUID = -5469211056131974701L;
		private ProcessorAgent _processorAgent;

		public AgentFinderBehaviour(ProcessorAgent agent, long tick) {
			super(agent, tick);
			_processorAgent = agent;
		}

		@Override
		public void onWake() {
			workers = new ArrayList<Worker>();

			boolean replyStackIsNotEmpty = true;

			while (replyStackIsNotEmpty) {
				ACLMessage aclMessage = myAgent.receive();
				if (aclMessage != null) {
					try {
						long time = (long) aclMessage.getContentObject();

						Worker worker = new Worker(aclMessage.getSender(), time, aclMessage.getReplyWith());
						workers.add(worker);

					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				} else {
					replyStackIsNotEmpty = false;
				}
			}

			if (workers.isEmpty()) {
				this.reset();
			}

			_processorAgent.serve(workers);
		}
	}
}
