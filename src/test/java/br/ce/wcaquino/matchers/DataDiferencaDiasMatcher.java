package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer diasAdicionais;
	
	public DataDiferencaDiasMatcher(Integer diasAdicionais) {
		this.diasAdicionais = diasAdicionais;
	}
	
	@Override
	public void describeTo(Description description) {
		Date dataEsperada = obterDataComDiferencaDias(diasAdicionais);
		DateFormat format = new SimpleDateFormat("dd/MM/YYYY");
		description.appendText(format.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return isMesmaData(data, obterDataComDiferencaDias(diasAdicionais));
	}

}
