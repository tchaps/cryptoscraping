package com.example.cryptoscraping;

import com.example.cryptoscraping.models.Crypto;
import com.example.cryptoscraping.services.ICryptoService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class CryptoscrapingApplication {

	public static final String BASE_URL="https://coinmarketcap.com/";
	public static final String NEW_URL ="https://coinmarketcap.com/new/";

	public static final String CLASS_CRYPTO_TABLE="h7vnx2-2 deceFm cmc-table  ";

	public static final String CLASS_CELL_CRYPTO_NAME="sc-1eb5slv-0 iworPT";

	public static final String CLASS_CELL_CRYPTO_TOKEN="sc-1eb5slv-0 gGIpIK coin-item-symbol";

	@Autowired
	private ICryptoService service;

	public static void main(String[] args) {
		SpringApplication.run(CryptoscrapingApplication.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			try {
				Document document = Jsoup.connect(NEW_URL).get();
				Elements cryptoTables = document.getElementsByClass(CLASS_CRYPTO_TABLE);
				if (cryptoTables != null) {
					for (Element it : cryptoTables) {
						List<Crypto> cryptos = treatTableElement(it);
						cryptos.forEach(crypto -> service.create(crypto));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
	}

	public static List<Crypto> treatTableElement(Element table) {
		Elements listCryptos = table.select("tr");
		return listCryptos.stream()
				.filter(element -> element.getElementsByClass("sc-1eb5slv-0 iworPT").size()>0)
				.map(CryptoscrapingApplication::extractCrypto)
				.map(CryptoscrapingApplication::getLinkToExplorer)
				.filter(crypto -> crypto.getLinkCmc() != null)
				.map(CryptoscrapingApplication::extractMetadata)
				.collect(Collectors.toList());
	}

	public static Crypto extractMetadata(Crypto crypto){
		try {
			Document document = Jsoup.connect(crypto.getLinkCmc()).get();
			Element holderEmplacement = document.getElementById("ContentPlaceHolder1_tr_tokenHolders");
			if(holderEmplacement != null) {
				String holders = holderEmplacement.getElementsByClass("mr-3").get(0).html();
				crypto.setHolders(holders);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return crypto;
	}

	public static Crypto getLinkToExplorer(Crypto crypto){
		try {
			Document document = Jsoup.connect(crypto.getLinkCmc()).get();
			Elements idExplorer = document.getElementsByClass("sc-10up5z1-5 jlEjUY");
			if(!idExplorer.isEmpty()){
				Elements cmclink = idExplorer.get(0).getElementsByClass("cmc-link");
				if (!cmclink.isEmpty()){
					String linkToExplorer = cmclink.get(0).attributes().get("href");
					crypto.setLinkCmc(linkToExplorer+"#balances");
					return crypto;
				}
			}
			crypto.setLinkCmc(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return crypto;
	}

	public static Crypto extractCrypto(Element row) {
		Elements cols = row.select("td");
		Crypto crypto = new Crypto();
		Elements linkCmc = row.getElementsByClass("cmc-link");
		String link = linkCmc.get(0).attributes().get("href");
		crypto.setLinkCmc(BASE_URL+link);
		for (int i = 2; i < cols.size(); i++) {
			Element col = cols.get(i);
			if (i == 2){
				Element cryptoName = col.getElementsByClass(CLASS_CELL_CRYPTO_NAME).get(0);
				Element cryptoCoin = col.getElementsByClass(CLASS_CELL_CRYPTO_TOKEN).get(0);
				crypto.setName(cryptoName.html());
				crypto.setCoinName(cryptoCoin.html());
			}
			if (i==3) {
				Elements price = col.select("span");
				crypto.setPrice(price.html());
			}
			if (i==6) {
				crypto.setMarketcap(col.html());
			}
			if (i==7) {
				crypto.setVolume(col.html());
			}
			if (i==9) {
				crypto.setAge(col.html());
			}
		}
		return crypto;
	}
}
