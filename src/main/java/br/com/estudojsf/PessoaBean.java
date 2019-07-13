package br.com.estudojsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.dao.DaoGeneric;
import br.com.entidades.Pessoa;

/*Anotação necessária para uma classe se tornar ManagedBean, o atributo nome é por qual ela será invocada.
/*Este managed bean irá controla Pessoa
 * */
@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	/* objeto que será usado para o cadastro */
	private Pessoa pessoa = new Pessoa();
	/* instancia do dao genérico */
	private DaoGeneric<Pessoa> dao = new DaoGeneric<Pessoa>();

	/* método para salvar e ser chamado da tela JSF */
	public String salvar() {
		// dao.salvar(pessoa); primeiro método feito com persist, que foi alterado para
		// merge
		pessoa = dao.merge(pessoa); /*
									 * como o étodo merge retorna uma entidade, atribuindo ao mesmo objeto pessoa, o
									 * jsf preencherá a tela com os dados de pessoas salvos/atualizados.
									 */

		return "";// jsf exige um retorno, e para ficar na mesma página retornamos uma string
					// vazia
	}

	/*
	 * Como o objeto do merge volta para ser exibido na tela, é necessário um método
	 * que gere um novo objeto para uma nova operação. Faremos um método ocm retorno
	 * de string vazia, pois ao retornar vazio para o jsf ele permanece na mesma
	 * página.
	 */
	public String novo() {
		pessoa = new Pessoa();
		return "";
	}
	
	public String remove() {
		dao.deletePorId(pessoa);
		pessoa = new Pessoa(); //para limpar os dados da tela do obj excluido
		return "";
	}
	/* set e get de pessoa */
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
}
