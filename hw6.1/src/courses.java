import tester.*;
import java.util.function.*;

class Course {
  String name;
  Instructor prof;
  IList<Student> students;
  
  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<Student>();
  }
  
  // adds given student to this list of students
  public void enrollStudent(Student s) {
    this.students =  new ConsList<Student>(s, this.students);
  }
  
  // to test same course, we only need to test the same name and professor
  // because no two courses will have the same name and professor
  public boolean equals(Course c) {
    return this.name.equals(c.name) && this.prof.equals(c.prof);
  }
  
  // checks if this course has this student in its list of students
  public boolean hasStudent(Student s) {
    return this.students.contains(s);
  }
  
}

class Instructor {
  String name;
  IList<Course> courses;
  
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }
  
  // tests sameness for instructors
  // only need to check if the names are the same and if the courses are the same because
  // no two instructors will have the same name and teach the same courses
  public boolean equals(Instructor prof) {
    return this.name.equals(prof.name) && this.courses.equals(prof.courses);
  }
  
  // checks if any student of this professor is enrolled in multiple courses taught by this professor
  // uses map to get the number of occurrences of the given student in each of the courses 
  // taught by this professor, then uses foldr to add all those occurrences up
  public boolean dejavu(Student s) {
    return this.courses.map(new numOccurrences(), s).foldr(new Add(), 0) > 1;
  }
  
}

class Student {
  String name;
  int id;
  IList<Course> courses;
  
  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }
  
  // adds the given course to the list of courses of this student
  // also calls enrollStudent to enroll this student in the given course
  public void enroll(Course c) {
    this.courses =  new ConsList<Course>(c, this.courses);
    c.enrollStudent(this);
  }
  
  // checks if this student has any courses in common with the given student
  public boolean classmates(Student s) {
    return this.courses.anyOverlap(s.courses);
  }
  
  // since each student has a unique ID, that is the only thing that needs
  // to be checked to see if one student is the same as another
  public boolean equals(Student s) {
    return this.id == s.id;
  }
  
}

// BiFunction object to add two integers
class Add implements BiFunction<Integer, Integer, Integer> {
  public Integer apply(Integer x, Integer y) {
    return x + y;
  }
}

// BiFunction object to get the number of occurrences of a student in a course
class numOccurrences implements BiFunction<Course, Student, Integer> {
  public Integer apply(Course c, Student s) {
    return c.students.numOccurrencesAcc(s, 0);
  }
}

interface IList<T> {
  // checks if there is any overlap between the given list and another list of the same type
  boolean anyOverlap(IList<T> other);

  // checks if the given element is in the list
  boolean contains(T element);
  
  // accumulator for numOccurrences
  // accumulates the number of occurrences of an object so far in the list
  // starts with 0 at the beginning
  // is updated when the first item in the cons is the same as the desired object
  int numOccurrencesAcc(T thing, int num);

  // folds the list from the right
  // applies the given function to each element in the list
  <U> U foldr(BiFunction<T, U, U> func, U base);

  // maps the given function to each element in the list
  // returns a new list with the results of the function applied to each element
  <U, A> IList<U> map(BiFunction<T, A, U> f, A somethingElse);
}

class MtList<T> implements IList<T>{
  // checks if there is any overlap between this empty list and another list
  // always false
  public boolean anyOverlap(IList<T> other) {
    return false;
  }
  
  // tests if the empty list contains the given element, always false
  public boolean contains(T element) {
    return false;
  }
  
  // accumulator for numOccurences, returns the given number
  public int numOccurrencesAcc(T thing, int num) {
    return num;
  }

  // foldr for an empty list, returns the base
  public <U> U foldr(BiFunction<T, U, U> func, U base) {
    return base;
  }

  // maps the given function to an empty list, returns an empty list of the new type
  public <U, A> IList<U> map(BiFunction<T, A, U> f, A somethingElse) {
    return new MtList<U>();
  }
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;
  
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
  
  // checks if there is any overlap between this list and another list 
  // by checking if the first element of this list is in the other list 
  // and recursively calling to check the rest of this list
  public boolean anyOverlap(IList<T> other) {
    return other.contains(this.first) || this.rest.anyOverlap(other);
  }
  
