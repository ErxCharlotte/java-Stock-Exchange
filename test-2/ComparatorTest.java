import java.util.*;

public class ComparatorTest {
    public static void main(String[] args) {

        Employee e1 = new Employee("001B", 36, 3000, 9922001);
        Employee e2 = new Employee("001A", 35.0, 2000, 5924001);
        Employee e3 = new Employee("101A", 35.0, 4000, 3924401);
        Employee e4 = new Employee("000C", 312, 4000, 3924401);
        Employee e5 = new Employee("000D", 333333, 4000, 3924401);
        Employee e6 = new Employee("0100", 11111, 4000, 3924401);
        Employee e7 = new Employee("0006", 0, 4000, 3924401);

        List<Employee> employees = new ArrayList<>();
        employees.add(e1);
        employees.add(e2);
        employees.add(e3);
        employees.add(e4);
        employees.add(e5);
        employees.add(e6);
        employees.add(e7);

        
      
        //employees.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

        //Collections.sort(employees, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        //Collections.sort(employees, Comparator.comparing(Employee::getAge).thenComparing(Employee::getName));
        employees.forEach(System.out::println);

        e1.name = "PPPP";
        System.out.println("=================================");
        employees.forEach(System.out::println);
        //employees.sort(Comparator.comparing(e -> e.getName()));

        //employees.sort(Comparator.comparing(Employee::getName));

    }
}



class Employee {
    String name;
    double age;
    double salary;
    long mobile;



    public Employee(String name, double age, double salary, long mobile) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", salary=").append(salary);
        sb.append(", mobile=").append(mobile);
        sb.append('}');
        return sb.toString();
    }
}