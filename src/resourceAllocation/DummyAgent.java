package resourceAllocation;

import jade.core.Agent;

public class DummyAgent extends Agent {

	private static final long serialVersionUID = -2959026186780910618L;

	@Override
    public void setup(){
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            long time = (long) Long.parseLong((String)args[0]);

            System.out.println("����� " + getAID().getName() + " ����� � ���������� ��������, ������� �������� " + time + " �����������.");

            MySimpleBehaviour behaviour = new MySimpleBehaviour(this, time);
            addBehaviour(behaviour);
        }
    }
}
