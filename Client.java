import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

public class Client extends Application {
	private DataOutputStream toServer= null;
	private DataInputStream fromServer = null;
	private String outWords;
	private String inWords;
	public void start(Stage stage) throws Exception {
		Button bt=new Button("发送");
		BorderPane borderForInput = new BorderPane();
		borderForInput.setPadding(new Insets(10, 10, 10, 10));
		borderForInput.setStyle("-fx-border-color:orange");
		borderForInput.setRight(bt);

		TextField enterData = new TextField();
		enterData.setMaxSize(450, 30);
		enterData.setAlignment(Pos.BOTTOM_RIGHT);
		borderForInput.setCenter(enterData);

		BorderPane pane = new BorderPane();
		TextArea textArea = new TextArea();
		pane.setBottom(borderForInput);
		pane.setCenter(new ScrollPane(textArea));

		Scene scene = new Scene(pane, 550, 250);
		stage.setScene(scene);
		stage.setTitle("Client");
		stage.show();
		
		try {
			Socket socket = new Socket("localhost", 8888);
			fromServer=new DataInputStream(socket.getInputStream());
			toServer=new DataOutputStream(socket.getOutputStream());
			textArea.appendText("succeeded connecting to the server at "
			+new Date()+"\n");
		} catch (IOException ex) {
			textArea.appendText(ex.toString() + '\n');
		}
		new Thread(()->{
			Platform.runLater(()->{
				bt.setOnAction(e->{
					try {
						outWords=enterData.getText().toString();
						if(outWords.equals("")==false) {
							toServer.writeUTF(outWords);
							toServer.flush();
							Platform.runLater(()->{
								textArea.appendText("客户端对服务器说："+outWords+"\n");
								enterData.setText("");
							});	
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
				});
			});
		}).start();;
		new Thread(()->{
			while(true) {//时刻监测服务器所发送的信息
				try {
					inWords=fromServer.readUTF();
					if(!inWords.equals("")) {
						Platform.runLater(()->{
							textArea.appendText("服务器对客户端说："+inWords+'\n');
						});
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}).start();;
	}
}