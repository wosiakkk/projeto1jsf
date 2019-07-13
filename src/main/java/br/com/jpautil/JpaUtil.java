package br.com.jpautil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil {

	/*
	 * Toda vez que o sistema subir, deve ficar disponível a entityManager para toda
	 * a aplicação. Somente uma instância de JPA/Hibernate deve ser criada para ser
	 * compartilhada por toda a aplicação
	 */

	private static EntityManagerFactory factory = null;

	/*
	 * toda vez que for feito uma referência para essa classe o método estático será
	 * chamado, e se não existir um entityManager, ele será criado. Com o if é
	 * garantido que este método será exectado somente uma vez, não criando outras
	 * instancias do entityManager
	 */

	static {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("projeto1jsf");
		}
	}

	/*
	 * Método publico que poderá ser acessado, de forma estática por toda a
	 * plicação, e que retornará o entityManager
	 */
	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
	
	/*método para retorna r PK de um determinado objeto.*/
	public static Object getPrimaryKey(Object entity) {
		return factory.getPersistenceUnitUtil().getIdentifier(entity); //método JPA que retorna a PK da entidade
	}
}
