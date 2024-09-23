package br.com.brenfeer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import br.com.brenfeer.model.ArquivoExcel;
import br.com.brenfeer.model.DadosPlanilha;
import br.com.brenfeer.model.LeitorXml;
import br.com.brenfeer.model.VarredorWeb;
import br.com.brenfeer.model.util.CaminhoArquivos;

public class Principal {

	private static LeitorXml leitorXml = null;
	private static String urlSite = null;
	private static String usuario = null;
	private static String senha = null;
	private static VarredorWeb varredorWeb = new VarredorWeb();
	private static ArquivoExcel arquivoExcel = new ArquivoExcel();
	private static CaminhoArquivos caminhoArquivos = new CaminhoArquivos();

	public static void main(String[] args)
			throws IOException, ParserConfigurationException, SAXException, InterruptedException {
		String caminhoXml = caminhoArquivos.getCaminhoArquivos() + "\\bin\\config\\arquivo-acesso.xml";
		carregaDados(caminhoArquivos.ajustarCaminhosArquivos(caminhoXml), true);
		System.out.println("-----INICIANDO O ROBO DE SCRAPING-----");
		System.out.println("\nLeitura do arquivo com os produtos");
		String caminhoPlanilha = caminhoArquivos.getCaminhoArquivos() + "\\planilhas\\produtos.xlsx";
		arquivoExcel.lerArquivo(caminhoArquivos.ajustarCaminhosArquivos(caminhoPlanilha));
		processaScraping(arquivoExcel.listaDadosCapturados());
		System.exit(0);
	}

	private static void carregaDados(String caminho, Boolean carrega)
			throws IOException, ParserConfigurationException, SAXException {
		if (carrega) {
			File abrirArquivo = new File(caminho);
			InputStream arquivo = null;
			try {
				arquivo = new FileInputStream(abrirArquivo);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null,
						"Verifique se o arquivo esta presente no diretorio!\nDiretorio : C:\\Projeto Robo Brenfeer\\bin\\config\\ \nNome do arquivo : arquivo.acesso.xml .");
				return;
			}
			if (arquivo != null) {
				List<String> listaTags = Arrays.asList("urlSite", "usuario", "senha");

				leitorXml = new LeitorXml();

				List<List<String>> lista = null;

				lista = leitorXml.processar(arquivo, listaTags);

				for (List<String> dados : lista) {
					urlSite = dados.get(0);
					usuario = dados.get(1);
					senha = dados.get(2);
				}

				if (urlSite == null || usuario == null || senha == null) {
					JOptionPane.showMessageDialog(null,
							"Verifique a estrutura do arquivo.acesso.xml.\nA estrutura deve ser \n<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
									+ "<acesso>\r\n" + "<urlSite>site-para-acesso</urlSite>\r\n"
									+ "<usuario>usuario-site</usuario>\r\n" + "<senha>senha-site</senha>\r\n"
									+ "</acesso>");
				}

			}

		}
	}

	private static void processaScraping(List<DadosPlanilha> list) throws InterruptedException {
		mensagemConsolePersonalizada("--INICIANDO SCRAPING NO SITE--");
		int qtde = 1 + list.size();
		System.out.println("\nTOTAL DE ITENS DA LISTA : " + qtde);

		for (int i = 0; i < list.size(); i++) {
			DadosPlanilha d = list.get(i);

			System.out.println("\nItem numero : " + (i + 1) + "\n");
			System.out.println("\nIniciou processo de captura de dados ....");
			System.out.println("Codigo produto : " + d.getIdProduto() + "\nSKU pruduto : " + d.getSkuInternoOvd());
			varredorWeb.capturarDados(urlSite, usuario, senha, d.getIdProduto(), d.getSkuInternoOvd());
			System.out.println("\nFinalizou item numero : " + (i + 1) + "\n");
			arquivoExcel.criarArquivo("ovdresultado.xlsx", varredorWeb.getListaDados());
		}
		System.out.println("\n\n");
		mensagemConsolePersonalizada("--FINALIZOU O SCRAPING NO SITE--");
		varredorWeb.fecharDriver();
	}

	public static void mensagemConsolePersonalizada(String message) {
		int length = message.length() + 4;

		System.out.println(" " + "-".repeat(length));
		System.out.println("| " + message + " |");
		System.out.println(" " + "-".repeat(length));
	}

}
