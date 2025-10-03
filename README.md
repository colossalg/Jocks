<p align="center">
    <img
        width="240"
        height="240"
        alt="JocksLogo"
        src="https://github.com/user-attachments/assets/3a64a874-5a12-463f-b741-129340f021cf"
    />
</p>
<p align="center">
    A programming language inspired by Lox, but still a little crappy.
</p>
<p align="center">
    Learn more about Lox in <a href="https://craftinginterpreters.com/">Crafting Interpreters</a>.
</p>



Data Types
----------

Jocks supports several primitive data types as can be found in most dynamic programming languages:

| Data Type | Example | Notes |
| --- | --- | ---|
| Numbers | `1`, `2.3` | There is no distinction between integer and floating point values, they are all stored as doubles internally. |
| Strings | `"ABCD"` | Must use double quotes. |
| Booleans | `true`, `false` | |
| Null | `nil` | |

Additionally, users may define their own types via `class` declarations (see below).

Operators
---------

**NOTE:**
Most of the following operators (unary and binary) may be overridden for user-defined classes by implementing methods with corresponding names.
This behaviour is described in the corresponding **Operator Overloading** section found below.

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

### Variable Declarations

Variable declarations are performed via the `var` keyword.
* A variable name must consist of underscores, letters, or digits, and start with either an underscore or letter.
* It is mandatory to assign a variable a value upon declaration.

```
var num_var_1  = 1;
var num_var_2  = 2.3;
var str_var    = "ABCD";
var bool_var_1 = true;
var bool_var_2 = false;
var nil_var    = nil;
var class_var  = new SomeClass();
```

### Assignment Expressions

The value of an existing variable may be changed via an assignment expression using the `=` operator.

Assignment expressions evaluate to the value assigned to the variable.

```
var v = nil;
print (v = "Hello, World!"); # Prints "Hello, World!".
print v;                     # Prints "Hello, World!".
```

### Variable Typing

Variables are dynamically typed, they may be assigned values of any type even if they differ to the type initially assigned.

```
var v = 1.0;
v = 2.3;
v = "ABCD";
v = true;
v = false;
v = nil;
v = new SomeClass();
```

### Variable Scoping

Variables are lexically scoped (both global and local) as per the block/function they are defined in.

Variables declared within a scope may shadow variables in an enclosing scope.

```
var a = 1;
print a;          # Prints "1.0".
print b;          # Error - variable not defined.
{
    var a = 2;    # Shadows outer declaration.
    var b = 3;
    print a;      # Prints "2.0".
    print b;      # Prints "3.0".
}
print a;          # Prints "1.0".
print b;          # Error - variable not defined.
```

Control Flow
------------

Jocks supports many of the usual control flow constructs found in most programming languages:
* `if`/`else`
* `while`
* `for`

These all work as expected for most common programming languages.

### If/Else 

An `if`/`else` block is used to conditionally execute code based upon the result of a 'condition-expression'.
* If the 'condition-expression' evaluates to `true` then the 'then-statement' will be executed.
* If the 'condition-expression' evaluates to `false` then the 'else-statement' will be executed.

```
if (condition-expression)
    # then-statement
else
    # else-statement

# Interpreter will continue from here.
# ...
```

The `else` keyword and accompanying 'else-statement' are optional and may be omitted entirely.

In this case, if the 'condition-expression' evaluates to `false` nothing will execute.

```
if (condition-expression)
    # then-statement

# Interpreter will continue from here.
# ...
```

### While Loops

A `while` loop is used to repeatedly execute code based upon the result of a 'condition-expression'.

The execution of a `while` loop works as follows:
1. The 'condition-expression' is evaluated:
    * If this evaluates to `true` continue to 2.
    * If this evaluates to `false` continue to 4.
2. The 'loop-body' is executed.
3. Return to 1.
4. Resume execution following the `while` loop.

```
while (condition-expression)
    # loop-body

# Interpreter will continue from here.
# ...
```

### For Loops

A `for` loop is used to repeatedly execute code based upon the result of a 'condition-expression'.

These differ to `while` loops as they contain more components to control the loop:
* The 'initializer-statement' run once when the loop is first encountered.
  This can be either of the following:
    * A variable declaration.
    * An expression statement.
* The 'condition-expression' run at the start of each iteration.
* The 'increment-expression' run at the end of each iteration.

The execution of a `for` loop works as follows:
1. The 'initializer-statement' is evaluated.
   If this is a variable declaration, then the variable is scoped to the loop and available in the following:
     * The 'condition-expression'.
     * The 'increment-expression'.
     * The 'loop-body'.
2. The 'condition-expression' is evaluated:
    * If this evaluates to `true` continue to 3.
    * If this evaluates to `false` continue to 6.
