package application;

import java.util.ArrayList;
import java.util.Arrays;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por receber os tópicos não utilizados por cada sensor, de forma a atualizar os demais
// com a lista atualizada
public class TopicUpdater {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
	private static String queueName = "BrokerLine";
	
	public ArrayList<String> parametersNotUsed = new ArrayList<String>();
	
	public Main main;
	
	public TopicUpdater(Main main) {
		this.main = main;
	}

	// Receber os parâmetros não utilizados pelo sensor
	public void receiveParametersnotUsed() throws JMSException {

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
			try {
				object = ((ObjectMessage) message).getObject();
				String[] request = (String[]) object;
				ArrayList<String> notUsedParameters = new ArrayList<String>(Arrays.asList(request));
				parametersNotUsed = notUsedParameters;
				// Atualizar o Broker com os parâmetros não monitorados
				this.main.updateAvailableParameters(notUsedParameters);
				// Enviar aos sensores a lista atualizada dos parãmetros disponíveis
				this.main.callBrokerTopicController();
				// Realizar uma nova escuta por outras atualizações de parâmetros pelos sensores
				this.main.callParametersUpdateConsumer();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

        consumer.close();
    }

}
