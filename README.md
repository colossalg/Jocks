Jocks
=====

A programming language inspired by Lox, but still a little crappy.

Data Types
----------

Jocks supports several primitive data types as can be found in most dynamic programming languages:

| Data Type | Example | Notes |
| --- | --- | ---|
| Numbers | `1`, `1.0` | There is no distinction between integer and floating point values, they are all stored as doubles internally). |
| Strings | `"ABCD"` | Must use double quotes. |
| Booleans | `true` / `false` | |
| Null | `nil` | |

Additionally, users may define their own types via `class` declarations (see below).

Operators
---------

**NOTE:**
Most of the following operators (unary and binary) may be overridden for user-defined classes by implementing methods with corresponding names.
This behaviour is described below in the corresponding **Operator Overloading** section found below.

Jocks supports the following **unary** operators:

| Operator | Valid Primitive Data Types | Result |
| --- | --- | --- |
| `+` | Numbers | The number's value. |
| `-` | Numbers | The number's value negated. |
| `!` | Booleans | The boolean's value negated. |

Jocks supports the following **binary arithmetic** operators:
| Operator | Valid Primitive Data Types | Result |
| --- | --- | --- |
| `+` | Number, string | The sum of the two numbers, or the concatenation of the two strings. |
| `-` | Number | As expected for subtraction of two numbers. |
| `*` | Number | As expected for multiplication of two numbers. |
| `/` | Number | As expected for division of two numbers. |

Jocks supports the following **binary logical** operators:
| Operator | Valid Primitive Data Types | Result |
| --- | --- | --- |
| `or` | Boolean | As expected from 'OR'ing two booleans (short circuits if first expression is `true`). |
| `and` | Boolean | As expected from 'AND'ing two booleans (short circuits if first expression is `false`). |

Variables
---------

Variables are declared using the `var` keyword, they must be assigned a value when declared.

```JavaScript
var variable_name = variable_value;
```

Assignment expressions evaluate to the value assigned to the variable.

```JavaScript
var variable_name = nil;
variable_name = variable_value; // Evaluates to the value of 'variable_value' assigned to 'variable_name'.
```

Variables are dynamically typed, they may be assigned values of any type even if they differ to the type initially assigned.

```JavaScript
var variable_name = nil;
variable_name = 1.0;
variable_name = "a";
variable_name = true;
variable_name = new SomeClass();
variable_name = nil;
```

Variables are lexically scoped (both global and local) as per the function/method/block they are defined in.
Variables declared within a scope may shadow variables in an enclosing scope.
A function declaration will also capture the variables in an enclosing scope.
This behaviour is described below in the corresponding **Closures** section found below.

```JavaScript
var a = 1;
print a;          // 1
print b;          // Error - variable not defined.
{
    var a = 2;    // Shadows outer declaration.
    var b = 3;
    print a;      // 2
    print b;      // 3
}
print a;          // 1
print b;          // Error - variable not defined.
```

Control Flow
------------

Jocks supports many of the usual control flow constructs found in most programming languages:
* `if`/`else` conditionals.
* `while` loops.
* `for` loops.

### If/Else Blocks

`if`/`else` blocks behave as would be expected normally.
The `else` block is optional and may be omitted entirely.

A `condition` expression is evaluated:
* If it is `true` the statement immediately following the parenthesis encompassing the `condition` will be executed.
* If it is `false` the statement immediately following the `else` keyword will be executed if it is present.

```Python
if (condition)
    // Then statement ...
```

```Python
if (condition)
    // Then statement ...
else
    // Else statement ...
```

### While Loops

TODO

```JavaScript
while (condition)
    // Loop statement ...
```

### For Loops

TODO

```JavaScript
for (/* optional initializer */; /* optional condition */; /* optional increment */)
    // Loop statement ...
```

```JavaScript
for (var i = 0; i < 10; i = i + 1)
    // Loop statement ...
```

```JavaScript
for (;;)
    // Loop statement ...
```

Print Statements
----------------

TODO

```Python
print some_value;
```

Functions
---------

TODO

```JavaScript
fun name(/* parameter list*/) {
    // Function body ...
}
```

```JavaScript
fun get_full_name(first_name, last_name) {
    return first_name + last_name;
}

var shortcut = get_full_name;
print shortcut("John", "Doe");
```

Closures
--------

Classes
-------

TODO

```JavaScript
class Pet {
    fun __init__(self, owner, name, type) {
        self.owner = owner;
        self.name = name;
        self.type = type;
    }

    fun get_description(self) {
        return self.owner + "'s " + self.type + " " + self.name;
    }

    fun make_noise(self) {
        // Empty
    }
}

var georges_fish = new Pet("George", "Wanda", "fish");
print georges_fish.get_description();   // Prints "George's fish Wanda".
georges_fish.make_noise();              // Does nothing.
```

```JavaScript
class Cat < Pet {
    fun __init__(self, owner, name) {
        super.__init__(self, owner, name, "cat");
    }

    fun make_noise(self) {
        print "Meow";
    }
}

class Dog < Pet {
    fun __init__(self, owner, name) {
        super.__init__(self, owner, name, "dog");
    }

    fun make_noise(self) {
        print "Woof";
    }
}

var carries_cat = new Cat("Carrie", "Fluffy");
print carries_cat.get_description();            // Prints "Carrie's cat Fluffy" (inherited from Pet).
carries_cat.make_noise();                       // Prints "Meow" (overrides Pet implementation).

var debrahs_dog = new Dog("Debrah", "Spotty");
print debrahs_dog.get_description();            // Prints "Debrah's dog Spotty" (inherited from Pet).
debrahs_dog.make_noise();                       // Prints "Woof" (overrides Pet implementation).
```

Operator Overloading
--------------------

TODO

```JavaScript
class OverloadExamples {

    fun __str__(self) {
        // Implementation
    }

    fun __equal__(self, other) {
        // Overrides comparisson in '==' binary operations.
    }

    fun __not_equal__(self, other) {
        // Overrides comparisson in '!=' binary operations.
    }

    fun __less_than__(self, other) {
        // Overrides comparisson in '<' binary operations.
    }

    fun __less_than_or_equal__(self, other) {
        // Overrides comparisson in '<=' binary operations.
    }

    fun __more_than__(self, other) {
        // Overrides comparisson in '>' binary operations.
    }

    fun __more_than_or_equal__(self, other) {
        // Overrides comparisson in '>=' binary operations.
    }

    fun __add__(self, other) {
        // Overrides comparisson in '+' binary operations.
    }

    fun __sub__(self, other) {
        // Overrides comparisson in '-' binary operations.
    }

    fun __mul__(self, other) {
        // Overrides comparisson in '*' binary operations.
    }

    fun __div__(self, other) {
        // Overrides comparisson in '/' binary operations.
    }

    fun __unary_add__(self, other) {
        // Overrides comparisson in '+' unary operations.
    }

    fun __unary_sub__(self, other) {
        // Overrides comparisson in '-' unary operations.
    }
}
```

Exceptions
----------

TODO

```JavaScript
fun foo() {
    throw /* expression */;
}
```

```JavaScript
try {
    // Try statement(s) ...
} catch (e) {
    // Catch statement(s) ...
}
```
