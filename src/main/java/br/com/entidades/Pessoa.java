package br.com.entidades;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.constraints.br.TituloEleitoral;

@Entity  
public class Pessoa implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String nome;
	
	/*A variável sobrenome vai ser validada no lado do servidor utilizando o hibernate-validator e suas anotações.
	 * Com isso a camada de view não precisa ter validações, como tem no atributo nome da tela. Os pacotes das anotações são
	 * javax.validation e org.hibernate.validator*/
	@NotEmpty(message = "Sobrenome deve ser informado.(bean validator)") //anotação que não autoriza campo ser vazio
	@NotNull(message = "Sobrenome deve ser informado.(bean validator)") //anotação que não autoriza o campo ser nulo.
	@Size(min=10,max=10, message = "O sobrenome deve ter entre 10 e 50 caracteres")//anotação que valida o tamanho do dado
	private String sobrenome;
	@DecimalMin(value = "10", message = "A idade deve ser maior que 10")//validanto tamanho decimais
	@DecimalMax(value = "50", message = "A idade deve ser menor que 10")
	private int idade;
	@CPF(message = "CPF inválido") //valida CPF brasileiro
	private String cpf;
	@TituloEleitoral(message = "Titulo Inválido") //valida titulo de eleitor brasileiro
	private String tituloEleitoral;
	
	private String sexo;
	private String[] frameworks;
	private Boolean ativo;
	private String login;
	private String senha;
	private String perfilUser;
	private String nivelProgramador;
	private Integer[] linguagens;
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private String unidade;
	private String ibge;
	private String gia;
	
	//anotação necessária para o POSTGRES criar a coluna no fomrato TEXTO que consegue amarzner textos enormes para suportar a base 64
	@Column(columnDefinition = "text")
	private String fotoIconBase64;
	
	private String extensao; //jpg,png, etc
	
	//anotação necessária quando se trabalha com dados grnades como vetores de bytes, como gravar arquivos no bd
	@Lob
	@Basic(fetch = FetchType.LAZY) //define que a foto só sera carregada quando for chamado o atributo para retornar a base 64
	private byte[] fotoIconBase64Original;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	private Cidades cidades;
	
	/*Objeto temporário apenas em memória para receber a lista de estados e preencher
	 * o combo na tela JSF, ele está marcado com @Transient para o hibernate não cria-lo na tabela pessoa do BD
	 * sendo assi  não fincando persistente */
	@Transient
	private Estados estados;
	
	@Temporal(TemporalType.DATE) //essa anotação diferencia se será salvo só a data, data com hora etc.
	private Date dataNascimento = new Date();
	
	public Pessoa() {
		// TODO Auto-generated constructor stub
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public int getIdade() {
		return idade;
	}
	public void setIdade(int idade) {
		this.idade = idade;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public void setFrameworks(String[] frameworks) {
		this.frameworks = frameworks;
	}
	public String[] getFrameworks() {
		return frameworks;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	public Boolean getAtivo() {
		return ativo;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getLogin() {
		return login;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getSenha() {
		return senha;
	} 
	public void setPerfilUser(String perfilUser) {
		this.perfilUser = perfilUser;
	}
	public String getPerfilUser() {
		return perfilUser;
	}
	public void setNivelProgramador(String nivelProgramador) {
		this.nivelProgramador = nivelProgramador;
	}
	public String getNivelProgramador() {
		return nivelProgramador;
	}
	public void setLinguagens(Integer[] linguagens) {
		this.linguagens = linguagens;
	}
	public Integer[] getLinguagens() {
		return linguagens;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getCep() {
		return cep;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getLocalidade() {
		return localidade;
	}
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getIbge() {
		return ibge;
	}
	public void setIbge(String ibge) {
		this.ibge = ibge;
	}
	public String getGia() {
		return gia;
	}
	public void setGia(String gia) {
		this.gia = gia;
	}
	public void setEstados(Estados estados) {
		this.estados = estados;
	}
	public Estados getEstados() {
		return estados;
	}

	public void setCidades(Cidades cidades) {
		this.cidades = cidades;
	}
	public Cidades getCidades() {
		return cidades;
	}
	public String getFotoIconBase64() {
		return fotoIconBase64;
	}

	public void setFotoIconBase64(String fotoIconBase64) {
		this.fotoIconBase64 = fotoIconBase64;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public byte[] getFotoIconBase64Original() {
		return fotoIconBase64Original;
	}

	public void setFotoIconBase64Original(byte[] fotoIconBase64Original) {
		this.fotoIconBase64Original = fotoIconBase64Original;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getCpf() {
		return cpf;
	}
	public void setTituloEleitoral(String tituloEleitoral) {
		this.tituloEleitoral = tituloEleitoral;
	}
	public String getTituloEleitoral() {
		return tituloEleitoral;
	}

	//hashcode e equals ajuda o java e o hibernate a diferenciar objetos
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
