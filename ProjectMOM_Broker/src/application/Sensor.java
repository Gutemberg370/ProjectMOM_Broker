package application;

// Classe que armazena as informações de um sensor
public class Sensor {
	
	private String id;
	
	private String parameterMeasured;
	
	private String minValue;
	
	private String maxValue;
	
	private String measuredValue;
	
	private String parameterStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParameterMeasured() {
		return parameterMeasured;
	}

	public void setParameterMeasured(String parameterMeasured) {
		this.parameterMeasured = parameterMeasured;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMeasuredValue() {
		return measuredValue;
	}

	public void setMeasuredValue(String measuredValue) {
		this.measuredValue = measuredValue;
	}

	public String getParameterStatus() {
		return parameterStatus;
	}

	public void setParameterStatus(String messageToSend) {
		this.parameterStatus = messageToSend;
	}

	public Sensor(String id, String parameterMeasured, String minValue, String maxValue, String measuredValue,
			String parameterStatus) {
		this.id = id;
		this.parameterMeasured = parameterMeasured;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.measuredValue = measuredValue;
		this.parameterStatus = parameterStatus;
	}
	

}
