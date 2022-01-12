package com.ricardo.minhasfinancas.rest;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ricardo.minhasfinancas.exception.ErroAutenticacao;
import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Usuario;
import com.ricardo.minhasfinancas.service.LancamentoService;
import com.ricardo.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControler {
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private LancamentoService lancamentoService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping
	public ResponseEntity salvar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException salvarUsuarioException) {
			return ResponseEntity.badRequest().body(salvarUsuarioException.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioAutenticado = usuarioService.autenticar(usuario.getEmail(), usuario.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao autenticarUsuarioException) {
			return ResponseEntity.badRequest().body(autenticarUsuarioException.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = usuarioService.findById(id);
		if (!usuario.isPresent())
			return new ResponseEntity(HttpStatus.NOT_FOUND);

		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
}
