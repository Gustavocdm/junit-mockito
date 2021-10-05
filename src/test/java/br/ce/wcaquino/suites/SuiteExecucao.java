package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//   @RunWith(Suite.class)
@SuiteClasses({
	CalculadoraTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {
	
	@BeforeClass
	public static void before() {
		// Pode ser utilizado o @BeforeClass em uma suíte de teste 
	}
	
	@AfterClass
	public static void after() {
		// Pode ser utilizado o @AfterClass em uma suíte de teste
	}
}
