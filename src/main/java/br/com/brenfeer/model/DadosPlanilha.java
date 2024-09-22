package br.com.brenfeer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosPlanilha {

	private String idProduto;
	private String descricao;
	private String linkProduto;
	private String precoAtual;
	private String precoAnterior;
	private String precoIpi;
	private String quantidade;
	private String unidadeMedida;
	private String skuInternoOvd;
	private String dataFormatada;
	private String mensagemOuErro;
	
}
