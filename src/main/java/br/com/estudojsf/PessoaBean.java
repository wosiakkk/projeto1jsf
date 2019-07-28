package br.com.estudojsf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.dao.DaoGeneric;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;

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
	/* lista de pessoas que será carregada para a tela */
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	/*Criando um objeto da interface implementada para usar o método sobescrito de buscar pessoa*/
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	/* método para salvar e ser chamado da tela JSF */
	public String salvar() {
		// dao.salvar(pessoa); primeiro método feito com persist, que foi alterado para
		// merge
		pessoa = dao.merge(pessoa); /*
									 * como o étodo merge retorna uma entidade, atribuindo ao mesmo objeto pessoa, o
									 * jsf preencherá a tela com os dados de pessoas salvos/atualizados.
									 */
		carregarPessoas();// recarregando os objetos após atulização no BD

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
		pessoa = new Pessoa(); // para limpar os dados da tela do obj excluido
		carregarPessoas();// recarregando os objetos após atulização no BD
		return "";
	}

	/* set e get de pessoa */
	public Pessoa getPessoa() {
		return pessoa;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/*
	 * Com a anotação Posconstruct, sempre que a tela for aberta e o Managed Bean
	 * for instanciado, após ele ser cirado em memória, ele vai carregar o método
	 * anotado
	 */
	@PostConstruct
	public void carregarPessoas() {
		/*
		 * carregando a lista genérica passando como objetos a serem retornados da
		 * classe Pessoa por parâmetro. Esse método sempre vai ser chamado quando houver
		 * alteração no BD, por isso ele é add nos métodos de salvar e delete desse
		 * managedBean
		 */
		pessoas = dao.getListEntity(Pessoa.class);
	}
	
	
	public String logar(){
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(pessoaUser != null) {//achou a pessoa no BD
			//adicionando o usuário encontrado na sessão (variável usuarioLogado usada na verificação do filter)
			//Pegando as informações do jsf em tmepo de execução usando o FacesContext
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser.getLogin());
			return "primeirapagina.jsf";
		}
		
		return "index.jsf";
	}
}
