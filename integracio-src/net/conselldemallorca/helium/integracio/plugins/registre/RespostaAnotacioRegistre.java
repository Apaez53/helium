/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.Date;

/**
 * Resposta a una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaAnotacioRegistre extends RespostaBase {

	private String numero;
	private Date data;
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

}
