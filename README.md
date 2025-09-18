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
| Numbers | `1`, `1.0` | There is no distinction between integer and floating point values, they are all stored as doubles internally). |
| Strings | `"ABCD"` | Must use double quotes. |
| Booleans | `true` / `false` | |
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

### Assignment Statements

Variable declarations consist of the following:
* `var` keyword.
* The identifier for the variable (consisting of '_', letters, numbers, and starting with a non-digit character).
* `=`.
* The value to be assigned to the variable.

```
var variable_name = variable_value;
```

### Assignment Expressions

Assignment expressions evaluate to the value assigned to the variable.

```
var variable_name = nil;
variable_name = variable_value; # Evaluates to the value of 'variable_value' assigned to 'variable_name'.
```

Variables are dynamically typed, they may be assigned values of any type even if they differ to the type initially assigned.

```
var variable_name = 1.0;
variable_name = 2.0;
variable_name = "abc";
variable_name = true;
variable_name = false;
variable_name = nil;
variable_name = new SomeClass();
```

Variables are lexically scoped (both global and local) as per the function/method/block they are defined in.
Variables declared within a scope may shadow variables in an enclosing scope.
A function declaration will also capture the variables in an enclosing scope.
This behaviour is described in the corresponding **Closures** section found below.

```
var a = 1;
print a;          # 1
print b;          # Error - variable not defined.
{
    var a = 2;    # Shadows outer declaration.
    var b = 3;
    print a;      # 2
    print b;      # 3
}
print a;          # 1
print b;          # Error - variable not defined.
```

Control Flow
------------

Jocks supports many of the usual control flow constructs found in most programming languages:
* `if`/`else` conditionals.
* `while` loops.
* `for` loops.

These all work more or less as expected.

### If/Else Blocks

`if`/`else` blocks consist of the following:
* `for` keyword.
* Opening `(`.
* Condition expression.
* Closing `)`.
* 'Then' statement.
* (BELOW ARE OPTIONAL BUT ALL REQUIRED TOGETHER)
* `else` keyword.
* 'Else' statement.

The condition expression is evaluated:
* If it is `true`, the 'then' statement is executed.
* If it is `false`, the 'else' statement is executed (if it is present).

Here is an `if` statement WITHOUT the optional `else` clause.

```
if (condition)
    # Then statement ...
```

Here is an `if` statement WITH the optional `else` clause.

```
if (condition)
    # Then statement ...
else
    # Else statement ...
```

### While Loops

`while` loops consist of the following:
* `while` keyword.
* Opening `(`.
* Condition expression.
* Closing `)`.
* 'Loop' statement.

The condition expression is evaluated at the start of each iteration:
* If it is `true`, the loop statement will be executed.
* If it is `false`, the loop will exit and the interpreter will continue with the next statement.

```
while (condition)
    # Loop statement ...
```

### For Loops

`for` loops consist of the following:
* `for` keyword.
* Opening `(`.
* (Optional) The initializer, this can be an expression or a variable definition using `var`.
* `;`
* (Optional) Condition expression.
* `;`
* (Optional) Increment expression.
* Closing `)`.
* 'Loop' statement.

The first time the loop is encountered the initializer is executed.
If this is a variable declaration, then the variable is scoped to the loop statement.

The condition expression is evaluated at the start of each iteration:
* If it is `true`, the loop statement will be executed.
* If it is `false`, the loop will exit and the interpreter will continue with the next statement.

The increment expression is evaluated at the end of each iteration.

```
for (optional_initializer; optional_condition; optional_increment)
    # Loop statement ...
```

Example with a variable declaration.

```
for (var i = 0; i < 10; i = i + 1)
    # Loop statement ...
```

Print Statements
----------------

Print statements convert values to a string then print them to the stdout.

For the primitive types this is straight forward:

```
print 1;      # 1.0
print 1.0;    # 1.0
print "abc";  # abc
print true;   # true
print false;  # false
print nil;    # nil
```

For functions, classes and instances they print a rough diagnostic ex. `JocksUserLandFunction(dummy)`.

Classes can override how they are converted to strings by overriding the `__str__` method.
This behaviour is described in the corresponding **Operator Overloading** section found below.

Functions
---------

Function declarations consist of the following:
* `fun` keyword.
* The identifier for the function (consisting of '_', letters, numbers, and starting with a non-digit character).
* Opening `(`.
* The list of parameter identifiers, following normal variable naming rules and separated by `,`.
* Closing `)`.
* Opening `{`.
* The function body, which is a list of statements to be executed upon function invocation.
* Closing `}`.

The scoping rules for the function name itself are as per a normal variable.
The parameters are scoped to the function body.

```
fun function_name(parameter_1, parameter_2, etc) {
    # Function body ...
}

# Invocation
function_name(arg_1, arg_2, etc);
```

Functions are first class in Jocks, they can be assigned to variables and invoked from there too.

```
fun foo() {
    print "foo";
}

var foo_var = foo;
foo_var(); # foo
```

Closures
--------

TODO

Classes
-------

TODO

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

Operator Overloading
--------------------

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
This is best illustrated with an example:

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

A value may be thrown using the `throw` keyword followed by an expression to produce the thrown value and a terminating `;`.
Values of any type may be thrown, and currently there is no way to distinguish by type when deciding whether to catch a thrown value.

```
throw value_producing_expression;
```

`try`/`catch` blocks consist of the following:
* `try` keyword.
* The 'try' statement to be executed which may `throw` from somewhere during execution.
* `catch` keyword.
* Opening `(`.
* An identifier for the variable representing any caught value.
* Closing `)`.
* The 'catch' statement to be executed if any value is caught.

Within the 'try' statements, if any value is thrown, then execution of the 'try' statement will cease from the point of the `throw` statement.
The thrown value will then be assigned to the identifier trailing the `catch` keyword (this variable is scoped to the catch statement).
The 'catch' statement will then be executed (if no `throw` happens in the 'try' statement the 'catch' statement will never ececute).

```
try
    # Try statement(s) ...
catch (e)
    # Catch statement(s) ...
```
