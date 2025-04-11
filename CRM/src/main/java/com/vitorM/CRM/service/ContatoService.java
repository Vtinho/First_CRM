package com.vitorM.CRM.service;

import com.vitorM.CRM.dto.ContatoDTO;
import com.vitorM.CRM.exception.RecursoNaoEncontradoException;
import com.vitorM.CRM.model.Cliente;
import com.vitorM.CRM.model.Contato;
import com.vitorM.CRM.model.Usuario;
import com.vitorM.CRM.repository.ClienteRepository;
import com.vitorM.CRM.repository.ContatoRepository;
import com.vitorM.CRM.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ContatoDTO> listarTodos() {
        return contatoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ContatoDTO> listarPaginado(Pageable pageable) {
        return contatoRepository.findAll(pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public ContatoDTO buscarPorId(Long id) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato não encontrado com ID: " + id));
        return converterParaDTO(contato);
    }

    @Transactional(readOnly = true)
    public Page<ContatoDTO> buscarPorCliente(Long clienteId, Pageable pageable) {
        return contatoRepository.findByClienteId(clienteId, pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public List<ContatoDTO> buscarPorUsuario(Long usuarioId) {
        return contatoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContatoDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return contatoRepository.findByDataContatoBetween(inicio, fim).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContatoDTO salvar(ContatoDTO contatoDTO) {
        Contato contato = converterParaEntidade(contatoDTO);
        contato = contatoRepository.save(contato);
        return converterParaDTO(contato);
    }

    @Transactional
    public ContatoDTO atualizar(Long id, ContatoDTO contatoDTO) {
        Contato contatoExistente = contatoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato não encontrado com ID: " + id));

        Cliente cliente = clienteRepository.findById(contatoDTO.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + contatoDTO.getClienteId()));

        Usuario usuario = null;
        if (contatoDTO.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(contatoDTO.getUsuarioId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + contatoDTO.getUsuarioId()));
        }

        contatoExistente.setDescricao(contatoDTO.getDescricao());
        contatoExistente.setTipo(contatoDTO.getTipo());
        contatoExistente.setDataContato(contatoDTO.getDataContato() != null ? contatoDTO.getDataContato() : LocalDateTime.now());
        contatoExistente.setCliente(cliente);
        contatoExistente.setUsuario(usuario);

        contatoExistente = contatoRepository.save(contatoExistente);
        return converterParaDTO(contatoExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!contatoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Contato não encontrado com ID: " + id);
        }
        contatoRepository.deleteById(id);
    }

    private ContatoDTO converterParaDTO(Contato contato) {
        ContatoDTO dto = new ContatoDTO();
        dto.setId(contato.getId());
        dto.setDescricao(contato.getDescricao());
        dto.setTipo(contato.getTipo());
        dto.setDataContato(contato.getDataContato());

        if (contato.getCliente() != null) {
            dto.setClienteId(contato.getCliente().getId());
            dto.setNomeCliente(contato.getCliente().getNome());
        }

        if (contato.getUsuario() != null) {
            dto.setUsuarioId(contato.getUsuario().getId());
            dto.setNomeUsuario(contato.getUsuario().getNome());
        }

        return dto;
    }

    private Contato converterParaEntidade(ContatoDTO dto) {
        Contato contato = new Contato();
        contato.setId(dto.getId());
        contato.setDescricao(dto.getDescricao());
        contato.setTipo(dto.getTipo());
        contato.setDataContato(dto.getDataContato() != null ? dto.getDataContato() : LocalDateTime.now());

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + dto.getClienteId()));
            contato.setCliente(cliente);
        }

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + dto.getUsuarioId()));
            contato.setUsuario(usuario);
        }

        return contato;
    }
}
