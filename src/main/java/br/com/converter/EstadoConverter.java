package br.com.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.jpautil.JpaUtil;

/*O conversor é necessário, pois ao salvar(enviar o formulário) o JSF pega os dados dos formulários
 * e tenta injetar em um objeto de uma classe, o que não seria possível com os comboboxes.
 * O uq eo ceversor faz? Quando os dados vem do servidor para a tela, fica tudo em String,então só é reconhecido o ID,
 * agora quando vai salvar o formulário, is dados da tela tentam ser convertidos para um objeto, esse é o objetivo do conversor, transformar os daods em um objeto que possa ser
 * salvo no BD*/

//para o jsf indentificar e executar os métodos do conversor automaticamnte é necessário essa anotação. O value é a refernecia para a tela JSF
//também é necessário fazer a implementação converter do pacote faces e também p serializable
@FacesConverter(forClass = Estados.class, value = "estadoConverter")
public class EstadoConverter implements Converter, Serializable {

	private static final long serialVersionUID = 1L;

	// retorna objeto inteiro
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String codigoEstado) {

		/*
		 * Como da tela só virá o cod do estado em string, será necessário uma consulta
		 * no BD para poder retorna um objeto estado inteiro
		 */
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		Estados estado = (Estados) entityManager.find(Estados.class, Long.parseLong(codigoEstado));
		
		return estado;
	}

	// retorna apenas o código em string
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object estado) {
		/*
		 * necessário a conversão de cast para o objeto ser um objeto do tipo estado
		 * para poder ser acessados seus atributos, no caso o id, e transforma-lo me
		 * string para ser retornado
		 */
		if(estado ==null) {
			return null;
		}
		//caso venha um objeto do tipo estado
		if(estado instanceof Estados) {
			return ((Estados) estado).getId().toString();
		}else {
			//caso venha o id pelo parametro do objeto
			return estado.toString(); //converte pra string, apesar de ser estado não é o objeto todo é somente o id
		}
		
		
	}

}