3. The 'loop-body' is executed.
4. The 'increment-expression' is evaluated.
5. Return to 2.
5. Resume execution following the `for` loop.

```
for (initializer-statement; condition-expression; increment-expression)
    # loop-body

# Interpreter will continue from here.
# ...
```

Print Statements
----------------

`print` statements convert values to a string then print them to `stdout`.

For the primitive types this is straight forward:

```
print 1;      # 1.0
print 2.3;    # 2.3
print "ABCD"; # abc
print true;   # true
print false;  # false
print nil;    # nil
```

For functions, classes and instances they print a rough diagnostic ex. `JocksUserLandFunction(dummy)`.

Classes can override how they are converted to strings by overriding the `__str__` method.

This behaviour is described in the corresponding **Operator Overloading** section found below.

Functions
---------

### Function Declarations

Function declarations are performed via the `fun` keyword.
* A function name must obey the regular naming rules for variables.
* Parameter names must obey the regular naming rules for variables.

```
fun name(parameters-names...) {
    # function-body
}
```

This is best illustrated with an example.

```
fun distance(x1, y1, x2, y2) {
    var dx = x1 - x2;
    var dy = y1 - y2;
    var distance_squared = dx * dx + dy * dy;
    return pow(distance_squared, 0.5);
}

# Invocation is as per most common programming languages.
var d =  distance(0, 0, 3, 4);
print d;                     # Prints "5.0".
```

If a function does not explicitly `return` a value, then it will implicitly `return` `nil`.

### Function Scoping

The function declaration introduces a new name into the current scope in the same manor as a variable declaration.

The parameters for a function are scoped to the function's body.

```
print greet("John");         # Error - variable not defined.

{
    print greet("John");     # Error - variable not defined.
    print name;              # Error - variable not defined.

    fun greet(name) {
        return "Hello, " + name + "!";
    }

    print greet("John");     # Prints "Hello, John!".
    print name;              # Error - variable not defined.

    # As function declarations just declare a variable whose
    # value is a function, then they can be assigned to
    # other variables and invoked via alternative
    # identifiers.
    var greet_var = greet;

    print greet_var("John"); # Prints "Hello, John!".
    print name;              # Error - variable not defined.
}

print greet("John");         # Error - variable not defined.
```

### Closures

When a function is declared it captures the surrounding context.

This behaviour facilitates closures which can be useful under several contexts.

This is best illustrated via an example.

```
fun make_counter() {
    var count = 0;
    fun counter() {
        # Count is captured from the context above, and
        # is available even after make_counter returns.
        count = count + 1;
        return (count - 1);
    }
    return counter;
}

var counter = make_counter();
while (counter() < 10) {
    print "Another"; # Will execute 10 times.
}
```

Classes
-------

### Class Declarations

`clas` declarations are performed via the `class` keyword.
* A `class` name must obey the regular naming rules for variables.
* Method names must obey the regular naming rules for variables.

```
class Name < optional-super-class {
    # Methods...
}
```

Methods are simply function declarations with the caveat that they will require a `self` parameter to be useful when invoked on an instance.

This is similar to Python, see the **Instances** section below for more information.

This is best illustrated via an example.

```
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
        # Empty
    }
}

var georges_fish = new Pet("George", "Wanda", "fish");
print georges_fish.get_description();   # Prints "George's fish Wanda".
georges_fish.make_noise();              # Does nothing.
```

### Class Scoping

As per functions above, a `class` declaration just introduces a variable into the current scope whose value is the `class`.

The rules for `class` scoping are as per variables and functions.

A `class` is first class in Jocks, they may be assigned to other variables and used via an alternate identifier to what they were declared with.

### Inheritance

If a `class` declaration includes an optional `super` `class`, to inherit from, then all methods declared within the `super` `class`
(or those it has interited) will be available to the `class`, and all of its instances. Finding the appropriate method to invoke on
a class or instance is similar to the behaviour of languages such as JavaScript. The classes form a chain which is walked at runtime
from the instance or `class` the method was invoked on until a `class` declaring that method is finally found. A `class` may wish to
delegate fully or partially to the `super` `class`, however, especially in the `__init__` method. This is possible using `super` which
is not a keyword, but a variable available from the context of all `class` method declarations.

This is best illustrated via an example.

```
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
print carries_cat.get_description();            # Prints "Carrie's cat Fluffy" (inherited from Pet).
carries_cat.make_noise();                       # Prints "Meow" (overrides Pet implementation).

var debrahs_dog = new Dog("Debrah", "Spotty");
print debrahs_dog.get_description();            # Prints "Debrah's dog Spotty" (inherited from Pet).
debrahs_dog.make_noise();                       # Prints "Woof" (overrides Pet implementation).
```

### Instances

Instances are created using the `new` keyword.

