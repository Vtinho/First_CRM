package com.vitorM.CRM.service;

import com.vitorM.CRM.repository.OportunidadeRepository;
import com.vitorM.CRM.dto.OportunidadeDTO;
import com.vitorM.CRM.dto.UsuarioDTO;
import com.vitorM.CRM.exception.RecursoJaExisteException;
import com.vitorM.CRM.exception.RecursoNaoEncontradoException;
import com.vitorM.CRM.model.Oportunidade;
import com.vitorM.CRM.model.Papel;
import com.vitorM.CRM.model.Usuario;
import com.vitorM.CRM.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final OportunidadeRepository oportunidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
        return converterParaDTO(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com email: " + email));
        return converterParaDTO(usuario);
    }

    @Transactional
    public UsuarioDTO salvar(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RecursoJaExisteException("Email já cadastrado: " + usuarioDTO.getEmail());
        }

        Usuario usuario = converterParaEntidade(usuarioDTO);
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        if (usuario.getPapeis() == null || usuario.getPapeis().isEmpty()) {
            Set<Papel> papeis = new HashSet<>();
            papeis.add(Papel.VENDEDOR);
            usuario.setPapeis(papeis);
        }

        usuario = usuarioRepository.save(usuario);
        return converterParaDTO(usuario);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        // Verifica se o novo email já existe para outro usuário
        if (!usuarioExistente.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RecursoJaExisteException("Email já cadastrado: " + usuarioDTO.getEmail());
        }

        usuarioExistente.setNome(usuarioDTO.getNome());
        usuarioExistente.setEmail(usuarioDTO.getEmail());

        // Atualiza a senha apenas se for fornecida
        if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        }

        if (usuarioDTO.getPapeis() != null && !usuarioDTO.getPapeis().isEmpty()) {
            usuarioExistente.setPapeis(usuarioDTO.getPapeis());
        }

        usuarioExistente.setAtivo(usuarioDTO.isAtivo());

        usuarioExistente = usuarioRepository.save(usuarioExistente);
        return converterParaDTO(usuarioExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO converterParaDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        // Não incluímos a senha no DTO por segurança
        dto.setPapeis(usuario.getPapeis());
        dto.setAtivo(usuario.isAtivo());
        return dto;
    }

    private Usuario converterParaEntidade(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        // A senha será codificada no método de salvar
        usuario.setSenha(dto.getSenha());
        usuario.setPapeis(dto.getPapeis());
        usuario.setAtivo(dto.isAtivo());
        return usuario;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public List<OportunidadeDTO> listarOportunidadesPorUsuario(Long usuarioId) {
        List<Oportunidade> oportunidades = oportunidadeRepository.findByUsuarioId(usuarioId);

        return oportunidades.stream()
                .map(this::converterOportunidadeParaDTO)
                .collect(Collectors.toList());
    }

    private OportunidadeDTO converterOportunidadeParaDTO(Oportunidade oportunidade) {
        OportunidadeDTO dto = new OportunidadeDTO();

        dto.setId(oportunidade.getId());
        dto.setTitulo(oportunidade.getTitulo());
        dto.setDescricao(oportunidade.getDescricao());
        dto.setValor(oportunidade.getValor());
        dto.setStatus(oportunidade.getStatus());
        dto.setDataFechamentoPrevista(oportunidade.getDataFechamentoPrevista());
        dto.setDataFechamentoReal(oportunidade.getDataFechamentoReal());

        if (oportunidade.getCliente() != null) {
            dto.setClienteId(oportunidade.getCliente().getId());
            dto.setNomeCliente(oportunidade.getCliente().getNome());
        }

        if (oportunidade.getUsuario() != null) {
            dto.setUsuarioId(oportunidade.getUsuario().getId());
            dto.setNomeUsuario(oportunidade.getUsuario().getNome());
        }
        return dto;
    }
}
