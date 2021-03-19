package com.goods2go.repositories;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.goods2go.models.User;
import com.goods2go.models.enums.Role;

@Transactional
public interface UserDao extends CrudRepository<User, Long> {

  //public User findByEmail(String email);
  
  public User findOneByEmail(String email);
  
  public Optional<User> findOneByEmailAndPassword(String email, String password);

  public Optional<User> findOneById(Long id);
   
  public Iterable<User> findByRoleAndIdentconfirmed(Role role, Boolean isConfirmed);
  
  public Iterable<User> findByRole(Role role);
  
  public Iterable<User> findByRoleIn(List<Role> role);

  public Iterable<User> findByDelivererstatuspending(Boolean delivererstatuspending);

  
} // class UserDao
