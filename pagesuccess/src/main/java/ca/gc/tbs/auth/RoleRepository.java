/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.gc.tbs.auth;


import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author didin
 */
public interface RoleRepository extends MongoRepository<Role, String> {
    
    Role findByRole(String role);
}
