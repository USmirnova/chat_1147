package client; // пакет клиент

import java.io.DataInputStream; // импортируем необходимые библиотеки
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client { // название класса идентично названию файла
public static void main(String[] args) {// точка входа в клиентскую часть программы
	Scanner scanner = new Scanner(System.in); // сканнер для считывания данных с консоли
	try { // если порт не занят
		Socket socket = new Socket("127.0.0.1", 8188); //соединяем пользователя с сервером
		System.out.println("Пользователь успешно подключен!");
		DataInputStream in = new DataInputStream(socket.getInputStream()); // объект ввода для пользователя
		DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // объект вывода пользовательского сокета
		Thread thread = new Thread(new Runnable() { // отдельный поток для вывода серверных сообщений
			@Override // например сообщение о присоединении новых пользователей и рассылки сообщений
			public void run() {
				try { // соединение в порядке
					while (true) { // бесконечный цикл для
						String response = in.readUTF(); // считываем данне из объекта ввода
						System.out.println(response); // выводим сообщение сервера
					}
				} catch (IOException e) { // если соединение разорвалось
					System.out.println("Соединение разорвано");
				}
			}
		});
		thread.start(); // стартуем поток

		while (true) { // основной поток для общения клиента с сервером
			String text = scanner.nextLine(); // вводим сообщение
			out.writeUTF(text); /// отправляем на сервер
		}
	} catch (IOException exception) { // исключения при занятом порте
		exception.printStackTrace();
	}
}
}
