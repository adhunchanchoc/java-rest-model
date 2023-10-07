package payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity //id, name, and role are attributes of our Employee domain object.
class Employee {
    private @Id @GeneratedValue Long id;
    private String name;
    private String role;

    Employee(){};

    public Employee(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
    public void setId(Long id){
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(this instanceof Employee)) return false;
        Employee employee= (Employee)obj;
        return Objects.equals(this.id, employee.id) && Objects.equals(this.name, employee.name) && Objects.equals(this.role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,this.name,this.role);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
