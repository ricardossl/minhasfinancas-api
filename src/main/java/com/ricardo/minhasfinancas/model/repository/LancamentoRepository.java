package com.ricardo.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ricardo.minhasfinancas.model.entity.Lancamento;
import com.ricardo.minhasfinancas.model.enums.StatusLancamento;
import com.ricardo.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	@Query(value = "select sum(l.valor) from Lancamento l join l.usuario u where u.id = :idusuario and l.tipo = :tipo and l.status = :status")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(@Param("idusuario") Long idusuario, @Param("tipo") TipoLancamento tipo, @Param("status")  StatusLancamento status);
}
