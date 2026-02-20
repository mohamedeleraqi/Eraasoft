public class Main {

    public static void main(String[] args) {
        
        Student student = new Student();
        student.setId(1);
        student.setAge(30);
        student.setName("Mohamed");

        student.print();
        
        System.out.println(student.getId() +"\n"+ student.getAge() +"\n"+ student.getName());
    }
}
