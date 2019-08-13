package br.com.repository;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JpaUtil;

public class IDaoPessoaImpl implements IDaoPessoa {

	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		Pessoa pessoa = null;
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();
		
		pessoa = (Pessoa) entityManager.createQuery("select p from Pessoa p where p.login = '"+ login +"' and p.senha = '"+senha+"'").getSingleResult();
		
		transacao.commit();
		entityManager.close();
		return pessoa;
	}

	@Override
	public List<SelectItem> listaEstados() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();
		List<Estados> estados = entityManager.createQuery("from Estados").getResultList();
		
		for (Estados estados2 : estados) {
			//O objeto será carrregado no combo, porém o que será mostrado para o usuário será só a label
			selectItems.add(new SelectItem(estados2, estados2.getNome()));
		}
		
		return selectItems;
	}

}
