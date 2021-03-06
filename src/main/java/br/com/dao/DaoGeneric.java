package br.com.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.jpautil.JpaUtil;

@Named //toda classe que usar injeção de dependencia deve ser anotada com named do pacote javax.inject
public class DaoGeneric<E> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	
	@Inject //anotação de injeção de dependencia do entitymanager
	private EntityManager entityManager;
	@Inject
	private JpaUtil jpaUtil;
	
	/*
	 * DAO genérico para ser utilizado com JPA e poder manter qualquer tipo de
	 * objeto(entidades)
	 */

	public void salvar(E entidade) {
		// estabelecendo um entityManager
		//EntityManager entityManager = JpaUtil.getEntityManager(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		/*
		 * estabelecendo um entity transaction, que é necessário para realizar
		 * transações no BD
		 */
		EntityTransaction transacao = entityManager.getTransaction();
		/* iniciando a transação */
		transacao.begin();
		/*
		 * neste momento o entityManager está preparado para salvar o objeto que foi
		 * passado como parametro. O método persist apenas salva o objeto no BD
		 */
		entityManager.merge(entidade);
		/* realizando o commit da operação de salvar */
		transacao.commit();
		/* fechando o entityManager para essa operação */
		//entityManager.close(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO, O FRMAEWORK IRÁ CONTROLAR ISSO
	}

	/*
	 * O método mege salva, atualiza e ainda retorna o objeto que foi salvo no banco
	 * de dados Como o merge retorna uma entidade, o método do dao deve ter um
	 * retorno do tipo E (entidade)
	 */
	public E merge(E entidade) {
		//EntityManager entityManager = JpaUtil.getEntityManager(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		EntityTransaction transacao = entityManager.getTransaction();

		transacao.begin();
		E retorno = entityManager.merge(entidade);
		transacao.commit();

		//entityManager.close(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		return retorno;
	}

	/* método pode dar erro de detached, usaro delete por id */
	public void delete(E entidade) {
		//EntityManager entityManager = JpaUtil.getEntityManager(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();
		entityManager.remove(entidade);
		transacao.commit();
		//entityManager.close(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
	}

	/*
	 * Método criado na classe jpautil, para descobrir qual é a PK do objeto e depos
	 * efetuar o delete
	 */
	public void deletePorId(E entidade) {
		//EntityManager entityManager = JpaUtil.getEntityManager(); NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();

		// consultando o id da entidade
		//Object id = JpaUtil.getPrimaryKey(entidade); NÃO CHAMA MAIS ESTATICO, APÓS CDI IMPLEMENTADO
		Object id = jpaUtil.getPrimaryKey(entidade);
		/*
		 * criando a query de delete, lembrando que como o objeto entidade é genérico é
		 * necessário pegar o tipo de classe dele para o sql funcionar(o nome ficar de
		 * acordo com a tabela do banco, o nome tem que ser simples ou canonico)
		 */
		entityManager.createQuery("delete from " + entidade.getClass().getSimpleName() + " where id = " + id)
				.executeUpdate();

		transacao.commit();
		entityManager.close();
	}

	/*
	 * Um data table é amarrado a uma lista de objetos. O método abaixo gera a
	 * lista(genérica). E como parâmetro uma classe genérica será passada.
	 */
	public List<E> getListEntity(Class<E> entidade) {
		//EntityManager entityManager = JpaUtil.getEntityManager();  NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();

		/*
		 * sql que ira buscar uma lista. Como o método está genérico será utlizado o
		 * nome canonical da classe que será por padrão igual a tabela criada pelo JPA
		 * no BD
		 */
		List<E> retorno = entityManager.createQuery("from " + entidade.getName()).getResultList();

		return retorno;
	}
	
	public E consultar(Class<E> entidade, String codigo) {
		//EntityManager entityManager = JpaUtil.getEntityManager();  NÃO NECESSÁRIO APÓS  O CDI IMPLEMENTADO
		EntityTransaction transacao = entityManager.getTransaction();
		transacao.begin();
		E objeto = (E)entityManager.find(entidade, Long.parseLong(codigo));
		transacao.commit();
		return objeto;

	}

}
