package com.tauru.shop.entities;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Address> addressList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Roles roles;

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        User user = (User) obj;

        return  user.username.equals(username) &&
                user.password.equals(password) &&
                user.email.equals(email) &&
                user.firstName.equals(firstName) &&
                user.lastName.equals(lastName);
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 11 * result + username.hashCode();
        result = 11 * result + password.hashCode();
        result = 11 * result + email.hashCode();
        result = 11 * result + firstName.hashCode();
        result = 11 * result + lastName.hashCode();

        return result;
    }

    public static class UserSortingComparator implements Comparator<User> {

        @Override
        public int compare(User firstUser, User secondUser) {

            int usernameComparator = firstUser.getUsername().compareTo(secondUser.getUsername());
            int emailComparator = firstUser.getEmail().compareTo(secondUser.getEmail());

            if (usernameComparator == 0) {
                return ((emailComparator == 0) ? usernameComparator : emailComparator);
            } else {
                return emailComparator;
            }
        }
    }
}
