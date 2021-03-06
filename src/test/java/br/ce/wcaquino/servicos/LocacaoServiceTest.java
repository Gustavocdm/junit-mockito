package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.umaLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDeDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	 @InjectMocks
	 @Spy
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciou2...");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finalizou2...");
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(DataUtils.obterData(1, 10, 2021)).when(service).obterData();
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(1, 10, 2021)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(2, 10, 2021)), is(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		Usuario usuario = umUsuario().agora();

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveLancarExcecaoAoAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}

	@Test
	public void deveLancarExcecaoAoTentarAlugar0Filme() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = umUsuario().agora();
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		// acao
		service.alugarFilme(usuario, null);
	}
	
	@Test
	// @Ignore
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {		
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(2, 10, 2021));
		Mockito.doReturn(DataUtils.obterData(2, 10, 2021)).when(service).obterData();
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// when(spc.possuiNegativacao(usuario)).thenReturn(true);
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		// acao
		try {
			service.alugarFilme(usuario, filmes);
		// verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usu??rio Negativado"));
		}
		
		 verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		// cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = umUsuario().comNome("Usuario 3").agora();
		
		List<Locacao> locacoes = Arrays.asList(
				umaLocacao().atrasada().comUsuario(usuario).agora(),
				umaLocacao().comUsuario(usuario2).agora(),
				umaLocacao().atrasada().comUsuario(usuario3).agora(),
				umaLocacao().atrasada().comUsuario(usuario3).agora());
		
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		// acao
		service.notificarAtrasos();
		
		// verificacao
		verify(email, times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email).notificarAtraso(usuario);
		verify(email, atLeastOnce()).notificarAtraso(usuario3);
		verify(email, never()).notificarAtraso(usuario2);
		verifyNoMoreInteractions(email);
		// verifyZeroInteractions(spc);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastr??fica"));
		
		// verificacao
		exception.expect(LocadoraException.class);		
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		// acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		Locacao locacao = umaLocacao().agora();
		
		// acao
		service.prorrogarLocacao(locacao, 3);
		
		// verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDeDias(3));
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		// cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		// acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
		// verificacao
		Assert.assertThat(valor, is(4.0));
	}
}
