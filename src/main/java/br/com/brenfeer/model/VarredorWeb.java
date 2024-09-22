package br.com.brenfeer.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VarredorWeb {

	private String codigoFormatado = null;
	private String descricaoProduto = null;
	private String valorOriginal = null;
	private String valorDesconto = null;
	private String valorImpostos = null;
	private String campoQtdeDisponivelEstoque = null;
	private String quantidadeDisponivelFormatada = null;
	private String unidadeMedidaProduto = null;
	private String mensagemOuErro = null;
	private Date data = null;
	private ArrayList<DadosPlanilha> listaDados = new ArrayList<>();
	private static boolean validaLogin = false;

	private static WebDriver driver = null;

	public VarredorWeb() {
//		System.setProperty("webdriver.gecko.driver", "C:\\Projeto Robo Brenfeer\\bin\\webdriver\\geckodriver.exe");
//		FirefoxOptions options = new FirefoxOptions();
//		options.setHeadless(true);
//		 Adicione opções do Firefox, se necessário

		System.setProperty("webdriver.chrome.driver", "C:\\Projeto Robo Brenfeer\\bin\\webdriver\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		// options.addArguments("--headless=new");

		try {
			driver = new ChromeDriver(options);
//			driver = new FirefoxDriver(options);
		} catch (Exception e) {
			System.out.println("Erro ao criar o WebDriver:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Verifique a versao do WEBDRIVER com o navegador GOOGLE CHROME."
					+ "\n1- Se a versao for diferente, e preciso baixar o webdriver atual no site"
					+ "\nhttps://chromedriver.chromium.org/downloads.\n"
					+ "\n2- Apos baixar e extrair o arquivo, salvar no diretorio\n C:\\Projeto Robo Brenfeer\\bin\\webdriver\\"
					+ "\n\n3- O nome do arquivo deve ser chromedriver.exe\n"
					+ "\n verifique essas etapas e tente novamente");
		}
		driver.manage().window().maximize();
	}

	public void capturarDados(String urlSite, String usuario, String senha, String idProduto, String skuInternoOvd) {
		if (idProduto != null && !idProduto.isEmpty()) {
			String urlProduto = urlSite + idProduto;

			try {

				driver.get(urlProduto);
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

				// erro do site https
				By alertaHttps = By.xpath("//*[@id=\"details-button\"]");
				if (driver.findElements(alertaHttps).size() > 0) {
					WebElement alertaElemento = wait
							.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"details-button\"]")));
					if (alertaElemento.isDisplayed()) {
						alertaElemento.click();
					}
					WebElement linkLiberacao = wait
							.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"proceed-link\"]")));
					if (linkLiberacao.isDisplayed()) {
						linkLiberacao.click();
					}
				}

				By formularioCookiesButton = By.cssSelector("*[data-test='COOKIE-POPUP-CLOSE-BTN']");
				if (driver.findElements(formularioCookiesButton).size() > 0) {
					WebElement element = wait.until(ExpectedConditions.elementToBeClickable(formularioCookiesButton));
					element.click();
				}

				// Verificar a presença do botão de detalhe do produto
				By botaoDetalheProdutoXpath = By.xpath(
						"/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]/section/div[3]/button[2]");
				System.out.println("verificou o botan de login presente : " + validaLogin);
				if (driver.findElements(botaoDetalheProdutoXpath).size() > 0) {

					// Localizar o elemento para abrir o formulário de login
					WebElement loginLink = (WebElement) ((JavascriptExecutor) driver).executeScript(
							"return document.querySelector('.header-login .login-btn p.login-btn__hello');");

					// Verificar se o link de login está visível
					if (loginLink.isDisplayed()) {
						// Clicar no link de login para abrir a tela de login
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginLink);

						// Encontrar os elementos de entrada de usuário e senha
						WebElement cnpjInput = driver.findElement(By.cssSelector("input[formcontrolname='usuario']"));
						WebElement senhaInput = driver.findElement(By.cssSelector("input[formcontrolname='senha']"));

						// Preencher usuário e senha
						cnpjInput.sendKeys(usuario);
						senhaInput.sendKeys(senha);

						// Clicar no botão de login
						WebElement loginButton = driver
								.findElement(By.xpath("//*[@id=\"loginModal\"]/app-login/div/div/form/button"));
						if (loginButton.isDisplayed()) {
							loginButton.click();
							System.out.println("Clicou efetuar o login");
							validaLogin = true;
						} else {
							System.out.println("Botão de login não está visível. Verifique a página.");
						}
					}
				} else {
					// O botão não está presente, assumindo que já estamos no login
					System.out.println("Usuário já está logado. Ignorando o processo de login.");
					validaLogin = true;
				}

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// Formulário de boas-vindas
				By welcomeButtonSelector = By.cssSelector(".welcome-modal__bottom-button");
				By checkboxSelector = By.cssSelector(".mat-checkbox-inner-container");
				if (driver.findElements(welcomeButtonSelector).size() > 0) {
					WebElement welcomeCheckbox = wait.until(ExpectedConditions.elementToBeClickable(checkboxSelector));
					welcomeCheckbox.click();
					WebElement welcomeButton = driver.findElement(welcomeButtonSelector);
					welcomeButton.click();
				}

				// validacao se o produto estiver indisponivel
				List<WebElement> elementoIndisponivel = driver.findElements(By.className("product-unavailable__text"));
				if (!elementoIndisponivel.isEmpty()) {
					String valorZero = new String("0,00");
					valorDesconto = valorZero;
					valorImpostos = valorZero;
					valorOriginal = valorZero;
					mensagemOuErro = "produto indisponivel";
					quantidadeDisponivelFormatada = new String("0");
					unidadeMedidaProduto = new String("vazio");
				} else {

					// capturar codigo e descricao do produto
					WebElement informacaoProduto = null;
					List<WebElement> dadosProduto = driver
							.findElements(By.xpath("/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]"));
					if (!dadosProduto.isEmpty()) {
						descricaoProduto = driver.findElement(By.className("title-product")).getText();
						System.out.println("capturou o titulo : " + descricaoProduto);

						String codigoProduto = driver.findElement(By.className("cod-prod")).getText();
						System.out.println("capturou o codigo : " + codigoProduto);
						codigoFormatado = extrairNumerosComRegex(codigoProduto);

						informacaoProduto = wait.until(ExpectedConditions.presenceOfElementLocated(
								By.xpath("/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]")));
					}

					// inicio das validacoes dos campos precos, temos 2 possibilidades de campo
					String campoPrecoOriginalBefore = capturarTextoComValidacao(informacaoProduto,
							By.className("price-group__before-price"));
					String campoPrecoOriginalUnity = capturarTextoComValidacao(informacaoProduto,
							By.className("price-group__unity-price"));
					System.out.println("\ncampo preco original before : " + campoPrecoOriginalBefore + "\n");
					System.out.println("campo preco original unity : " + campoPrecoOriginalBefore + "\n");

					// Verifica se ambos os campos retornam a mensagem de erro
					boolean erroBefore = "By.className: price-group__before-price".equals(campoPrecoOriginalBefore);
					boolean erroUnity = "By.className: price-group__unity-price".equals(campoPrecoOriginalUnity);

					if (erroBefore && erroUnity) {
						valorOriginal = "0,00";
					} else {
						// Se apenas um dos campos retornar a mensagem de erro, usa o outro
						if (erroBefore) {
							valorOriginal = extrairValorOriginal(campoPrecoOriginalUnity).replace("R$ ", "");
						} else if (erroUnity) {
							valorOriginal = extrairValorOriginal(campoPrecoOriginalBefore).replace("R$ ", "");
						} else {
							// Ambos os campos não retornam a mensagem de erro, possuem dados

							valorOriginal = extrairValorOriginal(campoPrecoOriginalBefore).replace("R$ ", "");

						}
					}

					// valida campo preco com desconto
					String textoPrecoDesconto = capturarTextoComValidacao(informacaoProduto, By.xpath(
							"/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]/section[1]/section/div/section/div/div[1]/div/div/span/span[2]"));
					if (textoPrecoDesconto.equalsIgnoreCase(
							"By.xpath:/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]/section[1]/section/div/section/div/div[1]/div/div/span/span[2]")) {
						valorDesconto = new String("0,00");
					} else {
						valorDesconto = extrairValorDesconto(textoPrecoDesconto).replace("R$ ", "");
						if (valorDesconto.isEmpty()) {
							valorDesconto = new String("0,00");
						}
					}

					// valida campo preco com taxas
					String textoPrecoImpostos = capturarTextoComValidacao(informacaoProduto,
							By.className("price-group__taxes"));
					if (textoPrecoImpostos.equalsIgnoreCase("By.className: price-group__taxes")) {
						valorImpostos = new String("0,00");
					} else {
						valorImpostos = extrairValorIpi(textoPrecoImpostos).replace("R$", "");
					}

					// Imprimir os valores formatados
					System.out.println("Codigo produto formatado : " + codigoFormatado);
					System.out.println("Valor original formatado : " + valorOriginal);
					System.out.println("Valor desconto formatado : " + valorDesconto);
					System.out.println("Valor impostos formatado : " + valorImpostos);

					if (valorOriginal.equalsIgnoreCase("0,00") && valorDesconto.equalsIgnoreCase("0,00")
							&& valorImpostos.equalsIgnoreCase("0,00")) {
						System.out.println("o produto esta com indisponibilidade");
						mensagemOuErro = new String("o produto esta com indisponibilidade");
						campoQtdeDisponivelEstoque = new String("0");
						quantidadeDisponivelFormatada = campoQtdeDisponivelEstoque;
						unidadeMedidaProduto = new String("vazio");

					} else {

						WebElement campoQtdeVisivel = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
								"/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]/section[1]/section/div/div[1]/div/app-quantity-input/div/input")));

						String validaCampoQtde = campoQtdeVisivel.getText();
						if (validaCampoQtde.equalsIgnoreCase(
								"By.xpath: /html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]/section[1]/section/div/div[1]/div/app-quantity-input/div/input")) {
							mensagemOuErro = new String("produto esta indisponinel");
							valorDesconto = new String("0,00");
							valorOriginal = valorDesconto;
							valorImpostos = valorDesconto;
							quantidadeDisponivelFormatada = new String("0");
							unidadeMedidaProduto = new String("vazio");

						} else {

							By campoQtdeProdutoXpath = By.xpath(
									"/html/body/app-root/div/app-detalhe-produto/div/div[1]/div[2]/section[1]/section/div/div[1]/div/app-quantity-input/div/input");
							WebElement campoQtdeProduto = wait
									.until(ExpectedConditions.presenceOfElementLocated(campoQtdeProdutoXpath));
							By botaoAdicionar = By.className("btn_add_div");
							if (campoQtdeProduto.isDisplayed()) {
								campoQtdeProduto.sendKeys("5000000", Keys.ENTER);
								System.out.println("Clicou no campo de quantidade.");

								By overlayLocator = By.cssSelector("div.loading-overlay.ng-star-inserted");
								wait.until(ExpectedConditions.invisibilityOfElementLocated(overlayLocator));

								// Clique no botão de adicionar após a espera pela sobreposição
								WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(botaoAdicionar));
								botao.click();
								System.out.println("Clicou no botão de adicionar.");
								WebElement elemento = null;
								try {
									elemento = wait.until(ExpectedConditions.elementToBeClickable(
											By.xpath("//*[@id=\"mat-dialog-1\"]/app-generic-dialog/div/p[1]")));

									if (elemento.getText().equalsIgnoreCase("Quantidade não permitida")) {
										System.out.println("elemento presente na tela Quantidade não permitida");
										WebElement elementoQtdeMultiploElement = wait
												.until(ExpectedConditions.presenceOfElementLocated(
														By.xpath("//*[@id=\"mat-dialog-1\"]/app-generic-dialog")));
										System.out.println(
												"pegou o texto do multiplo : " + elementoQtdeMultiploElement.getText());
										BigDecimal multiplo = new BigDecimal(
												extrairNumerosComRegex(elementoQtdeMultiploElement.getText()));
										BigDecimal calculo = multiplo.multiply(new BigDecimal(500000));
										System.out.println("Extraiu a quantidade múltipla de: " + multiplo);

										WebElement fechar = driver.findElement(By.xpath(
												"//*[@id=\"mat-dialog-1\"]/app-generic-dialog/div/button/span[1]/mat-icon"));
										fechar.click();

										campoQtdeProduto.clear();
										campoQtdeProduto.sendKeys(calculo.toString(), Keys.ENTER);
										System.out.println("Clicou no campo de quantidade.");

										botao.click();
										System.out.println("Clicou no botão de adicionar.");
										System.out.println("\npassou do elemento e vai pegar a qtde de estoque");
										// Capturar a quantidade disponivel no formulario de limite de estoque
										WebElement campoQtdeFormulario = wait
												.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
														"//*[@id=\"mat-dialog-2\"]/app-partial-stock-dialog/div/div")));
										campoQtdeDisponivelEstoque = campoQtdeFormulario.getText();
										System.out.println(
												"pegou todo o texto da qtde estoque : " + campoQtdeDisponivelEstoque);
										/// html/body/div[2]/div[2]/div/mat-dialog-container/app-partial-stock-dialog/div/div/p/span
										/// html/body/div[2]/div[4]/div/mat-dialog-container/app-partial-stock-dialog/div/div/p/span
										/// html/body/div[2]/div[2]/div/mat-dialog-container/app-partial-stock-dialog/div/div/p/span
										quantidadeDisponivelFormatada = obterParteDaString(
												extrairTextoQtdeEstoque(campoQtdeDisponivelEstoque), "antes");
										unidadeMedidaProduto = obterParteDaString(
												extrairTextoQtdeEstoque(campoQtdeDisponivelEstoque), "depois").replace(".", "");
										System.out.println(
												"Quantidade produto formatado : " + quantidadeDisponivelFormatada);

									}

								} catch (TimeoutException e) {
									System.out.println("pegou qtde estoque dentro do catch (TimeoutException e) {");
									WebElement campoQtdeFormulario = null;
									String[] tiposDeFormulario = { "mat-dialog-0", "mat-dialog-1", "mat-dialog-2" };

									for (String tipoDeFormulario : tiposDeFormulario) {
										String xpath = "//*[@id=\"" + tipoDeFormulario
												+ "\"]/app-partial-stock-dialog/div/div/p/span";
										By by = By.xpath(xpath);

										try {
											campoQtdeFormulario = wait
													.until(ExpectedConditions.presenceOfElementLocated(by));
											System.out.println(
													"Elemento encontrado no tipo de formulário: " + tipoDeFormulario);
											campoQtdeDisponivelEstoque = campoQtdeFormulario.getText();
											System.out.println("pegou o texto do formulario de qtde de estoque : "
													+ campoQtdeDisponivelEstoque);
											quantidadeDisponivelFormatada = obterParteDaString(
													extrairTextoQtdeEstoque(campoQtdeDisponivelEstoque), "antes");
											System.out.println("Quantidade produto formatado REGEX : "
													+ quantidadeDisponivelFormatada);
											unidadeMedidaProduto = obterParteDaString(
													extrairTextoQtdeEstoque(campoQtdeDisponivelEstoque), "depois".replace(".", ""));
											mensagemOuErro = new String("sucesso ao capturar dados");
											break;
										} catch (NoSuchElementException | TimeoutException n) {

										}
									}

								}

							}

						}

					}

				}

				data = new Date();
				SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				DadosPlanilha dadosPlanilha = new DadosPlanilha(codigoFormatado, descricaoProduto, urlProduto,
						valorDesconto, valorOriginal, valorImpostos, quantidadeDisponivelFormatada,
						unidadeMedidaProduto, skuInternoOvd, formatadorData.format(data), mensagemOuErro);
				System.out.println("pegou o sku ovd : " + skuInternoOvd);
				listaDados.clear();
				listaDados.add(dadosPlanilha);


			} catch (ElementClickInterceptedException e) {
				if (valorDesconto == null) {
					valorDesconto = new String("0,00");
				}

				if (valorOriginal == null) {
					valorOriginal = new String("0,00");
				}

				if (valorImpostos == null) {
					valorImpostos = new String("0,00");
				}

				if (quantidadeDisponivelFormatada == null) {
					quantidadeDisponivelFormatada = new String("0,00");
				}

				data = new Date();
				SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				DadosPlanilha dadosPlanilha = new DadosPlanilha(codigoFormatado, descricaoProduto, urlProduto,
						valorDesconto, valorOriginal, valorImpostos, quantidadeDisponivelFormatada,
						unidadeMedidaProduto, skuInternoOvd, formatadorData.format(data),
						"erro ElementClickInterceptedException : " + e.getMessage());
				listaDados.clear();
				listaDados.add(dadosPlanilha);
			} catch (NoSuchElementException e) {
				if (valorDesconto == null) {
					valorDesconto = new String("0,00");
				}

				if (valorOriginal == null) {
					valorOriginal = new String("0,00");
				}

				if (valorImpostos == null) {
					valorImpostos = new String("0,00");
				}

				if (quantidadeDisponivelFormatada == null) {
					quantidadeDisponivelFormatada = new String("0,00");
				}

				data = new Date();
				SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				DadosPlanilha dadosPlanilha = new DadosPlanilha(codigoFormatado, descricaoProduto, urlProduto,
						valorDesconto, valorOriginal, valorImpostos, quantidadeDisponivelFormatada,
						unidadeMedidaProduto, skuInternoOvd, formatadorData.format(data),
						"erro NoSuchElementException : " + e.getMessage());
				listaDados.clear();
				listaDados.add(dadosPlanilha);
			} catch (NullPointerException e) {
				if (valorDesconto == null) {
					valorDesconto = new String("0,00");
				}

				if (valorOriginal == null) {
					valorOriginal = new String("0,00");
				}

				if (valorImpostos == null) {
					valorImpostos = new String("0,00");
				}

				if (quantidadeDisponivelFormatada == null) {
					quantidadeDisponivelFormatada = new String("0,00");
				}

				data = new Date();
				SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				DadosPlanilha dadosPlanilha = new DadosPlanilha(codigoFormatado, descricaoProduto, urlProduto,
						valorDesconto, valorOriginal, valorImpostos, quantidadeDisponivelFormatada,
						unidadeMedidaProduto, skuInternoOvd, formatadorData.format(data),
						"erro NullPointerException : " + e.getMessage());
				listaDados.clear();
				listaDados.add(dadosPlanilha);
			}

		} else {
			System.out.println("codigo do produto invalido");

		}

	}

	public ArrayList<DadosPlanilha> getListaDados() {
		return listaDados;
	}

	// validacao extrair numeros dos campos de valor
	private String extrairValorOriginal(String input) {
		String valorSemCifrao = input.replaceAll("[^0-9.,]", "").trim();

		// Verifica se o input contém "Peça" e "TotalcomImpostos"
		if (input.contains("Peça")) {
			int posicaoPeça = input.indexOf("Peça");
			if (posicaoPeça >= 0) {
				return input.substring(0, posicaoPeça).trim();
			}
		} else if (input.contains("TotalcomImpostos")) {
			int posicaoTotalcomImpostos = input.indexOf("TotalcomImpostos");
			if (posicaoTotalcomImpostos >= 0) {
				return input.substring(0, posicaoTotalcomImpostos).trim();
			}
		}

		return valorSemCifrao;
	}

	private String extrairValorDesconto(String input) {
		String valorSemCifrao = input.replaceAll("[^0-9.,]", "").trim();

		// Verifica se o input contem texto "Peca"
		if (input.contains("Peça")) {
			int posicaoPeça = input.indexOf("Peça");
			if (posicaoPeça >= 0) {
				return input.substring(0, posicaoPeça).trim();
			}
		}

		return valorSemCifrao;
	}

	private String extrairValorIpi(String input) {
		String valorSemCifrao = input.replaceAll("[^0-9.,]", "").trim();

		// Verifica se o input contem texto "TotalcomImpostos"
		if (input.contains("TotalcomImpostos")) {
			int posicaoTotalcomImpostos = input.indexOf("TotalcomImpostos");
			if (posicaoTotalcomImpostos >= 0) {
				return input.substring(posicaoTotalcomImpostos + "TotalcomImpostos".length()).trim();
			}
		}

		return valorSemCifrao;
	}

	// validacao de NoSuchElementException
	private String capturarTextoComValidacao(WebElement parentElement, By locator) {
		try {
			WebElement elemento = parentElement.findElement(locator);
			return elemento.getText();
		} catch (org.openqa.selenium.NoSuchElementException e) {
			System.out.println("validou o texto de captura : " + locator);
			return new String(locator.toString());
		}
	}

	// validacao para extrair apenas codigo do produto
	private String extrairNumerosComRegex(String texto) {

		Pattern padrao = Pattern.compile("\\d+");
		Matcher regras = padrao.matcher(texto);

		StringBuilder numeros = new StringBuilder();
		while (regras.find()) {
			numeros.append(regras.group());
		}

		return numeros.toString();
	}

	private String extrairTextoQtdeEstoque(String campoQtde) {
		// Define a regex para capturar o número e a unidade
		String regex = "([\\d.,]+)\\s+(.+)";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(campoQtde);

		if (matcher.find()) {
			// Extrai a quantidade e a unidade
			String quantidade = matcher.group(1).trim();
			String unidade = matcher.group(2).trim();

			return quantidade + " ; " + unidade;
		}

		return "";
	}

	private String obterParteDaString(String entrada, String posicao) {
		String[] partes = entrada.split(";", 2);

		if (partes.length == 2) {

			if ("antes".equalsIgnoreCase(posicao)) {
				return partes[0].trim();
			} else if ("depois".equalsIgnoreCase(posicao)) {
				return partes[1].trim();
			}
		}

		return "";
	}

	public void fecharDriver() {
		if (driver != null) {
			driver.quit();
		}
	}

	public boolean elementoPresente(WebDriver driver, By by) {
		try {
			WebElement elemento = driver.findElement(by);
			return elemento.isDisplayed();
		} catch (NoSuchElementException | TimeoutException e) {
			return false;
		}
	}

}
