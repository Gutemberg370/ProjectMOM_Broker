package application;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe que envia aos clientes a lista de parâmetros monitorados por algum sensor
public class MonitoredParametersSender {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
    public String[] monitoredParameters = new String[3];
    
    private static String topicName = "MonitoredParameters";
    
    public Main main;
    
    public MonitoredParametersSender(Main main) {
    	this.main = main;
    }

    // Enviar a lista de parâmetros monitorados
	public void sendMonitoredParameters() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(topicName);

        MessageProducer producer = session.createProducer(topic);

        monitoredParameters = this.main.usedParameters.toArray(new String[0]);
        
        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(monitoredParameters);

        producer.send(objectMessage);
        
        //System.out.println("Sent message from MonitoredParametersSender");

        connection.close();
    }

}
