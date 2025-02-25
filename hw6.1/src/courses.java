import tester.*;
import java.util.function.*;

// COURSE ----------------------------
// represents a course with a name, professor, and list of students

class Course {
  String name;
  Instructor prof;
  IList<Student> students;
  
  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<Student>();
    // adds this course to the list of courses taught by the given professor
    prof.addCourse(this);
  }
  
  // adds given student to this list of students
  public void enrollStudent(Student s) {
    this.students =  new ConsList<Student>(s, this.students);
  }
  
  // checks if this course has this student in its list of students
  public boolean hasStudent(Student s) {
    return this.students.contains(s, new SameStudent());
  }
  
}

// class to represent a function that checks if two courses are the same
// to test same course, we only need to test the same name and students
// because no two courses will have the same name and students
class SameCourse implements BiFunction<Course, Course, Boolean> {
  public Boolean apply(Course c1, Course c2) {
    return c1.name.equals(c2.name) && c1.students.sameList(c2.students, new SameStudent());
  }
}

// INSTRUCTOR -------------------------
// represents an instructor with a name and list of courses

class Instructor {
  String name;
  IList<Course> courses;
  
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  // adds the given course to this professor's list of courses 
  void addCourse(Course c) {
    this.courses = new ConsList<Course>(c, this.courses);
  }
  
  // checks if any student of this professor is enrolled in multiple courses taught by this 
  // professor
  // uses map to get the number of occurrences of the given student in each of the courses 
  // taught by this professor, then uses foldr to add all those occurrences up
  public boolean dejavu(Student s) {
    return this.courses.map(new NumOccurrences(), s).foldr(new Add(), 0) > 1;
  }
  
}

// class to represent a function that checks if two instructors are the same
// check if the names are the same and if the courses are the same because
// no two instructors will have the same name and teach the same courses
// two professors might have the same name but be different instructors
class SameInstructor implements BiFunction<Instructor, Instructor, Boolean> {
  public Boolean apply(Instructor i1, Instructor i2) {
    return i1.name.equals(i2.name) && i1.courses.sameList(i2.courses, new SameCourse());
  }
}

// STUDENT -------------------------
// represents a student with a name, id, and list of courses

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
    return this.courses.anyOverlap(s.courses, new SameCourse());
  }
}

// class to represent a function that checks if two students are the same
// since each student has a unique ID, that is the only thing that needs
// to be checked to see if one student is the same as another
class SameStudent implements BiFunction<Student, Student, Boolean> {
  public Boolean apply(Student s1, Student s2) {
    return s1.id == s2.id;
  }
}

// LIST FUNCTIONS AND OBJECTS -------------------------

// BiFunction object to add two integers
class Add implements BiFunction<Integer, Integer, Integer> {
  public Integer apply(Integer x, Integer y) {
    return x + y;
  }
}

// BiFunction object to get the number of occurrences of a student in a course
class NumOccurrences implements BiFunction<Course, Student, Integer> {
  public Integer apply(Course c, Student s) {
    return c.students.numOccurrencesAcc(s, 0, new SameStudent());
  }
}

interface IList<T> {
  // checks if there is any overlap between the given list and another list of the same type
  boolean anyOverlap(IList<T> other, BiFunction<T, T, Boolean> testSame);

  // checks if the given element is in the list
  boolean contains(T element, BiFunction<T, T, Boolean> testSame);
  
  // accumulator for numOccurrences
  // accumulates the number of occurrences of an object so far in the list
  // starts with 0 at the beginning
  // is updated when the first item in the cons is the same as the desired object
  int numOccurrencesAcc(T thing, int num, BiFunction<T, T, Boolean> testSame);

  // folds the list from the right
  // applies the given function to each element in the list
  <U> U foldr(BiFunction<T, U, U> func, U base);

  // maps the given function to each element in the list
  // returns a new list with the results of the function applied to each element
  <U, A> IList<U> map(BiFunction<T, A, U> f, A somethingElse);

  // test equality of lists
  boolean sameList(IList<T> list, BiFunction<T, T, Boolean> testSame);

  // comapare list to empty list
  boolean equalsMt(MtList<T> mt, BiFunction<T, T, Boolean> testSame);

  // comapare list to non empty list
  boolean equalsCons(ConsList<T> cons, BiFunction<T, T, Boolean> testSame);
}

class MtList<T> implements IList<T> {
  // checks if there is any overlap between this empty list and another list
  // always false
  public boolean anyOverlap(IList<T> other, BiFunction<T, T, Boolean> testSame) {
    return false;
  }
  
  // tests if the empty list contains the given element, always false
  public boolean contains(T element, BiFunction<T, T, Boolean> testSame) {
    return false;
  }
  
