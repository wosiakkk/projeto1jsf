package br.com.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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

}
