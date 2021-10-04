package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {
	
	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DataDiferencaDiasMatcher ehHoje() {
		return ehHojeComDiferencaDeDias(0);
	}
	
	public static DataDiferencaDiasMatcher ehHojeComDiferencaDeDias(Integer dias) {
		return new DataDiferencaDiasMatcher(dias);
	}
}