  // accumulator for numOccurences, returns the given number
  public int numOccurrencesAcc(T thing, int num, BiFunction<T, T, Boolean> testSame) {
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

  // test equality of lists
  public boolean sameList(IList<T> other, BiFunction<T, T, Boolean> testSame) {
    return other.equalsMt(this, testSame);
  }

  // comapare this empty list to an empty list, always true
  public boolean equalsMt(MtList<T> mt, BiFunction<T, T, Boolean> testSame) {
    return true;
  }

  // comapare this empty list to cons list, always false
  public boolean equalsCons(ConsList<T> cons, BiFunction<T, T, Boolean> testSame) {
    return false;
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
  public boolean anyOverlap(IList<T> other, BiFunction<T, T, Boolean> testSame) {
    return other.contains(this.first, testSame) || this.rest.anyOverlap(other, testSame);
  }
  
  // checks if the given element is in this list
  public boolean contains(T element, BiFunction<T, T, Boolean> testSame) {
    return testSame.apply(this.first, element) || this.rest.contains(element, testSame);
  }
  
  // accumulator for numOccurrences
  // accumulates the number of occurrences of an object so far in the list
  public int numOccurrencesAcc(T thing, int num, BiFunction<T, T, Boolean> testSame) {
    if (testSame.apply(this.first, thing)) {
      return this.rest.numOccurrencesAcc(thing, num + 1, testSame);
    }
    else {
      return this.rest.numOccurrencesAcc(thing, num, testSame);
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

  // test equality of lists
  public boolean sameList(IList<T> other, BiFunction<T, T, Boolean> testSame) {
    return other.equalsCons(this, testSame);
  }

  // comapare this empty list to an empty list, always true
  public boolean equalsMt(MtList<T> mt, BiFunction<T, T, Boolean> testSame) {
    return false;
  }

  // comapare this empty list to cons list, always false
  public boolean equalsCons(ConsList<T> cons, BiFunction<T, T, Boolean> testSame) {
    return testSame.apply(this.first, cons.first) && this.rest.sameList(cons.rest, testSame);
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

  void testOverlap(Tester t) {
    initData();
    t.checkExpect(shresht.courses.anyOverlap(lyanne.courses, new SameCourse()), true);
    t.checkExpect(lyanne.courses.anyOverlap(tyler.courses, new SameCourse()), false);
  }

  void testContains(Tester t) {
    initData();
    t.checkExpect(shresht.courses.contains(cs5600, new SameCourse()), true);
    t.checkExpect(lyanne.courses.contains(cs1800, new SameCourse()), false);
  }

  void testNumOccurrences(Tester t) {
    initData();
    t.checkExpect(new NumOccurrences().apply(cs5600, shresht), 1);
    t.checkExpect(new NumOccurrences().apply(cs5600, lyanne), 0);
    t.checkExpect(cs1800.students.numOccurrencesAcc(bensen, 0, new SameStudent()), 1);
    t.checkExpect(cs1800.students.numOccurrencesAcc(shresht, 0, new SameStudent()), 0);
  }

  boolean testEnroll(Tester t) {
    initData();
    Student testStudent = new Student("Test Student", 6);
    Course testCourse = new Course("Test Course", prof1);
    testStudent.enroll(testCourse);
    return t.checkExpect(testStudent.courses.contains(testCourse, new SameCourse()), true) &&
           t.checkExpect(testCourse.students.contains(testStudent, new SameStudent()), true) &&
           t.checkExpect(prof1.courses.contains(testCourse, new SameCourse()), true);
  }

  boolean testAddCourse(Tester t) {
    initData();
    Student testStudent = new Student("Test Student", 6);
    Course testCourse = new Course("Test Course", prof1);
    testStudent.enroll(testCourse);
    return t.checkExpect(testStudent.courses.contains(testCourse, new SameCourse()), true) &&
           t.checkExpect(testCourse.students.contains(testStudent, new SameStudent()), true) &&
           t.checkExpect(prof1.courses.contains(testCourse, new SameCourse()), true);
  }
    
  boolean testSameStudent(Tester t) {
    initData();
    return t.checkExpect(new SameStudent().apply(shresht, 
        new Student("Shresht Bhowmick", 1)), true) &&
           t.checkExpect(new SameStudent().apply(shresht, lyanne), false) &&
           t.checkExpect(new SameStudent().apply(shresht, shresht), true) &&
           t.checkExpect(new SameStudent().apply(shresht, 
               new Student("Shresht Bhowmick", 2)), false) &&
           t.checkExpect(new SameStudent().apply(shresht, new Student("Not Shresht", 1)), true);
  }

  boolean testSameCourse(Tester t) {
    initData();
    Instructor prof1copy = new Instructor("Daniel Patterson");
    Course cs2510copy = new Course("CS2510", prof1copy);
    Student lyannecopy = new Student("Lyanne Xu", 2);
    Student bensencopy = new Student("Bensen Wang", 3);
      
    lyannecopy.enroll(cs2510copy);
    bensencopy.enroll(cs2510copy);
    
    return t.checkExpect(new SameCourse().apply(cs2510, new Course("CS2500", prof1)), false) &&
           t.checkExpect(new SameCourse().apply(cs2510, cs2500), false) && 
           t.checkExpect(new SameCourse().apply(cs2510, cs2510copy), true);
  }
  
  boolean testSameInstructor(Tester t) {
    initData();
    Instructor prof1copy = new Instructor("Daniel Patterson");
    Course cs2500copy = new Course("CS2500", prof1copy);
    Course cs2510copy = new Course("CS2510", prof1copy);
    Student shreshtcopy = new Student("Shresht Bhowmick", 1);
    Student lyannecopy = new Student("Lyanne Xu", 2);
    Student bensencopy = new Student("Bensen Wang", 3);
    Student eoincopy = new Student("Eoin Collette", 5);

    shreshtcopy.enroll(cs2500copy);
    lyannecopy.enroll(cs2500copy);
    lyannecopy.enroll(cs2510copy);
    bensencopy.enroll(cs2510copy);
    eoincopy.enroll(cs2500copy);

    return t.checkExpect(new SameInstructor().apply(prof1, 
        new Instructor("Daniel Patterson")), false) 
        && t.checkExpect(new SameInstructor().apply(prof2, prof1), false)
        && t.checkExpect(new SameInstructor().apply(prof1, prof1copy), true);
  }

  boolean testDeja(Tester t) {
    initData();
    return t.checkExpect(prof1.dejavu(shresht), false) &&
           t.checkExpect(prof2.dejavu(lyanne), false) &&
           t.checkExpect(prof2.dejavu(tyler), true);
  }

  boolean testClassmates(Tester t) {
    initData();
    return t.checkExpect(shresht.classmates(lyanne), true) &&
           t.checkExpect(shresht.classmates(bensen), false);
  }
}

