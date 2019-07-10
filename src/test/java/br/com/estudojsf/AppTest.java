package br.com.estudojsf;

import javax.persistence.Persistence;
import javax.persistence.Temporal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
   
	
	@org.junit.Test
	public void testeJPA() {
		Persistence.createEntityManagerFactory("projeto1jsf");
	}
	
	
	
}