This mechanism works as follows:
* A fresh instance is created, and its class is set to the class referenced in the `new` expression.
* The `__init__` method is then automatically invoked on this instance:
    * Invoking the method will pass the instance as the first parameter (generally named `self`).
    * The remaining parameters passed to the `new` expression will be forwarded.
* This works because instances may be assigned properties dynamically, and have no access control modifiers.

```
class SomeClass {
    fun __init__(self, member_1, member_2) {
        self.member_1 = member_1;
        self.member_2 = member_2;
    }
}

var instance = new SomeClass("A", "B");
# This will create an instance of SomeClass, then immediately invoke __init__(instance, "A", "B").
```

### Dot Expressions

The behaviour of `.` expressions are different based upon whether it is performed on a `class` or instance.
* On a `class`, the appropriate method is found and returned as normal with no additional steps.
* On an instance, the appropriate method is 'found and bound', then this bound method returned.

The 'binding' process on an instance works by:
* Capturing the instance the method was invoked on.
* Wrapping the method in an anonymous function that will:
    * Pass the instance as the first parameter to the method.
    * Pass its own parameters as the remaining parameters to the method.
* The bound method is just a normal function, it's first class and can be assigned, etc. as normal.

This is best illustrated via an example.

```
class Person {
    fun __init__(self, fname, lname) {
        self.fname = fname;
        self.lname = lname;
    }

    fun get_full_name(self) {
        return self.fname + " " + self.lname;
    }
}

var p = new Person("John", "Doe");

var full_name = p.get_full_name; # This is a bound method, it captures p in the context.
                                 # Calling full_name was equivalent to calling Person.get_full_name(p).

print full_name(); # Prints "John Doe".
```

### Operator Overloading

Classes may override many of the common operators (both binary and unary) by defining methods with corresponding names.

| Method Signature | Operation Overloaded |
| --- | --- |
| `fun __str__(self)` | String conversion for `print` statements. |
| `fun __equal__(self, other)` | Comparisson in `==` operations. |
| `fun __not_equal__(self, other)` | Comparisson in `!=` operations. |
| `fun __less_than__(self, other)` | Comparisson in `<` operations. |
| `fun __less_than_or_equal__(self, other)` | Comparisson in `<=` operations. |
| `fun __more_than__(self, other)` | Comparisson in `>` operations. |
| `fun __more_than_or_equal__(self, other)` | Comparisson in `>=` operations. |
| `fun __add__(self, other)` | Comparisson in `+` operations. |
| `fun __sub__(self, other)` | Comparisson in `-` operations. |
| `fun __mul__(self, other)` | Comparisson in `*` operations. |
| `fun __div__(self, other)` | Comparisson in `/` operations. |
| `fun __unary_add__(self)` | Comparisson in `+` operations (unary). |
| `fun __unary_sub__(self)` | Comparisson in `-` operations (unary). |

For the binary operations, the operation is considered to be triggered on the left operand.

This is best illustrated via an example.

```
var a = new A();
var b = new B();
var result = a + b; # This will trigger A.__add__ with a assigned to self, and b assigned to other.
```

An example of operator overloading may be 2D points where several arithmetic operations make sense:

```
class Point2D {
    fun __init__(self, x, y) {
        self.x = x;
        self.y = y;
    }

    fun __str__(self) {
        return "Point2D { x = " + to_string(self.x) + ", y = " + to_string(self.y) + " }";
    }

    fun __add__(self, other) {
        # Note it is hard to secure binary operator functions correctly at present
        # as a good means to check the class name for an instance is lacking.
        return new Point2D(self.x + other.x, self.y + other.y);
    }
}

var p1 = new Point2D(1, 2);
var p2 = new Point2D(3, 4);
var p3 = p1 + p2;
print p3; # Point2D { x = 3.0, y = 4.0 }
```

Exceptions
----------

A value may be thrown using the `throw` keyword followed by an expression to produce the thrown value.

Values of any type may be thrown, and currently there is no way to distinguish by type when deciding whether to catch a thrown value.

```
throw value-producing-expression;
```

Once a value is thrown, execution will cease and the call stack will be unwound until a `try`/`catch` block is encountered:
* The thrown value will then be assigned to the identifier trailing the `catch` keyword.
  This variable is scoped to the catch statement.
* The 'catch' statement will then be executed.
  If no `throw` happens in the 'try-statement' the 'catch-statement' will never ececute.

```
try
    # try-statement
catch (e)
    # catch statement
```

Comments
--------

Comments are created using the `#` character and last to the end of the line.

```
var x = nil; # This is a comment.
```

What's Left
-----------

The language could benefit from some finishing touches:
- Modules so a program can be split over more than one file.
- Extending the standard library (basic maths, IO, etc.).
