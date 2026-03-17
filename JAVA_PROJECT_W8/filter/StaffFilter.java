package filter;

import user.Person;

// PDF Section 12: Functional Interface — an interface with exactly ONE abstract method.
// The @FunctionalInterface annotation tells the compiler to enforce this rule.
// If someone tries to add a second abstract method, it will not compile.
//
// PDF Section 9:  Can be implemented using an anonymous inner class (verbose).
// PDF Section 13: Can be implemented using a lambda expression (short).
//
// Example anonymous class:
//   StaffFilter filter = new StaffFilter() {
//       @Override
//       public boolean test(Person staff) {
//           return staff.isActive();
//       }
//   };
//
// Example lambda (PDF Section 13):
//   StaffFilter filter = staff -> staff.isActive();
@FunctionalInterface
public interface StaffFilter {
    boolean test(Person staff); // Single abstract method
}