package br.com.recordstore.socios.dto;
import br.com.recordstore.socios.StatusSocio;
public record SocioDTO(Long id, String nome, String email, StatusSocio status) {}
