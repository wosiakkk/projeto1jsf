package br.com.estudojsf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.dao.DaoGeneric;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.repository.IDaoLancamento;
import br.com.repository.IDaoLancamentoImpl;

@ViewScoped
@ManagedBean(name = "lancamentoBean")
public class LancamentoBean {
	//instanciando a classe modelo
	private Lancamento lancamento = new Lancamento();
	//instanciando o dao ganerico passando como tipo o model Lacamento
	private DaoGeneric<Lancamento> daoGeneric = new DaoGeneric<Lancamento>();
	//uma lsita para cadastro de lançamentos
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	private IDaoLancamento daoLancamento = new IDaoLancamentoImpl();
	
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
