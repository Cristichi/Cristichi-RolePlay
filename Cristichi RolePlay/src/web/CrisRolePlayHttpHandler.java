package web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class CrisRolePlayHttpHandler implements HttpHandler {
	private String html;

	public CrisRolePlayHttpHandler(String html) {
		this.html = html;
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream is = t.getRequestBody();
		int i;
		boolean newVar = false, readName = true;
		HashMap<String, String> postData = new HashMap<>();
		{
			String varName = "", varData = "";
//			while ((i = is.read()) != -1) {
			while (true) {
				i = is.read();
				System.out.println("Letra: " + (char) i);
				sb.append((char) i);
				if (readName) {
					if ((char) i == '=') {
						System.out.println("Se terminó el nombre");
						readName = false;
					} else {
						System.out.println("Añadido al nombre");
						varName += (char) i;
					}
				} else {
					if ((char) i == '&' || i==-1) {
						System.out.println("Se terminó el valor");
						newVar = true;
					} else if ((char) i == '+'){
						varData+=" ";
					} else {
						System.out.println("Añadido al valor");
						varData += (char) i;
					}
				}
				
				if (newVar) {
					System.out.println("Se ha terminado esta variable con nombre: " + varName + " y valor " + varData);
					postData.put(varName, varData);
					varName = "";
					varData = "";
					newVar = false;
					readName = true;
				}
				
				if (i == -1) {
					break;
				}
			}
		}
		System.out.println("Datos: " + sb.toString());
		postData.forEach((k, v) -> System.out.println(("Nombre: \"" + k + "\" -Valor: \"" + v + "\"")));

		t.sendResponseHeaders(200, html.length());
		OutputStream os = t.getResponseBody();
		os.write(html.getBytes());
		os.close();
	}

}
