package com.ricardo.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ricardo.minhasfinancas.exception.ErroAutenticacao;
import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Usuario;
import com.ricardo.minhasfinancas.model.repository.UsuarioRepository;
import com.ricardo.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	@Autowired
	private UsuarioRepository usuarioRepository;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new ErroAutenticacao("Usuário não encontrado para o email informado"));

		if (!usuario.getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida");
		}

		return usuario;
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return usuarioRepository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = usuarioRepository.existsByEmail(email);
		if (existe)
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse email.");
	}

	@Override
	public Optional<Usuario> findById(Long id) {
		return usuarioRepository.findById(id);
	}

}
