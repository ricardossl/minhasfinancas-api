package com.ricardo.minhasfinancas.rest;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ricardo.minhasfinancas.exception.RegraNegocioException;
import com.ricardo.minhasfinancas.model.entity.Lancamento;
import com.ricardo.minhasfinancas.model.entity.Usuario;
import com.ricardo.minhasfinancas.model.enums.StatusLancamento;
import com.ricardo.minhasfinancas.model.enums.TipoLancamento;
import com.ricardo.minhasfinancas.rest.dto.LancamentoDTO;
import com.ricardo.minhasfinancas.service.LancamentoService;
import com.ricardo.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoControler {
	private final LancamentoService lancamentoService;
	private final UsuarioService usuarioService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamentoSalvo = lancamentoService.salvar(converter(dto));
			return new ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException salvarLancamento) {
			return ResponseEntity.badRequest().body(salvarLancamento.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	@GetMapping
	public ResponseEntity find(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long idUsuario) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		Optional<Usuario> usuario = usuarioService.findById(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Usuário não encontrado para o ID informado.");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		return ResponseEntity.ok(lancamentoService.buscar(lancamentoFiltro));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO) {
		return lancamentoService.findById(id).map(entitiy -> {
			try {
				Lancamento lancamento = converter(lancamentoDTO);
				lancamento.setId(entitiy.getId());
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody String status) {
		return lancamentoService.findById(id).map(lancamento -> {
			try {
				StatusLancamento statusSelecionado = StatusLancamento.valueOf(status);
				if (statusSelecionado == null) {
					return ResponseEntity.badRequest()
							.body("Não foi possível atualizar o status do lançamento, envie um status válido.");
				}
				lancamento.setStatus(statusSelecionado);
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return lancamentoService.findById(id).map(entidade -> {
			lancamentoService.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping("{id}")
	public ResponseEntity obterPorId(@PathVariable("id") Long id) {
		return lancamentoService.findById(id).map(lancamento -> {
			return ResponseEntity.ok(converter(lancamento));
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}

	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder().id(lancamento.getId()).descricao(lancamento.getDescricao())
				.mes(lancamento.getMes()).valor(lancamento.getValor()).status(lancamento.getStatus().name())
				.tipo(lancamento.getTipo().name()).usuario(lancamento.getUsuario().getId()).ano(lancamento.getAno()).build();
	}

	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setUsuario(usuarioService.findById(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado.")));
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		if (dto.getStatus() != null)
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));

		return lancamento;
	}

}
