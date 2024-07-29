package application;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por receber as solicitações de inscrição dos clientes
public class SubscriptionRequestReceiver {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
	private static String queueName = "SubscriptionLine";
	
	public Main main;
	
	public SubscriptionRequestReceiver(Main main) {
		this.main = main;
	}

	// Receber as solicitações de inscrição dos clientes
	public void receiveSubscriptionRequest() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue(queueName);

        MessageConsumer consumer = session.createConsumer(destination);
        
        Message message = consumer.receive();
        
		if (message instanceof ObjectMessage) {
			Object object;
			Boolean isSubscribed = false;
			try {
				object = ((ObjectMessage) message).getObject();
				String[] request = (String[]) object;
				// Cliente já existe na base de dados? Apenas adicionar o novo parâmetro ao registro
				// já existente, caso o parâmetro já não esteja lá
				for(Client client: this.main.registeredClients) {
					if(client.getId().equals(request[0])) {
						if(!client.getSubscribedParameters().contains(request[1])) {
							client.addSubscribedParameter(request[1]);
						}
						isSubscribed = true;
						break;
					}
				}
				
				// Cliente não existe na base de dados? Adicione-o com o novo parâmetro inscrito
				if(isSubscribed == false) {
					ArrayList<String> subscribedParameters = new ArrayList<String>();
					subscribedParameters.add(request[1]);
					this.main.registeredClients.add(new Client(request[0],subscribedParameters));
				}
				
				this.main.callSubscriptionRequestReceiver();
				
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

        consumer.close();
    }


}
