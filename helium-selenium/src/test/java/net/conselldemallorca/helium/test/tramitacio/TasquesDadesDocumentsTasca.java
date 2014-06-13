package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;
import net.conselldemallorca.helium.test.util.CheckFileHash;
import net.conselldemallorca.helium.test.util.HashType;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasquesDadesDocumentsTasca extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String pathArxiuPDF1 = carregarPropietat("deploy.arxiu.pdf.tramitacio_1", "Documento PDF a adjuntar 1");
	String hashArxiuPDF1 = carregarPropietat("deploy.arxiu.pdf.tramitacio_1.hash", "Hash documento PDF a adjuntar 1");
	
	@Test
	public void a_iniciar_expedient() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("TasquesDadesTasca/iniciar_expedient/1.png");

		crearTipusExpedientTest(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, properties.getProperty("defproc.tasca_dades_doc.exp.export.arxiu.path"));

		importarDadesTipExp(codTipusExp, properties.getProperty("tipexp.tasca_dades_doc.exp.export.arxiu.path"));
		screenshotHelper.saveScreenshot("TasquesExpedient/iniciar_expedient/2.png");
		
		iniciarExpediente(nomDefProc, codTipusExp, "SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime());

		screenshotHelper.saveScreenshot("TasquesDadesTasca/iniciar_expedient/2.png");
	}

	@Test
	public void b_visualizacio_tasca_dades() throws InterruptedException {
		// Básicos, múltiples y registros
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/1.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
				
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		List<VariableExpedient> listaVariables = new ArrayList<VariableExpedient>();
		
		// Leemos las variables
		int i = 1;
		while(i <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
			VariableExpedient variable = new VariableExpedient();
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]")).click();
			
			String codi = driver.findElement(By.xpath("//*[@id='codi0']")).getAttribute("value");
			String etiqueta = driver.findElement(By.xpath("//*[@id='etiqueta0']")).getAttribute("value");
			String tipo = driver.findElement(By.xpath("//*[@id='tipus0']")).getAttribute("value");
			String observaciones = driver.findElement(By.xpath("//*[@id='observacions0']")).getText();
			boolean multiple = driver.findElement(By.xpath("//*[@id='multiple0']")).isSelected();
			boolean oculta = driver.findElement(By.xpath("//*[@id='ocult0']")).isSelected();
			
			variable.setCodi(codi);
			variable.setEtiqueta(etiqueta);
			variable.setTipo(tipo);
			variable.setObservaciones(observaciones);
			variable.setOculta(oculta);
			variable.setMultiple(multiple);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/2"+i+".png");
			
			driver.findElement(By.xpath("//*[@id='command']/div[4]/button[2]")).click();
			
			if ("REGISTRE".equals(tipo)) {
				WebElement button = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[6]/form/button"));
				button.click();
				int j = 1;
				while(j <= driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size()) {
					VariableExpedient variableReg = new VariableExpedient();
					
					driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]")).click();
					
					String[] var = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText().split("/");
					String codiReg = var[0];
					String etiquetaReg = var[1];
					boolean obligatorioReg = "Si".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText());
					String tipoReg = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[2]")).getText();

					variableReg.setCodi(codiReg);
					variableReg.setEtiqueta(etiquetaReg);
					variableReg.setTipo(tipoReg);
					variableReg.setObligatorio(obligatorioReg);
					variableReg.setMultiple(multiple); // Si lo era la variable padre del registro
					
					variable.getRegistro().add(variableReg);
					
					screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+"-"+j+".png");
					j++;
				}
				
				driver.findElement(By.xpath("//*[@id='command']/div[2]/button[2]")).click();
			}
			
			listaVariables.add(variable);
			
			screenshotHelper.saveScreenshot("tramitar/dadesexpedient/visualizacio_dades_process/3"+i+".png");
			i++;
		}
		
		// Vemos el resto de parámetros de la primera tarea
		driver.findElement(By.xpath("//*[@id='tabnav']/li[2]/a")).click();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[3]/form/button")).click();
				
		Iterator<VariableExpedient> it = listaVariables.iterator();
		while(it.hasNext()) {
			VariableExpedient var = it.next();
			if (existeixElement("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]")) {
				boolean obligatorio = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[4]/input")).isSelected();
				var.setObligatorio(obligatorio);
				
				boolean readonly = driver.findElement(By.xpath("//td[contains(text(),'"+var.getCodi()+"/"+var.getEtiqueta()+"')]/parent::tr/td[5]/input")).isSelected();
				var.setReadOnly(readonly);
			} else {
				it.remove();
			}
		}
		
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuTasques']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipusExpedient0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[5]/button[1]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1')]/a")).click();
				
		// Comprobamos que se muestran los labels, variables, observaciones y botones según el tipo de variable
				
		for (VariableExpedient variable : listaVariables) {
			comprobarVariable(variable, false);			
		}
		
		// Guardamos y validamos
		driver.findElement(By.xpath("//*/button[contains(text(), 'Guardar')]")).click();
		existeixElementAssert("//*[@id='infos']/p", "No se guardó correctamente");
		
		driver.findElement(By.xpath("//*/button[contains(text(), 'Validar')]")).click();
		existeixElementAssert("//*[@id='infos']/p", "No se validó correctamente");
		noExisteixElementAssert("//*/div[contains(@class, 'error')]", "Existían errores de validación");
		
		existeixElementAssert("//*/button[contains(text(), 'Modificar')]", "No existía el botón modificar");
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/5.png");
	}
	
	@Test
	public void z_finalizar_expedient() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		eliminarExpedient(null, null);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
	
	private void comprobarVariable(VariableExpedient variable, boolean esModal) {
//		if (!variable.isOculta()) {
			existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']","La variable no oculta no se ha mostrado : " + variable.getEtiqueta());
			if (variable.isReadOnly()) {
				// Es readonly
				existeixElementAssert("//*[@id='commandReadOnly']/div/div/label[contains(text(), '"+variable.getEtiqueta()+"')]","La variable readonly no se ha mostrado : " + variable.getEtiqueta());
			} else {
				// No es readonly
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']", "La etiqueta no coincidia: " + variable.getEtiqueta());
				
				if (!esModal) {
					if (variable.isObligatorio()) {
						existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/*/em/img", "La variable no estaba como obligatoria : " + variable.getEtiqueta());
					} else {
						noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/*/em/img", "La variable estaba como obligatoria : " + variable.getEtiqueta());
					}
					
					if (!"REGISTRE".equals(variable.getTipo())) {
						if (variable.isMultiple() && !esModal) {
							existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]", "La variable no contenía el botón de múltiple : " + variable.getEtiqueta());
						} else {
							noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]", "La variable contenía el botón de múltiple : " + variable.getEtiqueta());
						}	
					}
					if (variable.getObservaciones() != null && !variable.getObservaciones().isEmpty()) {
						existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']","La variable debe mostrar observaciones : " + variable.getEtiqueta());
						String obs = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']")).getText();
						assertTrue("La observación no coincidía : " + variable.getEtiqueta(), obs.equals(variable.getObservaciones()));
					} else {
						noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']","La variable no debe mostrar observaciones : " + variable.getEtiqueta());
					}
				}
				if ("STRING".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("Texto 1 " + variable.getEtiqueta());

					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("Texto 2 " + variable.getEtiqueta());
					}
				} else if ("INTEGER".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
					
					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
					}
				} else if ("FLOAT".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
					
					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
					}
				} else if ("BOOLEAN".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@type='checkbox']", "No tenía un checkbox: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@type='checkbox']")).click();
					
					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).click();;
					}
				} else if ("TEXTAREA".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea", "No tenía un textarea: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea")).sendKeys("Texto 1 " + variable.getEtiqueta());
					
					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("Texto 2 " + variable.getEtiqueta());
					}
				} else if ("DATE".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']", "No tenía un input: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']")).sendKeys("13/11/2014");
					
					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("14/12/2014");
					}
				} else if ("PRICE".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
					
					if (variable.isMultiple() && !esModal) {
						driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
						driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
					}
				} else if ("TERMINI".equals(variable.getTipo())) {
					boolean cond = driver.findElements(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/select")).size() == 2
							&& driver.findElements(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).size() == 1;
					assertTrue("El termini no era correcto : " + variable.getEtiqueta(), cond);
					WebElement select1 = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li[1]/label/select"));
					List<WebElement> options1 = select1.findElements(By.tagName("option"));
					options1.get(options1.size()-1).click();
					WebElement select2 = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li[2]/label/select"));
					List<WebElement> options2 = select2.findElements(By.tagName("option"));
					options2.get(options2.size()-1).click();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).sendKeys("2");
				} else if ("SELECCIO".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/select", "No tenía un select: " + variable.getEtiqueta());
					WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/select"));
					List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
					options.get(options.size()-1).click();
				} else if ("SUGGEST".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys(usuari);
					driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();
				} else if ("REGISTRE".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button", "No tenía un botón: " + variable.getEtiqueta());
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button")).click();
					
					String url = driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"']")).getAttribute("src");
					url = url.substring(url.indexOf("registre.html"));
					if (modalObertaAssert(url)) {
						vesAModal(url);
						
						for (VariableExpedient variableReg : variable.getRegistro()) {
							comprobarVariable(variableReg, true);
						}
						
						driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
						tornaAPare();
					}
				} else if ("ACCIO".equals(variable.getTipo())) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button", "No tenía un botón: " + variable.getEtiqueta());
				}
			}
