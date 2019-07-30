package br.com.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Lancamento;
import br.com.jpautil.JpaUtil;

public class IDaoLancamentoImpl implements IDaoLancamento{

	@Override
	public List<Lancamento> consultar(Long codUser) {
		
		List<Lancamento> lista = null;
		
		EntityManager entityManager = JpaUtil.getEntityManager();
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();
		
		lista = entityManager.createQuery("from Lancamento where usuario.id ="+codUser).getResultList();
		
		transacao.commit();
		entityManager.close();
		return lista;
	}

}
