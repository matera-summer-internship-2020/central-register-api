package com.matera.cadastrocentral.client;

import com.matera.cadastrocentral.identitydocument.IdentityDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final IdentityDocumentRepository identityDocumentRepository;

    @Autowired
    public ClientService(final ClientRepository clientRepository,
                         final IdentityDocumentRepository identityDocumentRepository) {
        this.clientRepository = clientRepository;
        this.identityDocumentRepository = identityDocumentRepository;
    }

    /* API requests */

    // 1. Get all clients from the database.
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // 2. Get a specific client by id.
    public Optional<Client> getClientById(UUID clientId) {
        Optional<Client> client = clientRepository.findById(clientId);
        if(client.isPresent()){
            return client;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Client ID does not exist in the database! Try a valid one."
            );
        }
    }

    // 3. Insert a client into the database.
    public Client insertClient(final ClientDTO clientDTO) {
        Client client = clientRepository.save(new Client(clientDTO));
        client.getIdentityDocumentEntityList().forEach(
                identityDocumentEntity -> {
                    identityDocumentEntity.setClient(client);
                    identityDocumentRepository.save(identityDocumentEntity);
                }
        );
        return client;
    }

    // 4. Alter a client information in the database.
    public Client alterClient(UUID clientId, ClientDTO clientDTO) {
        try {
            Client alteredClient = clientRepository.getOne(clientId);
            alteredClient.setName(Optional.ofNullable(
                    clientDTO.getName()).orElse(alteredClient.getName()));
            alteredClient.setMaritalStatusEntity(Optional.ofNullable(
                    clientDTO.getMaritalStatusEntity()).orElse(alteredClient.getMaritalStatusEntity()));

            return clientRepository.save(alteredClient);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Client ID does not exist in the database! Try a valid one."
            );
        }
    }

    // 5. Delete a client from the database
    public void deleteClient(final UUID clientId) {
        try {
            clientRepository.deleteById(clientId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Client ID does not exist in the database! Try a valid one."
            );
        }
    }
}
