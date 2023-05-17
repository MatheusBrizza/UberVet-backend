package com.ubervet.backend.service;

import com.ubervet.backend.dto.UsuarioRequestDTO;
import com.ubervet.backend.model.Veterinario;
import com.ubervet.backend.model.VeterinarioComparator;
import com.ubervet.backend.repository.VeterinarioRepository;
import com.ubervet.backend.service.exception.ListaVaziaException;
import com.ubervet.backend.service.exception.VeterinarioExistenteException;
import com.ubervet.backend.service.exception.VeterinarioNaoExistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    public Veterinario criarVeterinario(Veterinario veterinario) throws VeterinarioExistenteException {
        validarVeterinarioExistentePorId(veterinario.getId());
        veterinario = Veterinario.builder()
                .id(criarId())
                .nome(veterinario.getNome())
                .registro(veterinario.getRegistro())
                .especializacao(veterinario.getEspecializacao())
                .endereco(veterinario.getEndereco())
                .telefone(veterinario.getTelefone())
                .email(veterinario.getEmail())
                .senha(veterinario.getSenha())
                .build();
        validarVeterinarioExistentePorEmail(veterinario.getEmail());
        return veterinarioRepository.save(veterinario);
    }


    public List<Veterinario> listarTodosVeterinarios() throws ListaVaziaException {
        validarListaVazia();
        return veterinarioRepository.findAll();
    }


    public void deletarVeterinarioPorEmail(String email) throws VeterinarioNaoExistenteException {
        validarVeterinarioNaoExistentePorEmail(email);
        veterinarioRepository.deleteByEmail(email);
    }

    public Veterinario atualizarVeterinario(String id, Veterinario veterinarioNovo) {
        Optional<Veterinario> veterinarioAntigo = veterinarioRepository.findById(id);
        veterinarioNovo.setId(veterinarioAntigo.get().getId());
        return veterinarioRepository.save(veterinarioNovo);
    }

    private void validarVeterinarioExistentePorId(String id) throws VeterinarioExistenteException {
        Optional<Veterinario> veterinarioExists = veterinarioRepository.findById(id);

        if (veterinarioExists.isPresent()) {
            throw new VeterinarioExistenteException(
                    String.format("Usuario com id %s já existe.", id)
            );
        }
    }
    private void validarVeterinarioExistentePorEmail(String email) throws VeterinarioExistenteException {
        Veterinario veterinarioExists = veterinarioRepository.findByEmail(email);
        if (!(veterinarioExists == null)) {
            throw new VeterinarioExistenteException(
                    String.format("Já existe um veterinário com email %s", email)
            );
        }
    }

    private void validarVeterinarioNaoExistentePorEmail(String email)
            throws VeterinarioNaoExistenteException {
        Veterinario veterinario = veterinarioRepository.findByEmail(email);

        if (veterinario == null) {
            throw new VeterinarioNaoExistenteException(
                    String.format("Não existe um veterinário com email %s, é preciso criar antes de " +
                            "completar esta ação.", email)
            );
        }
    }

    private void validarListaVazia() throws ListaVaziaException {
        List<Veterinario> listaExiste = veterinarioRepository.findAll();
        if (listaExiste.isEmpty()) {
            throw new ListaVaziaException(
                    String.format("Lista vazia")
            );
        }
    }

    private String criarId() {
        List<Veterinario> listaVeterinarios = veterinarioRepository.findAll();
        if (listaVeterinarios.isEmpty()) {
            return "1";
        }
        Collections.sort(listaVeterinarios, new VeterinarioComparator());
        Integer lastId = listaVeterinarios.size() + 1;
        return lastId.toString();
    }

}
