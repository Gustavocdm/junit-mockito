package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException  {		
		if (usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}
		
		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
//		if (filmes.get(0).getEstoque() < filmes.size()) {
//			throw new FilmeSemEstoqueException();
//		}
		
		if (filmes.stream().map(Filme::getEstoque).map(estoque -> estoque == 0).reduce(false, (a, b) -> a || b)) {
			throw new FilmeSemEstoqueException();
		}
		
		if (spcService.possuiNegativacao(usuario)) {
			throw new LocadoraException("Usuário Negativado");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
//		locacao.setValor(filmes.getPrecoLocacao());
		locacao.setValor(aplicarDescontos(filmes));
		
		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}
	
	public void setLocacaoDAO(LocacaoDAO dao) {
		this.dao = dao;
	}
	
	public void setSPCService(SPCService spc) {
		spcService = spc;
	}
	
	private Double aplicarDescontos(List <Filme> filmesAlugados) {
		Double descontosAplicados = 0.0;
		int index = 1;
		for (Filme filme : filmesAlugados) {
			switch (index) {
				case 3: descontosAplicados += filme.getPrecoLocacao() * 0.75d; break;
				case 4: descontosAplicados += filme.getPrecoLocacao() / 2; break;
				case 5: descontosAplicados += filme.getPrecoLocacao() * 0.25d; break;
				case 6: break;
				default: descontosAplicados += filme.getPrecoLocacao(); break;
			}			
			index++;
		}
		return descontosAplicados;
	}
}