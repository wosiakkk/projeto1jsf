package br.com.estudojsf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

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

	/* objeto que será usado para o cadastro (Model)*/
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
		mostrarMsg("Cadastro Realizado com sucesso!"); //método que irá exibir uma mensagem para o usuário
		return "";// jsf exige um retorno, e para ficar na mesma página retornamos uma string
					// vazia
	}

	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();// acessando o contexto do JSF
		FacesMessage message = new FacesMessage(msg);//setando a mesagem do JSF
		context.addMessage(null, message);//adicioanndo a mensagem no contexto do JSF que está sendo executado, o parâmetro null é que a mensagem não está associada a nenhum componente JSF
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
	
	public String limpar() {
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
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);
			return "primeirapagina.jsf";
		}
		
		return "index.jsf";
	}
	
	public String deslogar() {
		//Removendo o usuário  na sessão (variável usuarioLogado usada na verificação do filter)
		//Pegando as informações do jsf em tmepo de execução usando o FacesContext
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		//invalidando a sessão do usuário
		HttpServletRequest session = (HttpServletRequest)context.getCurrentInstance().getExternalContext().getRequest();
		session.getSession().invalidate();
		
		return "index.jsf";
	}
	
	
	/*Méotdo que verifica o perfil do usuário, verificando suas permissões de 
	 * visualização, alterando o atributo dos componentes chamado rendered.*/
	public boolean permiteAcesso(String acesso) {
		/*recupera o usuário salvo na sessão*/
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		//retornando um boolean no comparativo entre o acesso passado por parâmetro com o do objeto recuperado.
		return pessoaUser.getPerfilUser().equals(acesso); 
	}
	
	//método para responder a requisição ajax do componente JSF. O parâmetro delcarado é necessário para o JSF indentificar o método
	public void pesquisaCep(AjaxBehaviorEvent event) {
		//System.out.println("Metodo pesquisa cep chamando CEP: " + pessoa.getCep());
		/*Obtendo o Json do WS do CEP*/
		try {
			URL url = new URL("https://viacep.com.br/ws/"+pessoa.getCep()+"/json/"); //def a url do ws com o CEP do obj
			URLConnection connection = url.openConnection(); //abrindo conexão para a URL definida
			InputStream is = connection.getInputStream(); // executando e pegando os dados do retorno
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));//Lendo os dados, lembrando que é importante definir o encode para evitar erros de acentuação
			
			/*Jogando o retorno para dentro de uma String, utilizando um loop que lerá cada linha do retorno até o fim (!=null)
			 * Essa operação será add a um String Builder para montar a string corretamente com os dados lidos*/
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			/*com os atributos do objeto pessoa estão com o mesmo nome dos atributos do json de retorno do ws, o java consegue associar e jogar os dados
			 * do json vindo do ws para dentro do objto java gson. Esse objeto é um auxiliar, e os dados deles deve ser repassados (sets) para o objeto pessoa que é
			 * referente a tela jsf atual, para os dados irem para a tela*/
			Pessoa gson = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			pessoa.setLogradouro(gson.getLogradouro());
			pessoa.setComplemento(gson.getComplemento());
			pessoa.setBairro(gson.getBairro());
			pessoa.setLocalidade(gson.getLocalidade());
			pessoa.setUf(gson.getUf());
			pessoa.setUnidade(gson.getUnidade());
			pessoa.setIbge(gson.getIbge());
			pessoa.setGia(gson.getGia());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar CEP");
		}
	}
	
}
