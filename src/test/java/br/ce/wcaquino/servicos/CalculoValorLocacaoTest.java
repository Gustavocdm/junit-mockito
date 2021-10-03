package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	private LocacaoService service;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}
	
	private static Filme filme1 = new Filme("Filme 1", 2, 5.0);
	private static Filme filme2 = new Filme("Filme 2", 4, 5.0);
	private static Filme filme3 = new Filme("Filme 3", 5, 10.0);
	private static Filme filme4 = new Filme("Filme 4", 5, 10.0);
	private static Filme filme5 = new Filme("Filme 5", 5, 10.0);
	private static Filme filme6 = new Filme("Filme 6", 5, 10.0);
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2), 10.0, "2 Filmes: Sem Desconto"},
			{Arrays.asList(filme1, filme2, filme3), 17.5, "3º Filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 22.5, "4º Filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 25.0, "5º Filmes: 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 25.0, "6º Filmes: 100%"},
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		assertThat(locacao.getValor(), is(valorLocacao));
	}
}
