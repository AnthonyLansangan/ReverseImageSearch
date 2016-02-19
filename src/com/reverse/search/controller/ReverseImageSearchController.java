package com.reverse.search.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReverseImageSearchController {
	@RequestMapping("/welcome")
	public ModelAndView helloWorld(@RequestParam("file") MultipartFile file) {
		List<String> resultUrls = new ArrayList<String>();
		String imageFile = "";
		try {

			if (!file.isEmpty()) {
				try {
					byte[] bytes = file.getBytes();

					// Creating the directory to store file
					File dir = new File("/Users/AnthonyLansangan/Desktop/download");
					if (!dir.exists())
						dir.mkdirs();
					String name = "imageToSearch.png";
					// Create the file on server
					File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
					stream.write(bytes);
					stream.close();
					imageFile = serverFile.getAbsolutePath();

					} catch (Exception e) {
				}
			} 
			String url = "https://www.google.co.in/searchbyimage/upload";

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost(url);
			uploadFile.setHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36");

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("encoded_image", new File(imageFile), ContentType.APPLICATION_OCTET_STREAM,
					"image.png");
			builder.addTextBody("image_url", "", ContentType.TEXT_PLAIN);
			builder.addTextBody("image_content", "", ContentType.TEXT_PLAIN);
			builder.addTextBody("filename", "", ContentType.TEXT_PLAIN);
			builder.addTextBody("h1", "en", ContentType.TEXT_PLAIN);
			builder.addTextBody("bih", "179", ContentType.TEXT_PLAIN);
			builder.addTextBody("biw", "1600", ContentType.TEXT_PLAIN);
			HttpEntity multipart = builder.build();

			uploadFile.setEntity(multipart);

			CloseableHttpResponse response = httpClient.execute(uploadFile);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			String urlFromResponse = "";
			while ((line = rd.readLine()) != null) {
				if (line.indexOf("HREF") > 0) {
					System.out.println(line.substring(9, line.length() - 11));
					urlFromResponse = line.substring(9, line.length() - 11);
				}
			}
			URL newUrl = new URL(urlFromResponse);
			URLConnection connection = newUrl.openConnection();
			connection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36");
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String htmlString = "";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				htmlString += line;
			}
			System.out.println(htmlString);

			org.jsoup.nodes.Document doc = Jsoup.parse(htmlString);

			Elements bodyBlock = doc.getElementsByClass("srg");
			for (Element block : bodyBlock) {
				String citeUrl = block.html();
				System.out.println("Cite Found!");
				System.out.println("Site url = " + citeUrl);
				resultUrls.add(citeUrl);
			}
			// Elements cites = doc.getElementsByTag("cite");
			// for(Element cite : cites){
			// String citeUrl = cite.text();
			// System.out.println("Cite Found!");
			// System.out.println("Site url = "+ citeUrl);
			// resultUrls.add(citeUrl);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		String message = "Reverse Image Search";
		return new ModelAndView("welcome", "message", resultUrls);
	}
}
