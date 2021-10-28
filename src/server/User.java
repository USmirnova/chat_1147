package server; // в пакете сервер создаем класс User.java
// здесь не нужна точка входа psvm(public static void main(String[] args))
// мы создаем класс клиента по образу которого будут созданы объекты на сервере
import java.io.DataInputStream; // импортируем необходимые библиотеки
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.UUID; //библиотека для уникального идентификатора

public class User {
private Socket socket;
private String userName;
private UUID uuid; // уникальный идентификатор пользователя
private DataOutputStream out; // объект вывода данных
private DataInputStream in; // объект ввода данных

public User(Socket socket) { // конструктор класса
	this.socket = socket; // сокет будем передавать в конструкторе при создании объекта
	this.uuid = UUID.randomUUID(); // Уникальный идентификатор пользователя задаем прямо тут. При создании объекта он будет формироваться.
}
// геттеры для показа приватных данных
public Socket getSocket() { return socket; } // передается в конструкторе при объявлении объекта
public String getUserName() { return userName; } // если имя не задано, то вернется null
public UUID getUuid() { return uuid; } // формируется внутри класса
public DataOutputStream getOut() { return out; } // для вызова объектов вывода // устанавливаем в основном потоке
public DataInputStream getIn() { return in; } // и ввода клиентским соккетам   // устанавливаем в основном потоке
// сеттеры для установления значений приватным переменным
public void setUserName(String userName) { this.userName = userName; } // устанавливаем в отдельном потоке
public void setOut(DataOutputStream out) { this.out = out; } // для установки объектов        // устанавливаем в основном потоке
public void setIn(DataInputStream in) { this.in = in; } // вывода и ввода клиентским соккетам // устанавливаем в основном потоке
}
