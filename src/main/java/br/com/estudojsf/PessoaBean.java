package br.com.estudojsf;

import javax.faces.bean.ManagedBean;
//back-end
//Anotação necessária para uma classe se tornar ManagedBean, o atributo nome é por qual ela será invocada.
@ManagedBean(name="pessoaBean")
public class PessoaBean {

	private String nome;
	private String sobrenome;
	private String nomeCompleto;
	
	
	//get e o setters são necessários, pois o frameworks vai indentifica-los automaticamente para operar o bean
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSobrenome() {
		return sobrenome;
	}
	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}
	public String getNomeCompleto() {
		return nomeCompleto;
	}
	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	//métodos
	
	public void mostrarNome() {
		nomeCompleto = nome +" "+ sobrenome;
	}
}
