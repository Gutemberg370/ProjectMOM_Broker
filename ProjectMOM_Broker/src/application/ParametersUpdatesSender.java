package application;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

// Classe responsável por enviar as mensagens de alerta produzidas pelos sensores
public class ParametersUpdatesSender {
	
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	private String messageToSend;
	
	public Main main;
	
	public ParametersUpdatesSender(Main main) {
		this.main = main;
	}

	// Enviar a mensagem produzida pelo "sensor" ao "client" inscrito no parâmetro monitorado pelo sensor
	public void sendParameterSubscribed(Client client, Sensor sensor) throws JMSException {
        
		// Só envia a mensagem se for uma mensagem de alerta
        if(sensor.getParameterStatus().contains("Alerta")) {
        	
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(client.getId());
            
            // Cria a mensagem no caso de o valor medido for maior que o valor máximo
            if(sensor.getParameterStatus().contains("maior")){
            	messageToSend = String.format("Atenção! O sensor '%s', responsável pelo parâmetro '%s', obteve o valor %s em sua leitura, que é maior que o máximo de %s !", 
            								  sensor.getId(), sensor.getParameterMeasured(), sensor.getMeasuredValue(), sensor.getMaxValue());
            }
            
            // Cria a mensagem no caso de o valor medido for menor que o valor mínimo
            if(sensor.getParameterStatus().contains("menor")){
            	messageToSend = String.format("Atenção! O sensor '%s', responsável pelo parâmetro '%s', obteve o valor %s em sua leitura, que é menor que o mínimo de %s !", 
            								  sensor.getId(), sensor.getParameterMeasured(), sensor.getMeasuredValue(), sensor.getMinValue());
            }
           

            MessageProducer producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage(messageToSend);
            
            producer.send(message);
          
            //System.out.println("Sent message from PARAMETERSUPDATESSENDER");

            connection.close();
        	
        }
        
    }

}
