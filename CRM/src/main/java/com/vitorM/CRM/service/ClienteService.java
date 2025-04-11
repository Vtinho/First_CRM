package com.vitorM.CRM.service;

import com.vitorM.CRM.dto.ClienteDTO;
import com.vitorM.CRM.exception.RecursoNaoEncontradoException;
import com.vitorM.CRM.model.Cliente;
import com.vitorM.CRM.repository.ClienteRepository;
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
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listarPaginado(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> buscar(String termo, Pageable pageable) {
        return clienteRepository.buscarClientes(termo, pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + id));
        return converterParaDTO(cliente);
    }

    @Transactional
    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        Cliente cliente = converterParaEntidade(clienteDTO);
        cliente.setDataCriacao(LocalDateTime.now());
        cliente.setUltimaAtualizacao(LocalDateTime.now());
        cliente = clienteRepository.save(cliente);
        return converterParaDTO(cliente);
    }

    @Transactional
    public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + id));

        clienteExistente.setNome(clienteDTO.getNome());
        clienteExistente.setEmail(clienteDTO.getEmail());
        clienteExistente.setTelefone(clienteDTO.getTelefone());
        clienteExistente.setEmpresa(clienteDTO.getEmpresa());
        clienteExistente.setStatus(clienteDTO.getStatus());
        clienteExistente.setUltimaAtualizacao(LocalDateTime.now());

        clienteExistente = clienteRepository.save(clienteExistente);
        return converterParaDTO(clienteExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteDTO converterParaDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmpresa(cliente.getEmpresa());
        dto.setStatus(cliente.getStatus());
        return dto;
    }

    private Cliente converterParaEntidade(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmpresa(dto.getEmpresa());
        cliente.setStatus(dto.getStatus());
        return cliente;
    }
}
