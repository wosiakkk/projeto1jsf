package br.com.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.entidades.Pessoa;
import br.com.jpautil.JpaUtil;

/*Anotação necessário para um filtro, nele é definido em um vertor de String quais URLs
 *  ele irá interceptar as chamadas, no caso do /* ele irá atual em todas as urls do sistema*/
@WebFilter(urlPatterns = { "/*" })
public class FilterAutenticacao implements Filter {
	
	//Injeção de dependencia
	@Inject
	private JpaUtil jpaUtil;
	
	/* Métodos sobreescritos da interface Filter */
	/* A classe também precisa implementar a interface Filter do pacote servlet */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Iniciado quando o servidor é iniciado
		// Algo interessante em se fazer neste método é já levantar a conexão com o BD
		//JpaUtil.getEntityManager(); NÃO CHAMA DE FORMA ESTATICA MAIS, POIS FOI INJETADO COM CDI
		jpaUtil.getEntityManager();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		Filter.super.destroy();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// Método executado em todas as requisições, onde será trabalhado toda a
		// autenticação

		// Pegando os dados da requisição
		HttpServletRequest req = (HttpServletRequest) request;
		// Pegando os dados da sessão
		HttpSession session = req.getSession();
		// Carregando dentro da sessão o atributo de usuário logado, no qual é um objeto
		// Pessoa
		Pessoa usuarioLogado = (Pessoa) session.getAttribute("usuarioLogado");
		// Pegando o endereço do sistema, o que está sendo acessado pela requisição
		String url = req.getServletPath();
		// Verificando se está sendo acessado qualquer outra página que não seja a
		// página de Login quando o usuário não está autenticado
		if (!url.equalsIgnoreCase("index.jsf") && usuarioLogado == null) {
			// Como a requisição não tem permissão, o sistema redireciona novamente para o
			// index
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsf");
			dispatcher.forward(request, response);
			// return vazio necessário para o código não executar mais nada e o dispatcher
			// ser efeutado
			return;
		} else {
			// caso o usuário esteja autenticado o sistema executa as requisições
			// normalmente.
			
			// Chamada necessária para o filter funcionar e as ações do request e response
			// serem executadas
			chain.doFilter(request, response);
		}
	}
}
