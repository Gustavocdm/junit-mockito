package br.ce.wcaquino.builders;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;

import java.util.Arrays;
import java.util.Date;

import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {

	private Locacao locacao;
	
	private LocacaoBuilder() { }
	
	public static LocacaoBuilder umaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		builder.locacao = new Locacao();
		builder.locacao.setDataLocacao(new Date());
		builder.locacao.setDataRetorno(DataUtils.adicionarDias(builder.locacao.getDataLocacao(), 1));
		builder.locacao.setUsuario(umUsuario().agora());
		builder.locacao.setFilmes(Arrays.asList(umFilme().agora()));
		builder.locacao.setValor(4.0);
		
		return builder;
	}
	
	public LocacaoBuilder comDataRetorno(Date dataRetorno) {
		locacao.setDataRetorno(dataRetorno);
		return this;
	}
	
	public LocacaoBuilder comUsuario(Usuario usuario) {
		locacao.setUsuario(usuario);
		return this;
	}	
	
	public Locacao agora() {
		return locacao;
	}
	
}
