package br.com.brenfeer.model.util;

public class CaminhoArquivos {

	private static final String WINDOWS_ROOT_PATH = "C:\\Projeto Robo Brenfeer";
	private static final String LINUX_ROOT_PATH = "/home/dev/Pasta Projetos Execucao/Projeto Robo Brenfeer";

	public String getCaminhoArquivos() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return ajustarCaminhosArquivos(WINDOWS_ROOT_PATH + "\\");
		} else {
			return ajustarCaminhosArquivos(LINUX_ROOT_PATH + "/");
		}
	}

	public String ajustarCaminhosArquivos(String path) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return path.replace("/", "\\");
		} else {
			return path.replace("\\", "/");
		}
	}

	public String getGeckoDriver() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return WINDOWS_ROOT_PATH + "bin\\webdriver\\geckodriver.exe";
		} else {
			return LINUX_ROOT_PATH + "bin/webdriver/geckodriver";
		}
	}

	public String getChromeDriver() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return WINDOWS_ROOT_PATH + "bin\\webdriver\\chromedriver.exe";
		} else {
			return LINUX_ROOT_PATH + "bin/webdriver/chromedriver";
		}
	}

	
}
