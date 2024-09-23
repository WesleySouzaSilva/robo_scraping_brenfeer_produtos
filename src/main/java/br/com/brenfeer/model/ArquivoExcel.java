package br.com.brenfeer.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.brenfeer.model.util.CaminhoArquivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

@Slf4j
public class ArquivoExcel {

	private CaminhoArquivos caminhoArquivos = new CaminhoArquivos();

	final int COLUNA_TITULO_PRODUTO = 0;
	final int COLUNA_CODIGO_PRODUTO = 1;
	final int COLUNA_SKU_INTERNO_OVD = 2;

	private List<DadosPlanilha> listaDados = new ArrayList<>();

	public List<DadosPlanilha> lerArquivo(final String nomeArquivo) {
		log.info("Carregando o arquivo de leitura: {}", nomeArquivo);
		List<DadosPlanilha> produtosPlanilha = new ArrayList<>();

		File abrirArquivo = new File(nomeArquivo);
		if (!abrirArquivo.exists()) {
			log.error("Arquivo não encontrado: {}", nomeArquivo);
			JOptionPane.showMessageDialog(null,
					"Arquivo não encontrado: " + nomeArquivo + "\nVerifique o caminho do arquivo.");
			return produtosPlanilha;
		}

		try (InputStream arquivo = new FileInputStream(abrirArquivo)) {
			Workbook workbook = WorkbookFactory.create(arquivo);
			log.info("Workbook carregado: {}", workbook);

			Sheet primeiraAba = workbook.getSheetAt(0);

			int contadorLinha = 0;
			Iterator<Row> iterator = primeiraAba.iterator();

			while (iterator.hasNext()) {
				Row linha = iterator.next();

				// Ignora a primeira linha (cabeçalho)
				if (contadorLinha++ == 0) {
					continue;
				}

				log.info("Processando linha: {}", linha.getRowNum());

				String tituloProduto = obterValorCelula(linha.getCell(COLUNA_TITULO_PRODUTO));
				String codigoProduto = obterValorCelula(linha.getCell(COLUNA_CODIGO_PRODUTO));
				String skuInternoOvd = obterValorCelula(linha.getCell(COLUNA_SKU_INTERNO_OVD));

				// Verifica se todas as células necessárias têm dados
				if (tituloProduto != null || codigoProduto != null || skuInternoOvd != null) {
					DadosPlanilha produto = new DadosPlanilha(codigoProduto, tituloProduto, null, null, null, null,
							null, null, skuInternoOvd, null, null);
					produtosPlanilha.add(produto);
					listaDados.add(produto);
				}
			}

		} catch (IOException e) {
			log.error("Erro ao processar o arquivo: {}", nomeArquivo, e);
			JOptionPane.showMessageDialog(null,
					"Erro ao processar o arquivo, verifique se a estrutura do arquivo está correta!");
		}

		return produtosPlanilha;
	}

	private String obterValorCelula(Cell cell) {
		if (cell == null) {
			return null;
		}

		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getLocalDateTimeCellValue().toString();
			}
			return String.valueOf((long) cell.getNumericCellValue());
		default:
			return null;
		}
	}

	public List<DadosPlanilha> listaDadosCapturados() {
		return listaDados;
	}

	public void criarArquivo(final String nomeArquivo, final List<DadosPlanilha> dados) {
		log.info("Gerando o arquivo {}", nomeArquivo);
		String caminhoCompleto = caminhoArquivos.getCaminhoArquivos() + "\\planilhas\\" + nomeArquivo;

		File arquivo = new File(caminhoArquivos.ajustarCaminhosArquivos(caminhoCompleto));
		XSSFWorkbook workbook = null;

		try {
			// Abrir o workbook existente ou criar um novo se o arquivo não existir
			if (arquivo.exists()) {
				try (FileInputStream fileInputStream = new FileInputStream(arquivo)) {
					workbook = new XSSFWorkbook(fileInputStream);
				}
			} else {
				workbook = new XSSFWorkbook();
			}

			// Trabalhando com a planilha
			XSSFSheet planilha = workbook.getSheet("Plan1");
			if (planilha == null) {
				planilha = workbook.createSheet("Plan1");
				adicionarCabecalho(planilha, 0);
			}

			int numeroDaLinha = planilha.getLastRowNum() + 1;

			for (DadosPlanilha d : dados) {
				Row linha = planilha.createRow(numeroDaLinha++);
				adicionarCelula(linha, 0, d.getDescricao());
				adicionarCelula(linha, 1, d.getSkuInternoOvd());
				adicionarCelula(linha, 2, d.getPrecoAnterior());
				adicionarCelula(linha, 3, d.getPrecoAtual());
				adicionarCelula(linha, 4, d.getPrecoIpi());
				adicionarCelula(linha, 5, d.getQuantidade());
				adicionarCelula(linha, 6, d.getUnidadeMedida());
				adicionarCelula(linha, 7, d.getDataFormatada());
				adicionarCelula(linha, 8, d.getMensagemOuErro());
			}

			// Salvar o workbook no arquivo
			try (FileOutputStream outputStream = new FileOutputStream(caminhoCompleto)) {
				workbook.write(outputStream);
			}

		} catch (IOException e) {
			log.error("Erro ao processar o arquivo: {}", nomeArquivo, e);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					log.error("Erro ao fechar o workbook", e);
				}
			}
		}

		log.info("Arquivo gerado com sucesso!");
	}

	private void adicionarCabecalho(XSSFSheet planilha, int numeroLinha) {
		Row linha = planilha.createRow(numeroLinha);
		adicionarCelula(linha, 0, "Título Produto");
		adicionarCelula(linha, 1, "SKU Interno OVD");
		adicionarCelula(linha, 2, "Preço Normal");
		adicionarCelula(linha, 3, "Preço Com Desconto");
		adicionarCelula(linha, 4, "Preço com Impostos");
		adicionarCelula(linha, 5, "Estoque");
		adicionarCelula(linha, 6, "Unidade Medida Produto");
		adicionarCelula(linha, 7, "Data e Hora da Captura no Site OVD");
		adicionarCelula(linha, 8, "Mensagem ou Erro");
	}

	private void adicionarCelula(Row linha, int coluna, String valor) {
		Cell cell = linha.createCell(coluna);
		cell.setCellValue(valor);
	}

}
