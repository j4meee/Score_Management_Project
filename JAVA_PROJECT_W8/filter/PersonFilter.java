package filter;

import user.Person;

// PDF Section 12: Functional Interface — an interface with exactly ONE abstract method.
// PersonFilter is a more general version of StaffFilter.
// Used when filtering any Person (Admin, Teacher, or Student).
//
// Example anonymous class:
//   PersonFilter filter = new PersonFilter() {
//       @Override
//       public boolean test(Person person) {
//           return person instanceof Teacher;
//       }
//   };
//
// Example lambda (PDF Section 13):
//   PersonFilter filter = person -> person instanceof Teacher;
//
// Example with stream (PDF Section 15):
//   users.stream()
//        .filter(person -> person instanceof Teacher)
//        .forEach(person -> System.out.println(person.getFullName()));
@FunctionalInterface
public interface PersonFilter {
    boolean test(Person person); // Single abstract method
}