package br.com.estudojsf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JpaUtil;
import br.com.repository.IDaoPessoa;

/*Anotação necessária para uma classe se tornar ManagedBean, o atributo nome é por qual ela será invocada.
/*Este managed bean irá controla Pessoa
 * */
//@ViewScoped COMO FOI IMLEMNTADO CDI,A ANOTAÇÃO EQUIVALENTE É DE MESMO NOME, FICAR ATENDO AO PACOTE DE IMPORTAÇÃO(VIEW)!
//@ManagedBean(name = "pessoaBean") COMO FOI IMLEMNTADO CDI,A ANOTAÇÃO EQUIVALENTE AO MANAGEDBEAN É O NAMED
@javax.faces.view.ViewScoped
@Named(value = "pessoaBean")
public class PessoaBean implements Serializable{
	private static final long serialVersionUID = 1L;

	//CDI
	@Inject
	private JpaUtil jpaUtil;
	
	/* objeto que será usado para o cadastro (Model) */
	private Pessoa pessoa = new Pessoa();
	/* instancia do dao genérico */
	//private DaoGeneric<Pessoa> dao = new DaoGeneric<Pessoa>(); COMO O DAO ESTÁ USNADO CDI NÃO PODEMOS INSTANCIAR, PPOIS IRÁ PERDERS TODAS AS INJEÇÕES
	@Inject
	private DaoGeneric<Pessoa> dao;
	/* lista de pessoas que será carregada para a tela */
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	/*
	 * Criando um objeto da interface implementada para usar o método sobescrito de
	 * buscar pessoa
	 */
	//private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl(); COMO O DAO ESTÁ USNADO CDI NÃO PODEMOS INSTANCIAR, PPOIS IRÁ PERDERS TODAS AS INJEÇÕES
	@Inject
	private IDaoPessoa iDaoPessoa;
	// lista de secteItem para preencher o combo de Estados
	List<SelectItem> estados;
	// Listas de cidades
	List<SelectItem> cidades;
	
	//o Partpega o arquivo selecionado na tela e cria temporariamente no lado do servidor para depois ser possível processa-lo
	private Part arquivofoto;

	/* método para salvar e ser chamado da tela JSF */
	public String salvar() throws IOException{
		
		/*Inicio Processar Imagem*/
			//pegando a imagem em byte
			byte[] imagemByte = getByte(arquivofoto.getInputStream());
			//setando no objeto pessoa, salvando a imagem original
			pessoa.setFotoIconBase64Original(imagemByte);
			//transoformar em bufferimage
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
			//pegando o tipo da imagem
			//se o tipo for igual a 0, o que não tem anda dentro do buffer, pega um valor padrão, senão pega o tipo da imagem mesmo
			int type = bufferedImage.getType() == 0 ? bufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
			//miniatura
			int largura =200; //em pixels
			int altura = 200;
			//criar a miniatura
			BufferedImage resizedImage = new BufferedImage(largura,altura,type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(bufferedImage, 0, 0, altura, largura,null);
			g.dispose();
			//escrever novamente a imagem em tamanho menor
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//o content type retorna a string 'image/png' por exemplo. Por isso é preciso quebrar em um array para pegar somente o valor da extensao
			String extensao = arquivofoto.getContentType().split("\\/")[1];
			ImageIO.write(resizedImage, extensao, baos);
			//cabeçalho padrão para iamgem 'data:image/png;base64,'
			String miniImagem = "data:"+ arquivofoto.getContentType()+";base64,"+ DatatypeConverter.printBase64Binary(baos.toByteArray()) ;
			//setando miniatura
			pessoa.setFotoIconBase64(miniImagem);
			//setando a extensao
			pessoa.setExtensao(extensao);
		/*Fim Processar Imagem*/
		
		
		// dao.salvar(pessoa); primeiro método feito com persist, que foi alterado para
		// merge
		pessoa = dao.merge(pessoa); /*
									 * como o étodo merge retorna uma entidade, atribuindo ao mesmo objeto pessoa, o
									 * jsf preencherá a tela com os dados de pessoas salvos/atualizados.
									 */
		carregarPessoas();// recarregando os objetos após atulização no BD
		mostrarMsg("Cadastro Realizado com sucesso!"); // método que irá exibir uma mensagem para o usuário
		return "";// jsf exige um retorno, e para ficar na mesma página retornamos uma string
					// vazia
	}

	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();// acessando o contexto do JSF
		FacesMessage message = new FacesMessage(msg);// setando a mesagem do JSF
		context.addMessage(null, message);// adicioanndo a mensagem no contexto do JSF que está sendo executado, o
											// parâmetro null é que a mensagem não está associada a nenhum componente
											// JSF
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

	public String logar() {

		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());

		if (pessoaUser != null) {// achou a pessoa no BD
			// adicionando o usuário encontrado na sessão (variável usuarioLogado usada na
			// verificação do filter)
			// Pegando as informações do jsf em tmepo de execução usando o FacesContext
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);
			return "primeirapagina.jsf";
		}

