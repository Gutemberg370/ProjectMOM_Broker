package application;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por enviar aos sensores a lista de parâmetros não monitorados
public class BrokerTopicController {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    
    private static String topicOfInterest = "SendAvailableParameters";
    
    public String[] parameters = {"temperatura","umidade","velocidade"};
    
    public Main main;
    
    public BrokerTopicController(Main main) {
    	this.main = main;
    }

    // Enviar os parâmetros não monitorados
    public void sendAvaliableParameters() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(topicOfInterest);

        MessageProducer producer = session.createProducer(topic);

        ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(parameters);

        producer.send(objectMessage);
        
        //this.main.availableParameters = new ArrayList<String>(Arrays.asList(this.parameters));
        //System.out.println("Sent message from BrokerTopicController");

        connection.close();
    }
}