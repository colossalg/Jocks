Jocks
=====

A programming language inspired by Lox, but still a little crappy.

Data Types
----------

TODO

Operators
---------

TODO

```JavaScript
+a;
-a;
!a;
```

```JavaScript
a + b;
a - b;
a * b;
a / b;
```

```JavaScript
a or b;
a and b;
```

Variables
---------

TODO

```JavaScript
var name = val;
```

Control Flow
------------

TODO

```JavaScript
if (condition)
    // Then statement ...
```

```JavaScript
if (condition) {
    // Then statement ...
} else {
    // Else statement ...
}
```

```JavaScript
while (condition)
    // Loop statement ...
```

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