		return "index.jsf";
	}

	public String deslogar() {
		// Removendo o usuário na sessão (variável usuarioLogado usada na verificação do
		// filter)
		// Pegando as informações do jsf em tmepo de execução usando o FacesContext
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		// invalidando a sessão do usuário
		HttpServletRequest session = (HttpServletRequest) context.getCurrentInstance().getExternalContext()
				.getRequest();
		session.getSession().invalidate();

		return "index.jsf";
	}

	/*
	 * Méotdo que verifica o perfil do usuário, verificando suas permissões de
	 * visualização, alterando o atributo dos componentes chamado rendered.
	 */
	public boolean permiteAcesso(String acesso) {
		/* recupera o usuário salvo na sessão */
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		// retornando um boolean no comparativo entre o acesso passado por parâmetro com
		// o do objeto recuperado.
		return pessoaUser.getPerfilUser().equals(acesso);
	}

	// método para responder a requisição ajax do componente JSF. O parâmetro
	// delcarado é necessário para chamar através do listener
	public void pesquisaCep(AjaxBehaviorEvent event) {
		// System.out.println("Metodo pesquisa cep chamando CEP: " + pessoa.getCep());
		/* Obtendo o Json do WS do CEP */
		try {
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/"); // def a url do ws com o CEP do
																							// obj
			URLConnection connection = url.openConnection(); // abrindo conexão para a URL definida
			InputStream is = connection.getInputStream(); // executando e pegando os dados do retorno
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));// Lendo os dados, lembrando que
																						// é importante definir o encode
																						// para evitar erros de
																						// acentuação

			/*
			 * Jogando o retorno para dentro de uma String, utilizando um loop que lerá cada
			 * linha do retorno até o fim (!=null) Essa operação será add a um String
			 * Builder para montar a string corretamente com os dados lidos
			 */
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			/*
			 * com os atributos do objeto pessoa estão com o mesmo nome dos atributos do
			 * json de retorno do ws, o java consegue associar e jogar os dados do json
			 * vindo do ws para dentro do objto java gson. Esse objeto é um auxiliar, e os
			 * dados deles deve ser repassados (sets) para o objeto pessoa que é referente a
			 * tela jsf atual, para os dados irem para a tela
			 */
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

	// apontando ao jsf a variavél estados, ele ira buscar o método e retornar a
	// lista para a tela
	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}

	public List<SelectItem> getCidades() {
		return cidades;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	
	public Part getArquivofoto() {
		return arquivofoto;
	}
	public void setArquivofoto(Part arquivofoto) {
		this.arquivofoto = arquivofoto;
	}

	public void carregarCidades(AjaxBehaviorEvent event) {
		/*
		 * //pegando o valor do combo atrave´s do objeto event. O parametro que vem com
		 * o valor do combo selecinado é o submittedValue String codigoEstado = (String)
		 * event.getComponent().getAttributes().get("submittedValue"); //se o código for
		 * diferente de null (primeiro valo do combo escrito '--[Selecione]--')
		 */

		// o objeto do selected one menu vem no source do objeto event, que antes de ser
		// pego precisa ser convertido em HtmlSelectedOneMenu para posteriormente fazer
		// o casting para estado
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

		if (estado != null) {
			pessoa.setEstados(estado);

			List<Cidades> cidades = jpaUtil.getEntityManager()
					.createQuery("from Cidades as c where c.estados.id = " + estado.getId()).getResultList();

			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
			for (Cidades cidadesLista : cidades) {
				selectItemsCidade.add(new SelectItem(cidadesLista, cidadesLista.getNome()));
			}
			setCidades(selectItemsCidade);

		}
	}

	public String editar() {

		if (pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			List<Cidades> cidades = jpaUtil.getEntityManager()
					.createQuery("from Cidades as c where c.estados.id = " + estado.getId()).getResultList();

			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
			for (Cidades cidadesLista : cidades) {
				selectItemsCidade.add(new SelectItem(cidadesLista, cidadesLista.getNome()));
			}
			setCidades(selectItemsCidade);
		}
		return "";
	}
	
	/*Método que transforma um inputstream em array de bytes*/
	private  byte[] getByte(InputStream is) throws IOException{
		
		//variavél de controle
		int len;
		//tamanho padrão para arquivos
		int size = 1024;
		//fluxo de bytes
		byte[] buf = null;
		//caso o inputstream seja um instancia de bytearray, deve fazer outro procedimento
		if(is instanceof ByteArrayInputStream) {
			//verificando o tamanho do array
			size = is.available();
			//criando um buffer do tamanho
			buf = new byte[size];
			//a variavel de controle é o conteudo do buffer lido de zero até o tamanho dele
			len = is.read(buf,0,size);
		}else {
			//é uma saida de media em forma de bytes
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			//varrendo o iputstream e escrevendo no array, e ao mesmo que tempo que le, add na variavel de controle
			while((len = is.read(buf,0,size)) != -1) {
				bos.write(buf,0,len);
			}
			
			buf = bos.toByteArray();
		}
		return buf;
	}
	
	public void download() throws IOException{
		//pegando o paramatro passado pelo commandlink
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String fileDownloadId = params.get("fileDownloadId");
		
		Pessoa pessoa = dao.consultar(Pessoa.class, fileDownloadId);
		
		//dando resposta para o navegador
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		//setar o cabeçalho, tipo tamanho
		response.addHeader("Content-Disposition", "attachment; filename=download."+pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLength(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void registrarLog() {
		/*Rotina de gravação de log (não aconselhavel dessa forma, apenas didática para listeners)*/
	}
	public void mudancaDeValor(ValueChangeEvent event) {
		/*Esse evento é capturao ao executar um alterção nos dados no backend e não na view, neste caso só será caputrado após editar uma pessoa e salvar na tela*/
		System.out.println("Valor antigo: "+ event.getOldValue()); //pega o valor antes da mudança
		System.out.println("Valor Novo: "+ event.getNewValue());//pega o valor após a mudança
	}
}
