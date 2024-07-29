module ProjectMOM_Broker {
	requires javafx.controls;
	requires activemq.all;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
}
