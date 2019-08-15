package br.com.jpautil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped  //anotação do pacote enterprise, pois é configuração do CDI o aplication socpe é um instância apenas para TODO o projeto
public class JpaUtil {
	
/* SINGLETON CONNECTION CONFIGURAÇÃO:
	
	/*
	 * Toda vez que o sistema subir, deve ficar disponível a entityManager para toda
	 * a aplicação. Somente uma instância de JPA/Hibernate deve ser criada para ser
	 * compartilhada por toda a aplicação
	 *

	private static EntityManagerFactory factory = null;

	/*
	 * toda vez que for feito uma referência para essa classe o método estático será
	 * chamado, e se não existir um entityManager, ele será criado. Com o if é
	 * garantido que este método será exectado somente uma vez, não criando outras
	 * instancias do entityManager
	 *

	static {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("projeto1jsf");
		}
	}

	/*
	 * Método publico que poderá ser acessado, de forma estática por toda a
	 * plicação, e que retornará o entityManager
	 *
	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
	
	/*método para retorna r PK de um determinado objeto.
	public static Object getPrimaryKey(Object entity) {
		return factory.getPersistenceUnitUtil().getIdentifier(entity); //método JPA que retorna a PK da entidade
	}
	*/
	
	/*CONFIGURAÇÃO COM CDI:*/

	private EntityManagerFactory factory = null;
	
	public JpaUtil() {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("projeto1jsf");
		}
	}
	
	@Produces  //o produto gerado, para não ser necessário ficar chamdno o método
	@RequestScoped //escopo de requisição, toda vez que for salvar, editar,etc ele irá criar um entitymanager, ele automatiaza a chamada, para não ser necessário criar sempre antes das operações
	public EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
	public Object getPrimaryKey(Object entity) {
		return factory.getPersistenceUnitUtil().getIdentifier(entity); //método JPA que retorna a PK da entidade
	}
	
}
