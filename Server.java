import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

public class Server extends Application {
	private String outWords;
	private DataInputStream fromClient;
	private DataOutputStream toClient;
	public void start(Stage stage) throws Exception {
		//界面
		Button bt=new Button("发送");
		BorderPane borderOfServer=new BorderPane();
		borderOfServer.setPadding(new Insets(10,10,10,10));
		borderOfServer.setStyle("-fx-border-color:red");
		borderOfServer.setRight(bt);
		
		TextField enterData=new TextField();
		enterData.setMaxSize(450, 30);
		enterData.setAlignment(Pos.BOTTOM_RIGHT);
		borderOfServer.setCenter(enterData);
		
		BorderPane pane=new BorderPane();
		TextArea textArea=new TextArea();
		pane.setBottom(borderOfServer);
		pane.setCenter(new ScrollPane(textArea));

		Scene scene = new Scene(pane,550,250);
		stage.setTitle("Server");
		stage.setScene(scene);
		stage.show();
		
		new Thread(() -> {
			try {
				ServerSocket serverSocket = new ServerSocket(8888);
				Platform.runLater(() -> textArea.appendText("ServerSocket started at " 
				+ new Date() + '\n'));
				Socket socket = serverSocket.accept();
				fromClient = new DataInputStream(socket.getInputStream());
				toClient = new DataOutputStream(socket.getOutputStream());

				while (true) {
					String inWords=fromClient.readUTF();
					Platform.runLater(() -> {
						textArea.appendText("客户端对服务器说：" + inWords + '\n');
					});
					
					bt.setOnAction(e->{
						try {
							outWords=enterData.getText().toString();
							if(outWords.equals("")==false) {
								toClient.writeUTF(outWords);
								textArea.appendText("服务器对客户端说：" + outWords + '\n');
								enterData.setText("");
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
}