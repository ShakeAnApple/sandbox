package knowledge.consumer;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import knowledge.Knowledge;
import knowledge.KnowledgeAgent;

public class KnowledgeConsumerAgent extends KnowledgeAgent {
	
	private static final long serialVersionUID = 1069538812627168203L;
	
	public List<String> questions = new ArrayList<String>();

	@Override
	protected void initializeBehaviours() {
		addBehaviour(new FindFactsBehaviour(this, 5000));
	}

	@Override
	protected void initializeData() {
		Object[] args = getArguments();
		for (Object arg : args) {
			questions.add(arg.toString());
			trace("got question (" + arg.toString() + ")");
		}
	}

	@Override
	protected ServiceDescription[] getAgentServiceDescriptions() {
		ServiceDescription agentServices[] = new ServiceDescription[1];
		agentServices[0] = new ServiceDescription();
		agentServices[0].setName(Knowledge.KNOWLEDGE_SERVICES);
		agentServices[0].setType(Knowledge.KNOWLEDGE_SERVICE_CONSUMING);
		return agentServices;
	}
	
	synchronized public void findFact(AID[] knowledgeProcessors) {
		String fact = questions.get(0);
		for(AID knowledgeProcessor: knowledgeProcessors) {
			addBehaviour(new FindFactBehaviour(knowledgeProcessor, fact));
		}
		questions.remove(0);
	}
}