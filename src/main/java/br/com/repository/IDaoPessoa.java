package br.com.repository;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.entidades.Pessoa;

public interface IDaoPessoa {
	
	Pessoa consultarUsuario(String login, String senha);
	//para compenentes combo não retornamos uma lista de estado e sim de obejtos de itens para seleção
	List<SelectItem> listaEstados();
	
}