  // checks if the given element is in this list
  public boolean contains(T element) {
    return this.first.equals(element) || this.rest.contains(element);
  }
  
  // accumulator for numOccurrences
  // accumulates the number of occurrences of an object so far in the list
  public int numOccurrencesAcc(T thing, int num) {
    if (this.first.equals(thing)) {
      return this.rest.numOccurrencesAcc(thing, num + 1);
    }
    else {
      return this.rest.numOccurrencesAcc(thing, num);
    }
  }

  // folds the list from the right
  public <U> U foldr(BiFunction<T, U, U> func, U base) {
    return func.apply(this.first, this.rest.foldr(func, base));
  }

  // maps the given function to each element in the list
  public <U, A> IList<U> map(BiFunction<T, A, U> f, A somethingElse) {
    return new ConsList<U>(f.apply(this.first, somethingElse), this.rest.map(f, somethingElse));
  }
}

// 5 students, 4 courses, 2 instructors
// test methods: testSameCourse, testSameInstructor, testDejavu, testClassmates
class Examples {
  Instructor prof1;
  Instructor prof2;
  Course cs2500;
  Course cs2510;
  Course cs5600;
  Course cs1800;
  Student shresht;
  Student lyanne;
  Student bensen;
  Student tyler;
  Student eoin;
  
  void initData() {
    prof1 = new Instructor("Daniel Patterson");
    prof2 = new Instructor("Ben Lerner");
    cs2500 = new Course("CS2500", prof1);
    cs2510 = new Course("CS2510", prof1);
    cs5600 = new Course("CS5600", prof2);
    cs1800 = new Course("CS1800", prof2);
    shresht = new Student("Shresht Bhowmick", 1);
    lyanne = new Student("Lyanne Xu", 2);
    bensen = new Student("Bensen Wang", 3);
    tyler = new Student("Tyler Dong", 4);
    eoin = new Student("Eoin Collette", 5);
    
    shresht.enroll(cs2500);
    shresht.enroll(cs5600);
    lyanne.enroll(cs2500);
    lyanne.enroll(cs2510);
    bensen.enroll(cs2510);
    bensen.enroll(cs1800);
    tyler.enroll(cs5600);
    tyler.enroll(cs1800);
    eoin.enroll(cs2500);
    eoin.enroll(cs1800);
    }

    boolean testEnroll(Tester t) {
      Student testStudent = new Student("Test Student", 6);
      Course testCourse = new Course("Test Course", prof1);
      testStudent.enroll(testCourse);
      return t.checkExpect(testStudent.courses.contains(testCourse), true) &&
             t.checkExpect(testCourse.students.contains(testStudent), true) &&
             t.checkExpect(prof1.courses.contains(testCourse), true);
    }
    

    boolean testSameCourse(Tester t) {
      initData();
      return t.checkExpect(cs2500.equals(new Course("CS2500", prof1)), true) &&
             t.checkExpect(cs2500.equals(cs2510), false);
    }
    
    boolean testSameInstructor(Tester t) {
      initData();
      return t.checkExpect(prof1.equals(new Instructor("Daniel Patterson")), true) &&
             t.checkExpect(prof1.equals(prof2), false);
    }

    boolean testDeja(Tester t) {
      initData();
      return t.checkExpect(prof1.dejavu(shresht),true) &&
             t.checkExpect(prof2.dejavu(lyanne), false);
    }

    boolean testClassmates(Tester t) {
      initData();
      return t.checkExpect(shresht.classmates(lyanne), true) &&
             t.checkExpect(shresht.classmates(bensen), false);
    }

    boolean testSameStudent(Tester t) {
      initData();
      return t.checkExpect(shresht.equals(new Student("Shresht Bhowmick", 1)), true) &&
             t.checkExpect(shresht.equals(lyanne), false) &&
             t.checkExpect(shresht.equals(shresht), true) &&
             t.checkExpect(shresht.equals(new Student("Shresht Bhowmick", 2)), false) &&
             t.checkExpect(shresht.equals(new Student("Not Shresht", 1)), false);
    }    }

}