//		} else {
//			noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']","La variable oculta se ha mostrado : " + variable.getEtiqueta());
//		}

		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/4-"+variable.getCodi()+".png");
	}
	
	public class DocumentoExpedient {
		private String codi;
		private String nom;
		private boolean esPlantilla;
		private String arxiu;
		private String descripcio;
		private boolean adjuntarAutomaticamente;
		
		public boolean isAdjuntarAutomaticamente() {
			return adjuntarAutomaticamente;
		}
		public void setAdjuntarAutomaticamente(boolean adjuntarAutomaticamente) {
			this.adjuntarAutomaticamente = adjuntarAutomaticamente;
		}
		public String getDescripcio() {
			return descripcio;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
		public String getArxiu() {
			return arxiu;
		}
		public void setArxiu(String arxiu) {
			this.arxiu = arxiu;
		}
		public boolean isEsPlantilla() {
			return esPlantilla;
		}
		public void setEsPlantilla(boolean esPlantilla) {
			this.esPlantilla = esPlantilla;
		}
		public String getCodi() {
			return codi;
		}
		public void setCodi(String codi) {
			this.codi = codi;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
	}
	
	public class VariableExpedient {
		private String codi;
		private String etiqueta;
		private String tipo;
		private String observaciones;
		private boolean oculta;
		private boolean obligatorio;
		private boolean multiple;
		private boolean readOnly;
		
		List<VariableExpedient> registro = new ArrayList<VariableExpedient>();

		public List<VariableExpedient> getRegistro() {
			return registro;
		}

		public void setRegistro(List<VariableExpedient> registro) {
			this.registro = registro;
		}

		public String getEtiqueta() {
			return etiqueta;
		}

		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

		public String getObservaciones() {
			return observaciones;
		}

		public void setObservaciones(String observaciones) {
			this.observaciones = observaciones;
		}

		public boolean isOculta() {
			return oculta;
		}

		public void setOculta(boolean oculta) {
			this.oculta = oculta;
		}

		public boolean isObligatorio() {
			return obligatorio;
		}

		public void setObligatorio(boolean obligatorio) {
			this.obligatorio = obligatorio;
		}

		public String getCodi() {
			return codi;
		}

		public void setCodi(String codi) {
			this.codi = codi;
		}

		public boolean isMultiple() {
			return multiple;
		}

		public void setMultiple(boolean multiple) {
			this.multiple = multiple;
		}

		public boolean isReadOnly() {
			return readOnly;
		}

		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}
	}
}
