package br.com.estudojsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.dao.DaoGeneric;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoLancamento;
//@ViewScoped COMO FOI IMLEMNTADO CDI,A ANOTAÇÃO EQUIVALENTE É DE MESMO NOME, FICAR ATENDO AO PACOTE DE IMPORTAÇÃO(VIEW)!
//@ManagedBean(name = "lancamentoBean") COMO FOI IMLEMNTADO CDI,A ANOTAÇÃO EQUIVALENTE AO MANAGEDBEAN É O NAMED
@javax.faces.view.ViewScoped
@Named(value = "lancamentoBean")
public class LancamentoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	//instanciando a classe modelo
	private Lancamento lancamento = new Lancamento();
	//instanciando o dao ganerico passando como tipo o model Lacamento
	//private DaoGeneric<Lancamento> daoGeneric = new DaoGeneric<Lancamento>(); NÃO ISTANCIA ESSE OBJETO MAIS OU PERDERÁ A INJEÇÃO DE DEPENDENCIA IMPLEMENTADA
	@Inject
	private DaoGeneric<Lancamento> daoGeneric;
	//uma lsita para cadastro de lançamentos
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	//private IDaoLancamento daoLancamento = new IDaoLancamentoImpl();  NÃO ISTANCIA ESSE OBJETO MAIS OU PERDERÁ A INJEÇÃO DE DEPENDENCIA IMPLEMENTADA
	@Inject
	private IDaoLancamento daoLancamento;
	
	
	public String salvar() {
		//recueprando o usuário que está logado
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		//seta o usuario logado no obj de lancamento
		lancamento.setUsuario(pessoaUser);
		//salvando o lancamento
		daoGeneric.salvar(lancamento);
		//carregar os lançamentos do usuário logado após salvar um novo registro
		carregarLancamentos();
		return "";
	}
	
	@PostConstruct
	private void carregarLancamentos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		lancamentos = daoLancamento.consultar(pessoaUser.getId());
		
	}

	public String novo() {
		lancamento = new Lancamento();
		return "";
	}
	
	public String remove(){
		daoGeneric.deletePorId(lancamento);
		lancamento = new Lancamento();
		carregarLancamentos();
		return "";
	}
	
	public Lancamento getLancamento() {
		return lancamento;
	}
	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}
	public DaoGeneric<Lancamento> getDaoGeneric() {
		return daoGeneric;
	}
	public void setDaoGeneric(DaoGeneric<Lancamento> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}
	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}
	
	
	
}
