package com.ricardo.minhasfinancas.service;

import java.util.Optional;

import com.ricardo.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	Usuario autenticar(String email, String senha);

	Usuario salvarUsuario(Usuario usuario);

	void validarEmail(String email);

	Optional<Usuario> findById(Long id);
}
