package application;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javafx.application.Platform;
import javafx.scene.paint.Color;

// Classe responsável por receber os valores determinados por um sensor para um determinado parâmetro
public class MeasuredValuesReceiver implements MessageListener {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static String topicName = "MeasuredValue";
    
    public Main main;
    
    public MeasuredValuesReceiver(Main main) {
    	this.main = main;
    }

    // Atualizar os valores de um parâmetro dado as informações recebidas de um sensor
	public static void updateParametersValues(Main main) throws JMSException {
    	new MeasuredValuesReceiver(main).go();
    }

    public void go() throws JMSException {

    	try {
    		
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(this);
            
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

        
    }

	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			Object object;
			try {
				object = ((ObjectMessage) message).getObject();
				String[] request = (String[]) object;
				
				switch(request[0]) {
				
					case "temperatura":
						
						// Atualizar o objeto que representa o sensor de temperatura no Broker
						this.main.temperatureSensor.setMinValue(request[1]);
						this.main.temperatureSensor.setMaxValue(request[2]);
						this.main.temperatureSensor.setMeasuredValue(request[3]);
						this.main.temperatureSensor.setParameterStatus(request[5]);
						
						// Atualizar as labels do sensor de temperatura
				        Runnable updateTemperatureLabels = () -> {
				            Platform.runLater(() -> {
								this.main.temperatureIsMonitored.setText("Sim");
								this.main.temperatureIsMonitored.setTextFill(Color.GREEN);						
								this.main.temperatureSensorId.setText(this.main.temperatureSensor.getId());
								this.main.temperatureMinValue.setText(request[1]);
								this.main.temperatureMaxValue.setText(request[2]);
								this.main.temperatureMeasuredValue.setText(request[3]);
								this.main.temperatureMeasuredCondition.setText(request[5]);	
				            });
				        };
				        Thread updateTemperatureLabelsThread = new Thread(updateTemperatureLabels);
				        updateTemperatureLabelsThread.setDaemon(true);
				        updateTemperatureLabelsThread.start();
						
				        // Enviar aos clientes inscritos, caso necessário, a mensagem de alerta relacionada
				        // a temperatura
						for(Client client: this.main.registeredClients) {
							if(client.getSubscribedParameters().contains("temperatura")) {
								this.main.callParametersUpdatesSender(client, this.main.temperatureSensor);
							}
						}
						
						break;
					
					case "umidade":
						
						// Atualizar o objeto que representa o sensor de umidade no Broker
						this.main.humiditySensor.setMinValue(request[1]);
						this.main.humiditySensor.setMaxValue(request[2]);
						this.main.humiditySensor.setMeasuredValue(request[3]);
						this.main.humiditySensor.setParameterStatus(request[5]);
						
						// Atualizar as labels do sensor de umidade
				        Runnable updateHumidityLabels = () -> {
				            Platform.runLater(() -> {
								this.main.humidityIsMonitored.setText("Sim");
								this.main.humidityIsMonitored.setTextFill(Color.GREEN);						
								this.main.humiditySensorId.setText(this.main.humiditySensor.getId());
								this.main.humidityMinValue.setText(request[1]);
								this.main.humidityMaxValue.setText(request[2]);
								this.main.humidityMeasuredValue.setText(request[3]);
								this.main.humidityMeasuredCondition.setText(request[5]);	
				            });
				        };
				        Thread updateHumidityLabelsThread = new Thread(updateHumidityLabels);
				        updateHumidityLabelsThread.setDaemon(true);
				        updateHumidityLabelsThread.start();
						
				        // Enviar aos clientes inscritos, caso necessário, a mensagem de alerta relacionada
				        // a umidade
						for(Client client: this.main.registeredClients) {
							if(client.getSubscribedParameters().contains("umidade")) {
								this.main.callParametersUpdatesSender(client, this.main.humiditySensor);
							}
						}
						
						break;
						
					case "velocidade":
						
						// Atualizar o objeto que representa o sensor de velocidade no Broker
						this.main.velocitySensor.setMinValue(request[1]);
						this.main.velocitySensor.setMaxValue(request[2]);
						this.main.velocitySensor.setMeasuredValue(request[3]);
						this.main.velocitySensor.setParameterStatus(request[5]);
						
						// Atualizar as labels do sensor de velocidade
				        Runnable updateVelocityLabels = () -> {
				            Platform.runLater(() -> {
								this.main.velocityIsMonitored.setText("Sim");
								this.main.velocityIsMonitored.setTextFill(Color.GREEN);						
								this.main.velocitySensorId.setText(this.main.velocitySensor.getId());
								this.main.velocityMinValue.setText(request[1]);
								this.main.velocityMaxValue.setText(request[2]);
								this.main.velocityMeasuredValue.setText(request[3]);
								this.main.velocityMeasuredCondition.setText(request[5]);	
				            });
				        };
				        Thread updateVelocityLabelsThread = new Thread(updateVelocityLabels);
				        updateVelocityLabelsThread.setDaemon(true);
				        updateVelocityLabelsThread.start();
				        
				        // Enviar aos clientes inscritos, caso necessário, a mensagem de alerta relacionada
				        // a valocidade
						for(Client client: this.main.registeredClients) {
							if(client.getSubscribedParameters().contains("velocidade")) {
								this.main.callParametersUpdatesSender(client, this.main.velocitySensor);
							}
						}
						
						break;
					
					default:
						
						break;
				
				}
				
				// Se a lista de parâmetros monitorados não possuir o parâmetro analisado, adicionar-lo
				if(!this.main.usedParameters.contains(request[0])) {
					this.main.usedParameters.add(request[0]);
				}
				
				//this.main.callMeasuredValuesUpdateConsumer();
				
				// Enviar uma mensagem aos clientes com a lista de parâmetros monitorados atualizada
				this.main.callMonitoredParametersSender();	
				
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
