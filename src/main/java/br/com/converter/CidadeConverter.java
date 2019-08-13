package br.com.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Cidades;
import br.com.jpautil.JpaUtil;


//para o jsf indentificar e executar os métodos do conversor automaticamnte é necessário essa anotação
//também é necessário fazer a implementação converter do pacote faces e também p serializable
@FacesConverter(forClass = Cidades.class, value = "cidadeConverter")
public class CidadeConverter implements Converter, Serializable {

	private static final long serialVersionUID = 1L;

	// retorna objeto inteiro
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String codigoCidade) {

		
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		Cidades cidade = (Cidades) entityManager.find(Cidades.class, Long.parseLong(codigoCidade));
		
		return cidade;
	}

	// retorna apenas o código em string
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object cidade) {
		/*
		 * necessário a conversão de cast para o objeto ser um objeto do tipo estado
		 * para poder ser acessados seus atributos, no caso o id, e transforma-lo me
		 * string para ser retornado
		 */
		
		if(cidade == null) {
			return null;
		}
		if(cidade instanceof Cidades) {
			return ((Cidades) cidade).getId().toString();
		}else {
			return cidade.toString();
		}
		
	}

}
