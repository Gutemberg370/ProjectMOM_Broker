package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	
	private BrokerTopicController brokerTopicController = new BrokerTopicController(this);
	private TopicUpdater topicUpdater = new TopicUpdater(this);
	private MeasuredValuesReceiver measuredValuesReceiver = new MeasuredValuesReceiver(this);
	private MonitoredParametersSender monitoredParametersSender = new MonitoredParametersSender(this);
	private SubscriptionRequestReceiver subscriptionRequestReceiver = new SubscriptionRequestReceiver(this);
	private ParametersUpdatesSender parametersUpdatesSender = new ParametersUpdatesSender(this);
	
	public String[] allParameters = {"temperatura","umidade","velocidade"};
	public ArrayList<String> availableParameters = new ArrayList<String>(Arrays.asList(allParameters));
	public ArrayList<String> usedParameters = new ArrayList<String>();
	public ArrayList<Client> registeredClients = new ArrayList<Client>();
	
	public Sensor temperatureSensor;
	public Sensor humiditySensor;
	public Sensor velocitySensor;
	
	// Serviço responsável por executar uma thread mais de uma vez
	final ExecutorService service = Executors.newCachedThreadPool();
	
	
	//As labels do Broker
	
	//Temperatura
	Label temperatureIsMonitored;	
	Label temperatureSensorId;
	Label temperatureMinValue;
	Label temperatureMaxValue;
	Label temperatureMeasuredValue;
	Label temperatureMeasuredCondition;
	
	//Umidade
	Label humidityIsMonitored;
	Label humiditySensorId;
	Label humidityMinValue;
	Label humidityMaxValue;
	Label humidityMeasuredValue;
	Label humidityMeasuredCondition;
	
	//Velocidade
	Label velocityIsMonitored;
	Label velocitySensorId;
	Label velocityMinValue;
	Label velocityMaxValue;
	Label velocityMeasuredValue;
	Label velocityMeasuredCondition;
	
	
	// Thread responsável por receber dos sensores quais parâmetros ainda estão sem monitoramento
	final class ParametersUpdateConsumer implements Runnable {
	    @Override
	    public void run() {
        	try {
        		topicUpdater.receiveParametersnotUsed();
			} catch (JMSException e) {
				e.printStackTrace();
			}	

	    }
	}; 
	
	// Thread responsável por receber os valores determinados por um sensor para um determinado parâmetro
	// monitorado por ele
	final class MeasuredValuesUpdateConsumer implements Runnable {
	    @Override
	    public void run() {
        	try {
        		measuredValuesReceiver.updateParametersValues(Main.this);
			} catch (JMSException e) {
				e.printStackTrace();
			}	

	    }
	};
	
	// Thread responsável por receber as solicitações de inscrição dos clientes
	final class ClientSubscriptionRealizer implements Runnable {
	    @Override
	    public void run() {
        	try {
        		subscriptionRequestReceiver.receiveSubscriptionRequest();
			} catch (JMSException e) {
				e.printStackTrace();
			}	

	    }
	};
	
	public void callBrokerTopicController() throws JMSException {
		brokerTopicController.sendAvaliableParameters();
	}
	
	public void callParametersUpdateConsumer() {
		service.submit(new ParametersUpdateConsumer());
	}
	
	public void callMeasuredValuesUpdateConsumer() {
		service.submit(new MeasuredValuesUpdateConsumer());
	}
	
	public void callMonitoredParametersSender() throws JMSException {
		monitoredParametersSender.sendMonitoredParameters();
	}
	
	public void callSubscriptionRequestReceiver() {
		service.submit(new ClientSubscriptionRealizer());
	}
	
	public void callParametersUpdatesSender(Client client, Sensor sensor) throws JMSException {
		parametersUpdatesSender.sendParameterSubscribed(client, sensor);
	}
	
	public void sendTopicsAndWait() throws JMSException {
		
		callBrokerTopicController();
		callParametersUpdateConsumer();
		callMeasuredValuesUpdateConsumer();
		callSubscriptionRequestReceiver();
	}
	
	private void startSensors() {
		temperatureSensor = new Sensor("temperaturaSensor","temperatura","-","-","-","Valor medido dentro dos limites.");
		humiditySensor = new Sensor("umidadeSensor","umidade","-","-","-","Valor medido dentro dos limites.");
		velocitySensor = new Sensor("velocidadeSensor","velocidade","-","-","-","Valor medido dentro dos limites.");
	}
	
	private void startLabels() {
		
		//Temperatura
		temperatureIsMonitored = new Label("Não");
		temperatureIsMonitored.setFont(new Font("Monaco",14));
		temperatureIsMonitored.setTextFill(Color.color(1, 0, 0));
		temperatureIsMonitored.setLayoutX(215);
		temperatureIsMonitored.setLayoutY(133);
		temperatureIsMonitored.setTextAlignment(TextAlignment.CENTER);
		
		temperatureSensorId = new Label("-");
		temperatureSensorId.setFont(new Font("Monaco",14));
		temperatureSensorId.setLayoutX(155);
		temperatureSensorId.setLayoutY(153);
		temperatureSensorId.setTextAlignment(TextAlignment.CENTER);
		
	    temperatureMinValue = new Label("-");
		temperatureMinValue.setFont(new Font("Monaco",14));
		temperatureMinValue.setLayoutX(185);
		temperatureMinValue.setLayoutY(173);
		temperatureMinValue.setTextAlignment(TextAlignment.CENTER);
		
		temperatureMaxValue = new Label("-");
		temperatureMaxValue.setFont(new Font("Monaco",14));
		temperatureMaxValue.setLayoutX(185);
		temperatureMaxValue.setLayoutY(193);
		temperatureMaxValue.setTextAlignment(TextAlignment.CENTER);
		
		temperatureMeasuredValue = new Label("-");
		temperatureMeasuredValue.setFont(new Font("Monaco",14));
		temperatureMeasuredValue.setLayoutX(185);
		temperatureMeasuredValue.setLayoutY(213);
		temperatureMeasuredValue.setTextAlignment(TextAlignment.CENTER);
		
		temperatureMeasuredCondition = new Label("-");
		temperatureMeasuredCondition.setFont(new Font("Monaco",14));
		temperatureMeasuredCondition.setLayoutX(25);
		temperatureMeasuredCondition.setLayoutY(253);
		temperatureMeasuredCondition.setTextAlignment(TextAlignment.LEFT);
		
		//Umidade
		humidityIsMonitored = new Label("Não");
		humidityIsMonitored.setFont(new Font("Monaco",14));
		humidityIsMonitored.setTextFill(Color.color(1, 0, 0));
		humidityIsMonitored.setLayoutX(450);
		humidityIsMonitored.setLayoutY(133);
		humidityIsMonitored.setTextAlignment(TextAlignment.CENTER);
		
		humiditySensorId = new Label("-");
		humiditySensorId.setFont(new Font("Monaco",14));
		humiditySensorId.setLayoutX(415);
		humiditySensorId.setLayoutY(153);
		humiditySensorId.setTextAlignment(TextAlignment.CENTER);
		
		humidityMinValue = new Label("-");
		humidityMinValue.setFont(new Font("Monaco",14));
		humidityMinValue.setLayoutX(420);
		humidityMinValue.setLayoutY(173);
		humidityMinValue.setTextAlignment(TextAlignment.CENTER);
		
		humidityMaxValue = new Label("-");
		humidityMaxValue.setFont(new Font("Monaco",14));
		humidityMaxValue.setLayoutX(420);
		humidityMaxValue.setLayoutY(193);
		humidityMaxValue.setTextAlignment(TextAlignment.CENTER);
		
		humidityMeasuredValue = new Label("-");
		humidityMeasuredValue.setFont(new Font("Monaco",14));
		humidityMeasuredValue.setLayoutX(420);
		humidityMeasuredValue.setLayoutY(213);
		humidityMeasuredValue.setTextAlignment(TextAlignment.CENTER);
		
		humidityMeasuredCondition = new Label("-");
		humidityMeasuredCondition.setFont(new Font("Monaco",14));
		humidityMeasuredCondition.setLayoutX(285);
		humidityMeasuredCondition.setLayoutY(253);
		humidityMeasuredCondition.setTextAlignment(TextAlignment.LEFT);
		
		//Valocidade
		velocityIsMonitored = new Label("Não");
		velocityIsMonitored.setFont(new Font("Monaco",14));
		velocityIsMonitored.setTextFill(Color.color(1, 0, 0));
		velocityIsMonitored.setLayoutX(715);
		velocityIsMonitored.setLayoutY(133);
		velocityIsMonitored.setTextAlignment(TextAlignment.CENTER);
		
		velocitySensorId = new Label("-");
		velocitySensorId.setFont(new Font("Monaco",14));
		velocitySensorId.setLayoutX(665);
		velocitySensorId.setLayoutY(153);
		velocitySensorId.setTextAlignment(TextAlignment.CENTER);
		
		velocityMinValue = new Label("-");
		velocityMinValue.setFont(new Font("Monaco",14));
		velocityMinValue.setLayoutX(680);
		velocityMinValue.setLayoutY(173);
		velocityMinValue.setTextAlignment(TextAlignment.CENTER);
		
		velocityMaxValue = new Label("-");
		velocityMaxValue.setFont(new Font("Monaco",14));
		velocityMaxValue.setLayoutX(680);
		velocityMaxValue.setLayoutY(193);
		velocityMaxValue.setTextAlignment(TextAlignment.CENTER);
		
		velocityMeasuredValue = new Label("-");
		velocityMeasuredValue.setFont(new Font("Monaco",14));
		velocityMeasuredValue.setLayoutX(680);
		velocityMeasuredValue.setLayoutY(213);
		velocityMeasuredValue.setTextAlignment(TextAlignment.CENTER);
		
		velocityMeasuredCondition = new Label("-");
		velocityMeasuredCondition.setFont(new Font("Monaco",14));
		velocityMeasuredCondition.setLayoutX(535);
		velocityMeasuredCondition.setLayoutY(253);
		velocityMeasuredCondition.setTextAlignment(TextAlignment.LEFT);
		
		
	}
	
	// Função que atualiza a lista de parâmetros disponíveis e a lista de monitorados
	public void updateAvailableParameters(ArrayList<String> parametersNotUsed) {
		brokerTopicController.parameters = parametersNotUsed.toArray(new String[0]);
		availableParameters = parametersNotUsed;
		for (String element : allParameters) {
		    if(!availableParameters.contains(element) && !usedParameters.contains(element)) {
		    	usedParameters.add(element);
		    }
		}
	}
	
	// Função que cria a página do Broker
	private Parent createBrokerPage() {
		
    	Pane root = new Pane();
    	
    	BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf("#bbb3ec"), new CornerRadii(10), new Insets(10));

    	Background background = new Background(backgroundFill);
    	
    	root.setBackground(background);
    	
    	root.setPrefSize(790, 372);
    	           
    	
    	Label broker = new Label("BROKER ONLINE");
    	broker.setFont(new Font("Monaco",36));
    	broker.setTextFill(Color.GREEN);
    	broker.setLayoutX(255);
    	broker.setLayoutY(20);
    	broker.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureLabel = new Label("Temperatura");
    	temperatureLabel.setFont(new Font("Monaco",20));
    	temperatureLabel.setLayoutX(60);
    	temperatureLabel.setLayoutY(90);
    	temperatureLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureIsMonitoredLabel = new Label("Temperatura monitorada?");
    	temperatureIsMonitoredLabel.setFont(new Font("Monaco",16));
    	temperatureIsMonitoredLabel.setLayoutX(25);
    	temperatureIsMonitoredLabel.setLayoutY(130);
    	temperatureIsMonitoredLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureSensorIdLabel = new Label("Nome do sensor:");
    	temperatureSensorIdLabel.setFont(new Font("Monaco",16));
    	temperatureSensorIdLabel.setLayoutX(25);
    	temperatureSensorIdLabel.setLayoutY(150);
    	temperatureSensorIdLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureMinValueLabel = new Label("Temperatura mínima:");
    	temperatureMinValueLabel.setFont(new Font("Monaco",16));
    	temperatureMinValueLabel.setLayoutX(25);
    	temperatureMinValueLabel.setLayoutY(170);
    	temperatureMinValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureMaxValueLabel = new Label("Temperatura máxima:");
    	temperatureMaxValueLabel.setFont(new Font("Monaco",16));
    	temperatureMaxValueLabel.setLayoutX(25);
    	temperatureMaxValueLabel.setLayoutY(190);
    	temperatureMaxValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureMeasuredValueLabel = new Label("Temperatura medida:");
    	temperatureMeasuredValueLabel.setFont(new Font("Monaco",16));
    	temperatureMeasuredValueLabel.setLayoutX(25);
    	temperatureMeasuredValueLabel.setLayoutY(210);
    	temperatureMeasuredValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label temperatureMessageToSendLabel = new Label("Condição do parâmetro:");
    	temperatureMessageToSendLabel.setFont(new Font("Monaco",16));
    	temperatureMessageToSendLabel.setLayoutX(25);
    	temperatureMessageToSendLabel.setLayoutY(230);
    	temperatureMessageToSendLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humidityLabel = new Label("Umidade");
    	humidityLabel.setFont(new Font("Monaco",20));
    	humidityLabel.setLayoutX(328);
    	humidityLabel.setLayoutY(90);
    	humidityLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humidityIsMonitoredLabel = new Label("Umidade monitorada?");
    	humidityIsMonitoredLabel.setFont(new Font("Monaco",16));
    	humidityIsMonitoredLabel.setLayoutX(285);
    	humidityIsMonitoredLabel.setLayoutY(130);
    	humidityIsMonitoredLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humiditySensorIdLabel = new Label("Nome do sensor:");
    	humiditySensorIdLabel.setFont(new Font("Monaco",16));
    	humiditySensorIdLabel.setLayoutX(285);
    	humiditySensorIdLabel.setLayoutY(150);
    	humiditySensorIdLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humidityMinValueLabel = new Label("Umidade mínima:");
    	humidityMinValueLabel.setFont(new Font("Monaco",16));
    	humidityMinValueLabel.setLayoutX(285);
    	humidityMinValueLabel.setLayoutY(170);
    	humidityMinValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humidityMaxValueLabel = new Label("Umidade máxima:");
    	humidityMaxValueLabel.setFont(new Font("Monaco",16));
    	humidityMaxValueLabel.setLayoutX(285);
    	humidityMaxValueLabel.setLayoutY(190);
    	humidityMaxValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humidityMeasuredValueLabel = new Label("Umidade medida:");
    	humidityMeasuredValueLabel.setFont(new Font("Monaco",16));
    	humidityMeasuredValueLabel.setLayoutX(285);
    	humidityMeasuredValueLabel.setLayoutY(210);
    	humidityMeasuredValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label humidityMessageToSendLabel = new Label("Condição do parâmetro:");
    	humidityMessageToSendLabel.setFont(new Font("Monaco",16));
    	humidityMessageToSendLabel.setLayoutX(285);
    	humidityMessageToSendLabel.setLayoutY(230);
    	humidityMessageToSendLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocityLabel = new Label("Velocidade");
    	velocityLabel.setFont(new Font("Monaco",20));
    	velocityLabel.setLayoutX(570);
    	velocityLabel.setLayoutY(90);
    	velocityLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocityIsMonitoredLabel = new Label("Velocidade monitorada?");
    	velocityIsMonitoredLabel.setFont(new Font("Monaco",16));
    	velocityIsMonitoredLabel.setLayoutX(535);
    	velocityIsMonitoredLabel.setLayoutY(130);
    	velocityIsMonitoredLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocitySensorIdLabel = new Label("Nome do sensor:");
    	velocitySensorIdLabel.setFont(new Font("Monaco",16));
    	velocitySensorIdLabel.setLayoutX(535);
    	velocitySensorIdLabel.setLayoutY(150);
    	velocitySensorIdLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocityMinValueLabel = new Label("Velocidade mínima:");
    	velocityMinValueLabel.setFont(new Font("Monaco",16));
    	velocityMinValueLabel.setLayoutX(535);
    	velocityMinValueLabel.setLayoutY(170);
    	velocityMinValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocityMaxValueLabel = new Label("Velocidade máxima:");
    	velocityMaxValueLabel.setFont(new Font("Monaco",16));
    	velocityMaxValueLabel.setLayoutX(535);
    	velocityMaxValueLabel.setLayoutY(190);
    	velocityMaxValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocityMeasuredValueLabel = new Label("Velocidade medida:");
    	velocityMeasuredValueLabel.setFont(new Font("Monaco",16));
    	velocityMeasuredValueLabel.setLayoutX(535);
    	velocityMeasuredValueLabel.setLayoutY(210);
    	velocityMeasuredValueLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Label velocityMessageToSendLabel = new Label("Condição do parâmetro:");
    	velocityMessageToSendLabel.setFont(new Font("Monaco",16));
    	velocityMessageToSendLabel.setLayoutX(535);
    	velocityMessageToSendLabel.setLayoutY(230);
    	velocityMessageToSendLabel.setTextAlignment(TextAlignment.CENTER);
    	
    	Button sendTopicsAvailablesAndMonitoredButton = new Button("Enviar tópicos para Clientes e Sensores");
    	sendTopicsAvailablesAndMonitoredButton.setLayoutX(265);
    	sendTopicsAvailablesAndMonitoredButton.setLayoutY(320);
    	sendTopicsAvailablesAndMonitoredButton.setMinWidth(150);
    	sendTopicsAvailablesAndMonitoredButton.setOnAction(event -> {
    		
    		// No pressionar do botão, se é enviado para os sensores os parâmetros não monitorados e
    		// , para os clientes, os parâmetros monitorados
    		try {
				callBrokerTopicController();
			} catch (JMSException e) {
				e.printStackTrace();
			}
    		
    		try {
				callMonitoredParametersSender();
			} catch (JMSException e) {
				e.printStackTrace();
			}
    		
        });
    	
  	
    	root.getChildren().addAll(broker, temperatureLabel, temperatureIsMonitoredLabel, temperatureIsMonitored, temperatureSensorIdLabel, temperatureSensorId,
    			                  temperatureMinValueLabel, temperatureMinValue, temperatureMaxValueLabel, temperatureMaxValue, temperatureMeasuredValueLabel, temperatureMeasuredValue,
    			                  humidityLabel, temperatureMessageToSendLabel, temperatureMeasuredCondition, humidityIsMonitoredLabel, humidityIsMonitored, humiditySensorIdLabel, humiditySensorId,
    			                  humidityMinValueLabel, humidityMinValue, humidityMaxValueLabel, humidityMaxValue, humidityMeasuredValueLabel, humidityMeasuredValue, humidityMessageToSendLabel, 
    			                  humidityMeasuredCondition, velocityLabel, velocityIsMonitoredLabel, velocityIsMonitored, velocitySensorIdLabel, velocitySensorId, velocityMinValueLabel, 
    			                  velocityMinValue, velocityMaxValueLabel, velocityMaxValue, velocityMeasuredValueLabel, velocityMeasuredValue, velocityMessageToSendLabel, velocityMeasuredCondition,
    			                  sendTopicsAvailablesAndMonitoredButton);
    	
    	return root;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			startSensors();
			startLabels();
			Scene brokerPage = new Scene(createBrokerPage());
			primaryStage.setTitle("Broker");
			primaryStage.setScene(brokerPage);
			primaryStage.show();
			sendTopicsAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}
