package br.com.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Pessoa;

@Named
public class IDaoPessoaImpl implements IDaoPessoa, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager entityManager;
	
	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		Pessoa pessoa = null;
		//EntityManager entityManager = JpaUtil.getEntityManager(); não necessário após cdi
		//EntityTransaction transacao = entityManager.getTransaction(); não necessário após cdi
		//transacao.begin(); não necessário após cdi
		
		pessoa = (Pessoa) entityManager.createQuery("select p from Pessoa p where p.login = '"+ login +"' and p.senha = '"+senha+"'").getSingleResult();
		
	//	transacao.commit(); não necessário após cdi
		//entityManager.close(); não necessário após cdi
		return pessoa;
	}

	@Override
	public List<SelectItem> listaEstados() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
	//EntityManager entityManager = JpaUtil.getEntityManager(); não necessário após cdi
		//EntityTransaction transacao = entityManager.getTransaction(); não necessário após cdi
		//transacao.begin(); não necessário após cdi
		List<Estados> estados = entityManager.createQuery("from Estados").getResultList();
		
		for (Estados estados2 : estados) {
			//O objeto será carrregado no combo, porém o que será mostrado para o usuário será só a label
			selectItems.add(new SelectItem(estados2, estados2.getNome()));
		}
		
		return selectItems;
	}

}
