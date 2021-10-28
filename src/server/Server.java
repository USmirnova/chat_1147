package server; // пакет сервер
// Лабораторная работа №
// чат для совместной работы с графической частью
// 27.09.2021_2 чат консольный многопоточный // Имя клиентов уникально // Личные сообщения // Список пользователей online
// Описание в файле java.docx
// E:\ITcourse\professional_2021\java_educational_projects\chat_compatible_gui_2710_2
import java.io.DataInputStream;// импортируем необходимые библиотеки
import java.io.DataOutputStream; // при написании кода тип данных красным цветом
import java.io.IOException; // при установки мыши предлагает подгрузить
import java.net.ServerSocket; //библиотеку нажав клавиши Alt+Enter
import java.net.Socket;
import java.util.ArrayList;

public class Server { // название класса идентично названию файла
public static void main(String[] args) { // точка входа в серверную часть программы
	ArrayList<User> users = new ArrayList<>(); // коллекция пользовательских объектов
	try { // если порт занят
		ServerSocket serverSocket = new ServerSocket(8188); // создаем сервер
		System.out.println("Сервер запущен");
		while (true) {// бесконечный цикл для общения с клиентами // основной поток
			Socket socket = serverSocket.accept(); // создаем клиентский сокет, ждем когда он подключится к серверу
			System.out.println("Подключился новый клиент.");
			User currentUser = new User(socket); // создаем объект текущего пользователя и сразу записываем в него сокет
			users.add(currentUser); // добавляем текущего пользователя в коллекцию подлкючившихся пользователей
			currentUser.setOut(new DataOutputStream(currentUser.getSocket().getOutputStream())); // объект вывода текущему пользователю
			currentUser.setIn(new DataInputStream(currentUser.getSocket().getInputStream())); // объект ввода текущему пользователю
			Thread thread = new Thread(new Runnable() { // в отдельном потоке
				@Override
				public void run() {
					String text; // переменная с текстом сообщений
					try { // пользователь не отвалился
						while (currentUser.getUserName()==null) { // цикл бесконечен пока текущий пользователь не введет адекватное имя
							currentUser.getOut().writeUTF("Введите имя: "); // запрашиваем имя у только что подключившегося текущего пользователя
							String userName = currentUser.getIn().readUTF(); // из потока ввода считываем имя

							if (userName.length()<3 || userName.indexOf(" ")>-1) {// проверка на Enter, пробел, короткое имя
								currentUser.getOut().writeUTF("Имя должно содержать минимум 3 символа и не должно содержать пробелы.");
							}
							else { // имя адекватное
								boolean identity = false;
								for (User user : users) { // перебираем коллекцию
									if (user.getUserName()!=null) { // если имя задано
										if(user.getUserName().equals(userName)) { // сравниваем. если совпадает с только что введенным
											identity = true; // меняем значение переменной
										}
									}
								}
								if (identity == true) {
									currentUser.getOut().writeUTF("Пользователь с таким именем уже существует, придумайте другое имя.");
								}
								else {
									currentUser.setUserName(userName); // устанавливаем имя текущему пользователю
									currentUser.getOut().writeUTF("Доброго времени "+currentUser.getUserName()+"! Вы присоединились к беседе.");
									for (User us : users) { // рассылка всем пользователям сообщения о новом собеседнике
										if (!us.equals(currentUser)) { // кроме отправителя
											us.getOut().writeUTF("Пользователь "+currentUser.getUserName()+" присоеденился к беседе.");
										}
									}
								}
							}
						}

						while (true) { // бесконечный цикл
							text = currentUser.getIn().readUTF(); // считываем данные с консоли
							if(text.equals("/onlineUsers")) { // список подключенных пользователей
								String names = "Пользователи онлайн: ";
								for (User user: users) {
									names += user.getUserName()+", "; //строка со всеми пользователями онлайн
								}
								currentUser.getOut().writeUTF(names); // Отправили список через объект вывода
							}
							else if (text.indexOf("/m")==0) { // отправка личных сообщений
								String[] arr = text.split(" "); // строку разбиваем на массив
								String recipientName = arr[1]; // вычленяем имя рецепиента
								text = ""; // обнуляем переменную
								for (int i = 2; i < arr.length; i++) { //
									text += " "+arr[i]; // записываем в строку только сообщение реципиенту
								}
								boolean recipientIsHere = false;
								for (User user : users) {
									if (user.getUserName().equals(recipientName)) {  // именно этому пользователю отошлем
										user.getOut().writeUTF("личное от "+currentUser.getUserName() + ":" + text);
										recipientIsHere = true;
										//break; // как только нашли выходим из цикла
									}
								}
								
								if (recipientIsHere == false) currentUser.getOut().writeUTF("Пользователь "+recipientName + " отсутствует в чате. Ваше сообщение не доставлено!");
							}
							else { // другие сообщения
								System.out.println(currentUser.getUserName()+": " + text); // вывод сообщения name:text
								//mailing(users, currentUser, text); // создать метод не удалось
								for (User user : users) {
									if (currentUser.getUuid().equals(user.getUuid())) continue; // текущему пользователю рассылать не будем
									user.getOut().writeUTF(currentUser.getUserName()+": " + text); // рассылка сообщения name:text всем кроме отправившего
								}
							}

						}
					} catch (IOException exception) { // пользователь отвалился
						users.remove(currentUser); // удаляем его из коллекции подключенных, иначе при рассылке будет ошибка
						for (User user : users) { // перебор подключенных
							try { // здесь без try-catch нельзя
								user.getOut().writeUTF("Пользователь "+currentUser.getUserName()+" покинул беседу"); // рассылка пользователям
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						System.out.println("Пользователь "+currentUser.getUserName()+" покинул беседу"); //выводим на в консоли сервера
					}
				}
			});
			thread.start(); // запуск потока
		}
	} catch (IOException exception) { // если порт занят
		exception.printStackTrace();
	}
}
}
