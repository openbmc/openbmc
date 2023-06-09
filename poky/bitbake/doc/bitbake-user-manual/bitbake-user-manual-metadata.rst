.. SPDX-License-Identifier: CC-BY-2.5

====================
Syntax and Operators
====================

|

BitBake files have their own syntax. The syntax has similarities to
several other languages but also has some unique features. This section
describes the available syntax and operators as well as provides
examples.

Basic Syntax
============

This section provides some basic syntax examples.

Basic Variable Setting
----------------------

The following example sets ``VARIABLE`` to "value". This assignment
occurs immediately as the statement is parsed. It is a "hard"
assignment. ::

   VARIABLE = "value"

As expected, if you include leading or
trailing spaces as part of an assignment, the spaces are retained::

   VARIABLE = " value"
   VARIABLE = "value "

Setting ``VARIABLE`` to "" sets
it to an empty string, while setting the variable to " " sets it to a
blank space (i.e. these are not the same values). ::

   VARIABLE = ""
   VARIABLE = " "

You can use single quotes instead of double quotes when setting a
variable's value. Doing so allows you to use values that contain the
double quote character::

   VARIABLE = 'I have a " in my value'

.. note::

   Unlike in Bourne shells, single quotes work identically to double
   quotes in all other ways. They do not suppress variable expansions.

Modifying Existing Variables
----------------------------

Sometimes you need to modify existing variables. Following are some
cases where you might find you want to modify an existing variable:

-  Customize a recipe that uses the variable.

-  Change a variable's default value used in a ``*.bbclass`` file.

-  Change the variable in a ``*.bbappend`` file to override the variable
   in the original recipe.

-  Change the variable in a configuration file so that the value
   overrides an existing configuration.

Changing a variable value can sometimes depend on how the value was
originally assigned and also on the desired intent of the change. In
particular, when you append a value to a variable that has a default
value, the resulting value might not be what you expect. In this case,
the value you provide might replace the value rather than append to the
default value.

If after you have changed a variable's value and something unexplained
occurs, you can use BitBake to check the actual value of the suspect
variable. You can make these checks for both configuration and recipe
level changes:

-  For configuration changes, use the following::

      $ bitbake -e

   This
   command displays variable values after the configuration files (i.e.
   ``local.conf``, ``bblayers.conf``, ``bitbake.conf`` and so forth)
   have been parsed.

   .. note::

      Variables that are exported to the environment are preceded by the
      string "export" in the command's output.

-  To find changes to a given variable in a specific recipe, use the
   following::

      $ bitbake recipename -e | grep VARIABLENAME=\"

   This command checks to see if the variable actually makes
   it into a specific recipe.

Line Joining
------------

Outside of :ref:`functions <bitbake-user-manual/bitbake-user-manual-metadata:functions>`,
BitBake joins any line ending in
a backslash character ("\\") with the following line before parsing
statements. The most common use for the "\\" character is to split
variable assignments over multiple lines, as in the following example::

   FOO = "bar \
          baz \
          qaz"

Both the "\\" character and the newline
character that follow it are removed when joining lines. Thus, no
newline characters end up in the value of ``FOO``.

Consider this additional example where the two assignments both assign
"barbaz" to ``FOO``::

   FOO = "barbaz"
   FOO = "bar\
   baz"

.. note::

   BitBake does not interpret escape sequences like "\\n" in variable
   values. For these to have an effect, the value must be passed to some
   utility that interprets escape sequences, such as
   ``printf`` or ``echo -n``.

Variable Expansion
------------------

Variables can reference the contents of other variables using a syntax
that is similar to variable expansion in Bourne shells. The following
assignments result in A containing "aval" and B evaluating to
"preavalpost". ::

   A = "aval"
   B = "pre${A}post"

.. note::

   Unlike in Bourne shells, the curly braces are mandatory: Only ``${FOO}`` and not
   ``$FOO`` is recognized as an expansion of ``FOO``.

The "=" operator does not immediately expand variable references in the
right-hand side. Instead, expansion is deferred until the variable
assigned to is actually used. The result depends on the current values
of the referenced variables. The following example should clarify this
behavior::

   A = "${B} baz"
   B = "${C} bar"
   C = "foo"
   *At this point, ${A} equals "foo bar baz"*
   C = "qux"
   *At this point, ${A} equals "qux bar baz"*
   B = "norf"
   *At this point, ${A} equals "norf baz"*

Contrast this behavior with the
:ref:`bitbake-user-manual/bitbake-user-manual-metadata:immediate variable
expansion (:=)` operator.

If the variable expansion syntax is used on a variable that does not
exist, the string is kept as is. For example, given the following
assignment, ``BAR`` expands to the literal string "${FOO}" as long as
``FOO`` does not exist. ::

   BAR = "${FOO}"

Setting a default value (?=)
----------------------------

You can use the "?=" operator to achieve a "softer" assignment for a
variable. This type of assignment allows you to define a variable if it
is undefined when the statement is parsed, but to leave the value alone
if the variable has a value. Here is an example::

   A ?= "aval"

If ``A`` is
set at the time this statement is parsed, the variable retains its
value. However, if ``A`` is not set, the variable is set to "aval".

.. note::

   This assignment is immediate. Consequently, if multiple "?="
   assignments to a single variable exist, the first of those ends up
   getting used.

Setting a weak default value (??=)
----------------------------------

The weak default value of a variable is the value which that variable
will expand to if no value has been assigned to it via any of the other
assignment operators. The "??=" operator takes effect immediately, replacing
any previously defined weak default value. Here is an example::

   W ??= "x"
   A := "${W}" # Immediate variable expansion
   W ??= "y"
   B := "${W}" # Immediate variable expansion
   W ??= "z"
   C = "${W}"
   W ?= "i"

After parsing we will have::

   A = "x"
   B = "y"
   C = "i"
   W = "i"

Appending and prepending non-override style will not substitute the weak
default value, which means that after parsing::

   W ??= "x"
   W += "y"

we will have::

   W = " y"

On the other hand, override-style appends/prepends/removes are applied after
any active weak default value has been substituted::

   W ??= "x"
   W:append = "y"

After parsing we will have::

   W = "xy"

Immediate variable expansion (:=)
---------------------------------

The ":=" operator results in a variable's contents being expanded
immediately, rather than when the variable is actually used::

   T = "123"
   A := "test ${T}"
   T = "456"
   B := "${T} ${C}"
   C = "cval"
   C := "${C}append"

In this example, ``A`` contains "test 123", even though the final value
of :term:`T` is "456". The variable :term:`B` will end up containing "456
cvalappend". This is because references to undefined variables are
preserved as is during (immediate)expansion. This is in contrast to GNU
Make, where undefined variables expand to nothing. The variable ``C``
contains "cvalappend" since ``${C}`` immediately expands to "cval".

.. _appending-and-prepending:

Appending (+=) and prepending (=+) With Spaces
----------------------------------------------

Appending and prepending values is common and can be accomplished using
the "+=" and "=+" operators. These operators insert a space between the
current value and prepended or appended value.

These operators take immediate effect during parsing. Here are some
examples::

   B = "bval"
   B += "additionaldata"
   C = "cval"
   C =+ "test"

The variable :term:`B` contains "bval additionaldata" and ``C`` contains "test
cval".

.. _appending-and-prepending-without-spaces:

Appending (.=) and Prepending (=.) Without Spaces
-------------------------------------------------

If you want to append or prepend values without an inserted space, use
the ".=" and "=." operators.

These operators take immediate effect during parsing. Here are some
examples::

   B = "bval"
   B .= "additionaldata"
   C = "cval"
   C =. "test"

The variable :term:`B` contains "bvaladditionaldata" and ``C`` contains
"testcval".

Appending and Prepending (Override Style Syntax)
------------------------------------------------

You can also append and prepend a variable's value using an override
style syntax. When you use this syntax, no spaces are inserted.

These operators differ from the ":=", ".=", "=.", "+=", and "=+"
operators in that their effects are applied at variable expansion time
rather than being immediately applied. Here are some examples::

   B = "bval"
   B:append = " additional data"
   C = "cval"
   C:prepend = "additional data "
   D = "dval"
   D:append = "additional data"

The variable :term:`B`
becomes "bval additional data" and ``C`` becomes "additional data cval".
The variable ``D`` becomes "dvaladditional data".

.. note::

   You must control all spacing when you use the override syntax.

.. note::

   The overrides are applied in this order, ":append", ":prepend", ":remove".

It is also possible to append and prepend to shell functions and
BitBake-style Python functions. See the ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:shell functions`" and ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:bitbake-style python functions`"
sections for examples.

.. _removing-override-style-syntax:

Removal (Override Style Syntax)
-------------------------------

You can remove values from lists using the removal override style
syntax. Specifying a value for removal causes all occurrences of that
value to be removed from the variable. Unlike ":append" and ":prepend",
there is no need to add a leading or trailing space to the value.

When you use this syntax, BitBake expects one or more strings.
Surrounding spaces and spacing are preserved. Here is an example::

   FOO = "123 456 789 123456 123 456 123 456"
   FOO:remove = "123"
   FOO:remove = "456"
   FOO2 = " abc def ghi abcdef abc def abc def def"
   FOO2:remove = "\
       def \
       abc \
       ghi \
       "

The variable ``FOO`` becomes
"  789 123456    " and ``FOO2`` becomes "    abcdef     ".

Like ":append" and ":prepend", ":remove" is applied at variable
expansion time.

.. note::

   The overrides are applied in this order, ":append", ":prepend", ":remove".
   This implies it is not possible to re-append previously removed strings.
   However, one can undo a ":remove" by using an intermediate variable whose
   content is passed to the ":remove" so that modifying the intermediate
   variable equals to keeping the string in::

     FOOREMOVE = "123 456 789"
     FOO:remove = "${FOOREMOVE}"
     ...
     FOOREMOVE = "123 789"

   This expands to ``FOO:remove = "123 789"``.

.. note::

   Override application order may not match variable parse history, i.e.
   the output of ``bitbake -e`` may contain ":remove" before ":append",
   but the result will be removed string, because ":remove" is handled
   last.

Override Style Operation Advantages
-----------------------------------

An advantage of the override style operations ":append", ":prepend", and
":remove" as compared to the "+=" and "=+" operators is that the
override style operators provide guaranteed operations. For example,
consider a class ``foo.bbclass`` that needs to add the value "val" to
the variable ``FOO``, and a recipe that uses ``foo.bbclass`` as follows::

   inherit foo
   FOO = "initial"

If ``foo.bbclass`` uses the "+=" operator,
as follows, then the final value of ``FOO`` will be "initial", which is
not what is desired::

   FOO += "val"

If, on the other hand, ``foo.bbclass``
uses the ":append" operator, then the final value of ``FOO`` will be
"initial val", as intended::

   FOO:append = " val"

.. note::

   It is never necessary to use "+=" together with ":append". The following
   sequence of assignments appends "barbaz" to FOO::

       FOO:append = "bar"
       FOO:append = "baz"


   The only effect of changing the second assignment in the previous
   example to use "+=" would be to add a space before "baz" in the
   appended value (due to how the "+=" operator works).

Another advantage of the override style operations is that you can
combine them with other overrides as described in the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:conditional syntax (overrides)`" section.

Variable Flag Syntax
--------------------

Variable flags are BitBake's implementation of variable properties or
attributes. It is a way of tagging extra information onto a variable.
You can find more out about variable flags in general in the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:variable flags`" section.

You can define, append, and prepend values to variable flags. All the
standard syntax operations previously mentioned work for variable flags
except for override style syntax (i.e. ":prepend", ":append", and
":remove").

Here are some examples showing how to set variable flags::

   FOO[a] = "abc"
   FOO[b] = "123"
   FOO[a] += "456"

The variable ``FOO`` has two flags:
``[a]`` and ``[b]``. The flags are immediately set to "abc" and "123",
respectively. The ``[a]`` flag becomes "abc 456".

No need exists to pre-define variable flags. You can simply start using
them. One extremely common application is to attach some brief
documentation to a BitBake variable as follows::

   CACHE[doc] = "The directory holding the cache of the metadata."

.. note::

   Variable flag names starting with an underscore (``_``) character
   are allowed but are ignored by ``d.getVarFlags("VAR")``
   in Python code. Such flag names are used internally by BitBake.

Inline Python Variable Expansion
--------------------------------

You can use inline Python variable expansion to set variables. Here is
an example::

   DATE = "${@time.strftime('%Y%m%d',time.gmtime())}"

This example results in the ``DATE`` variable being set to the current date.

Probably the most common use of this feature is to extract the value of
variables from BitBake's internal data dictionary, ``d``. The following
lines select the values of a package name and its version number,
respectively::

   PN = "${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[0] or 'defaultpkgname'}"
   PV = "${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1] or '1.0'}"

.. note::

   Inline Python expressions work just like variable expansions insofar as the
   "=" and ":=" operators are concerned. Given the following assignment, foo()
   is called each time FOO is expanded::

      FOO = "${@foo()}"

   Contrast this with the following immediate assignment, where foo() is only
   called once, while the assignment is parsed::

      FOO := "${@foo()}"

For a different way to set variables with Python code during parsing,
see the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:anonymous python functions`" section.

Unsetting variables
-------------------

It is possible to completely remove a variable or a variable flag from
BitBake's internal data dictionary by using the "unset" keyword. Here is
an example::

   unset DATE
   unset do_fetch[noexec]

These two statements remove the ``DATE`` and the ``do_fetch[noexec]`` flag.

Providing Pathnames
-------------------

When specifying pathnames for use with BitBake, do not use the tilde
("~") character as a shortcut for your home directory. Doing so might
cause BitBake to not recognize the path since BitBake does not expand
this character in the same way a shell would.

Instead, provide a fuller path as the following example illustrates::

   BBLAYERS ?= " \
       /home/scott-lenovo/LayerA \
   "

Exporting Variables to the Environment
======================================

You can export variables to the environment of running tasks by using
the ``export`` keyword. For example, in the following example, the
``do_foo`` task prints "value from the environment" when run::

   export ENV_VARIABLE
   ENV_VARIABLE = "value from the environment"

   do_foo() {
       bbplain "$ENV_VARIABLE"
   }

.. note::

   BitBake does not expand ``$ENV_VARIABLE`` in this case because it lacks the
   obligatory ``{}`` . Rather, ``$ENV_VARIABLE`` is expanded by the shell.

It does not matter whether ``export ENV_VARIABLE`` appears before or
after assignments to ``ENV_VARIABLE``.

It is also possible to combine ``export`` with setting a value for the
variable. Here is an example::

   export ENV_VARIABLE = "variable-value"

In the output of ``bitbake -e``, variables that are exported to the
environment are preceded by "export".

Among the variables commonly exported to the environment are ``CC`` and
``CFLAGS``, which are picked up by many build systems.

Conditional Syntax (Overrides)
==============================

BitBake uses :term:`OVERRIDES` to control what
variables are overridden after BitBake parses recipes and configuration
files. This section describes how you can use :term:`OVERRIDES` as
conditional metadata, talks about key expansion in relationship to
:term:`OVERRIDES`, and provides some examples to help with understanding.

Conditional Metadata
--------------------

You can use :term:`OVERRIDES` to conditionally select a specific version of
a variable and to conditionally append or prepend the value of a
variable.

.. note::

   Overrides can only use lower-case characters, digits and dashes.
   In particular, colons are not permitted in override names as they are used to
   separate overrides from each other and from the variable name.

-  *Selecting a Variable:* The :term:`OVERRIDES` variable is a
   colon-character-separated list that contains items for which you want
   to satisfy conditions. Thus, if you have a variable that is
   conditional on "arm", and "arm" is in :term:`OVERRIDES`, then the
   "arm"-specific version of the variable is used rather than the
   non-conditional version. Here is an example::

      OVERRIDES = "architecture:os:machine"
      TEST = "default"
      TEST:os = "osspecific"
      TEST:nooverride = "othercondvalue"

   In this example, the :term:`OVERRIDES`
   variable lists three overrides: "architecture", "os", and "machine".
   The variable ``TEST`` by itself has a default value of "default". You
   select the os-specific version of the ``TEST`` variable by appending
   the "os" override to the variable (i.e. ``TEST:os``).

   To better understand this, consider a practical example that assumes
   an OpenEmbedded metadata-based Linux kernel recipe file. The
   following lines from the recipe file first set the kernel branch
   variable ``KBRANCH`` to a default value, then conditionally override
   that value based on the architecture of the build::

      KBRANCH = "standard/base"
      KBRANCH:qemuarm = "standard/arm-versatile-926ejs"
      KBRANCH:qemumips = "standard/mti-malta32"
      KBRANCH:qemuppc = "standard/qemuppc"
      KBRANCH:qemux86 = "standard/common-pc/base"
      KBRANCH:qemux86-64 = "standard/common-pc-64/base"
      KBRANCH:qemumips64 = "standard/mti-malta64"

-  *Appending and Prepending:* BitBake also supports append and prepend
   operations to variable values based on whether a specific item is
   listed in :term:`OVERRIDES`. Here is an example::

      DEPENDS = "glibc ncurses"
      OVERRIDES = "machine:local"
      DEPENDS:append:machine = "libmad"

   In this example, :term:`DEPENDS` becomes "glibc ncurses libmad".

   Again, using an OpenEmbedded metadata-based kernel recipe file as an
   example, the following lines will conditionally append to the
   ``KERNEL_FEATURES`` variable based on the architecture::

      KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
      KERNEL_FEATURES:append:qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
      KERNEL_FEATURES:append:qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"

-  *Setting a Variable for a Single Task:* BitBake supports setting a
   variable just for the duration of a single task. Here is an example::

      FOO:task-configure = "val 1"
      FOO:task-compile = "val 2"

   In the
   previous example, ``FOO`` has the value "val 1" while the
   ``do_configure`` task is executed, and the value "val 2" while the
   ``do_compile`` task is executed.

   Internally, this is implemented by prepending the task (e.g.
   "task-compile:") to the value of
   :term:`OVERRIDES` for the local datastore of the
   ``do_compile`` task.

   You can also use this syntax with other combinations (e.g.
   "``:prepend``") as shown in the following example::

      EXTRA_OEMAKE:prepend:task-compile = "${PARALLEL_MAKE} "

.. note::

   Before BitBake 1.52 (Honister 3.4), the syntax for :term:`OVERRIDES`
   used ``_`` instead of ``:``, so you will still find a lot of documentation
   using ``_append``, ``_prepend``, and ``_remove``, for example.

   For details, see the
   :yocto_docs:`Overrides Syntax Changes </migration-guides/migration-3.4.html#override-syntax-changes>`
   section in the Yocto Project manual migration notes.

Key Expansion
-------------

Key expansion happens when the BitBake datastore is finalized. To better
understand this, consider the following example::

   A${B} = "X"
   B = "2"
   A2 = "Y"

In this case, after all the parsing is complete, BitBake expands
``${B}`` into "2". This expansion causes ``A2``, which was set to "Y"
before the expansion, to become "X".

.. _variable-interaction-worked-examples:

Examples
--------

Despite the previous explanations that show the different forms of
variable definitions, it can be hard to work out exactly what happens
when variable operators, conditional overrides, and unconditional
overrides are combined. This section presents some common scenarios
along with explanations for variable interactions that typically confuse
users.

There is often confusion concerning the order in which overrides and
various "append" operators take effect. Recall that an append or prepend
operation using ":append" and ":prepend" does not result in an immediate
assignment as would "+=", ".=", "=+", or "=.". Consider the following
example::

   OVERRIDES = "foo"
   A = "Z"
   A:foo:append = "X"

For this case,
``A`` is unconditionally set to "Z" and "X" is unconditionally and
immediately appended to the variable ``A:foo``. Because overrides have
not been applied yet, ``A:foo`` is set to "X" due to the append and
``A`` simply equals "Z".

Applying overrides, however, changes things. Since "foo" is listed in
:term:`OVERRIDES`, the conditional variable ``A`` is replaced with the "foo"
version, which is equal to "X". So effectively, ``A:foo`` replaces
``A``.

This next example changes the order of the override and the append::

   OVERRIDES = "foo"
   A = "Z"
   A:append:foo = "X"

For this case, before
overrides are handled, ``A`` is set to "Z" and ``A:append:foo`` is set
to "X". Once the override for "foo" is applied, however, ``A`` gets
appended with "X". Consequently, ``A`` becomes "ZX". Notice that spaces
are not appended.

This next example has the order of the appends and overrides reversed
back as in the first example::

   OVERRIDES = "foo"
   A = "Y"
   A:foo:append = "Z"
   A:foo:append = "X"

For this case, before any overrides are resolved,
``A`` is set to "Y" using an immediate assignment. After this immediate
assignment, ``A:foo`` is set to "Z", and then further appended with "X"
leaving the variable set to "ZX". Finally, applying the override for
"foo" results in the conditional variable ``A`` becoming "ZX" (i.e.
``A`` is replaced with ``A:foo``).

This final example mixes in some varying operators::

   A = "1"
   A:append = "2"
   A:append = "3"
   A += "4"
   A .= "5"

For this case, the type of append
operators are affecting the order of assignments as BitBake passes
through the code multiple times. Initially, ``A`` is set to "1 45"
because of the three statements that use immediate operators. After
these assignments are made, BitBake applies the ":append" operations.
Those operations result in ``A`` becoming "1 4523".

Sharing Functionality
=====================

BitBake allows for metadata sharing through include files (``.inc``) and
class files (``.bbclass``). For example, suppose you have a piece of
common functionality such as a task definition that you want to share
between more than one recipe. In this case, creating a ``.bbclass`` file
that contains the common functionality and then using the ``inherit``
directive in your recipes to inherit the class would be a common way to
share the task.

This section presents the mechanisms BitBake provides to allow you to
share functionality between recipes. Specifically, the mechanisms
include ``include``, ``inherit``, :term:`INHERIT`, and ``require``
directives.

Locating Include and Class Files
--------------------------------

BitBake uses the :term:`BBPATH` variable to locate
needed include and class files. Additionally, BitBake searches the
current directory for ``include`` and ``require`` directives.

.. note::

   The BBPATH variable is analogous to the environment variable PATH .

In order for include and class files to be found by BitBake, they need
to be located in a "classes" subdirectory that can be found in
:term:`BBPATH`.

``inherit`` Directive
---------------------

When writing a recipe or class file, you can use the ``inherit``
directive to inherit the functionality of a class (``.bbclass``).
BitBake only supports this directive when used within recipe and class
files (i.e. ``.bb`` and ``.bbclass``).

The ``inherit`` directive is a rudimentary means of specifying
functionality contained in class files that your recipes require. For
example, you can easily abstract out the tasks involved in building a
package that uses Autoconf and Automake and put those tasks into a class
file and then have your recipe inherit that class file.

As an example, your recipes could use the following directive to inherit
an ``autotools.bbclass`` file. The class file would contain common
functionality for using Autotools that could be shared across recipes::

   inherit autotools

In this case, BitBake would search for the directory
``classes/autotools.bbclass`` in :term:`BBPATH`.

.. note::

   You can override any values and functions of the inherited class
   within your recipe by doing so after the "inherit" statement.

If you want to use the directive to inherit multiple classes, separate
them with spaces. The following example shows how to inherit both the
``buildhistory`` and ``rm_work`` classes::

   inherit buildhistory rm_work

An advantage with the inherit directive as compared to both the
:ref:`include <bitbake-user-manual/bitbake-user-manual-metadata:\`\`include\`\` directive>` and :ref:`require <bitbake-user-manual/bitbake-user-manual-metadata:\`\`require\`\` directive>`
directives is that you can inherit class files conditionally. You can
accomplish this by using a variable expression after the ``inherit``
statement. Here is an example::

   inherit ${VARNAME}

If ``VARNAME`` is
going to be set, it needs to be set before the ``inherit`` statement is
parsed. One way to achieve a conditional inherit in this case is to use
overrides::

   VARIABLE = ""
   VARIABLE:someoverride = "myclass"

Another method is by using anonymous Python. Here is an example::

   python () {
       if condition == value:
           d.setVar('VARIABLE', 'myclass')
       else:
           d.setVar('VARIABLE', '')
   }

Alternatively, you could use an in-line Python expression in the
following form::

   inherit ${@'classname' if condition else ''}
   inherit ${@functionname(params)}

In all cases, if the expression evaluates to an
empty string, the statement does not trigger a syntax error because it
becomes a no-op.

``include`` Directive
---------------------

BitBake understands the ``include`` directive. This directive causes
BitBake to parse whatever file you specify, and to insert that file at
that location. The directive is much like its equivalent in Make except
that if the path specified on the include line is a relative path,
BitBake locates the first file it can find within :term:`BBPATH`.

The include directive is a more generic method of including
functionality as compared to the :ref:`inherit <bitbake-user-manual/bitbake-user-manual-metadata:\`\`inherit\`\` directive>`
directive, which is restricted to class (i.e. ``.bbclass``) files. The
include directive is applicable for any other kind of shared or
encapsulated functionality or configuration that does not suit a
``.bbclass`` file.

As an example, suppose you needed a recipe to include some self-test
definitions::

   include test_defs.inc

.. note::

   The include directive does not produce an error when the file cannot be
   found.  Consequently, it is recommended that if the file you are including is
   expected to exist, you should use :ref:`require <require-inclusion>` instead
   of include . Doing so makes sure that an error is produced if the file cannot
   be found.

.. _require-inclusion:

``require`` Directive
---------------------

BitBake understands the ``require`` directive. This directive behaves
just like the ``include`` directive with the exception that BitBake
raises a parsing error if the file to be included cannot be found. Thus,
any file you require is inserted into the file that is being parsed at
the location of the directive.

The require directive, like the include directive previously described,
is a more generic method of including functionality as compared to the
:ref:`inherit <bitbake-user-manual/bitbake-user-manual-metadata:\`\`inherit\`\` directive>` directive, which is restricted to class
(i.e. ``.bbclass``) files. The require directive is applicable for any
other kind of shared or encapsulated functionality or configuration that
does not suit a ``.bbclass`` file.

Similar to how BitBake handles :ref:`include <bitbake-user-manual/bitbake-user-manual-metadata:\`\`include\`\` directive>`, if
the path specified on the require line is a relative path, BitBake
locates the first file it can find within :term:`BBPATH`.

As an example, suppose you have two versions of a recipe (e.g.
``foo_1.2.2.bb`` and ``foo_2.0.0.bb``) where each version contains some
identical functionality that could be shared. You could create an
include file named ``foo.inc`` that contains the common definitions
needed to build "foo". You need to be sure ``foo.inc`` is located in the
same directory as your two recipe files as well. Once these conditions
are set up, you can share the functionality using a ``require``
directive from within each recipe::

   require foo.inc

``INHERIT`` Configuration Directive
-----------------------------------

When creating a configuration file (``.conf``), you can use the
:term:`INHERIT` configuration directive to inherit a
class. BitBake only supports this directive when used within a
configuration file.

As an example, suppose you needed to inherit a class file called
``abc.bbclass`` from a configuration file as follows::

   INHERIT += "abc"

This configuration directive causes the named class to be inherited at
the point of the directive during parsing. As with the ``inherit``
directive, the ``.bbclass`` file must be located in a "classes"
subdirectory in one of the directories specified in :term:`BBPATH`.

.. note::

   Because .conf files are parsed first during BitBake's execution, using
   INHERIT to inherit a class effectively inherits the class globally (i.e. for
   all recipes).

If you want to use the directive to inherit multiple classes, you can
provide them on the same line in the ``local.conf`` file. Use spaces to
separate the classes. The following example shows how to inherit both
the ``autotools`` and ``pkgconfig`` classes::

   INHERIT += "autotools pkgconfig"

Functions
=========

As with most languages, functions are the building blocks that are used
to build up operations into tasks. BitBake supports these types of
functions:

-  *Shell Functions:* Functions written in shell script and executed
   either directly as functions, tasks, or both. They can also be called
   by other shell functions.

-  *BitBake-Style Python Functions:* Functions written in Python and
   executed by BitBake or other Python functions using
   ``bb.build.exec_func()``.

-  *Python Functions:* Functions written in Python and executed by
   Python.

-  *Anonymous Python Functions:* Python functions executed automatically
   during parsing.

Regardless of the type of function, you can only define them in class
(``.bbclass``) and recipe (``.bb`` or ``.inc``) files.

Shell Functions
---------------

Functions written in shell script are executed either directly as
functions, tasks, or both. They can also be called by other shell
functions. Here is an example shell function definition::

   some_function () {
       echo "Hello World"
   }

When you create these types of functions in
your recipe or class files, you need to follow the shell programming
rules. The scripts are executed by ``/bin/sh``, which may not be a bash
shell but might be something such as ``dash``. You should not use
Bash-specific script (bashisms).

Overrides and override-style operators like ``:append`` and ``:prepend``
can also be applied to shell functions. Most commonly, this application
would be used in a ``.bbappend`` file to modify functions in the main
recipe. It can also be used to modify functions inherited from classes.

As an example, consider the following::

   do_foo() {
       bbplain first
       fn
   }

   fn:prepend() {
       bbplain second
   }

   fn() {
       bbplain third
   }

   do_foo:append() {
       bbplain fourth
   }

Running ``do_foo`` prints the following::

   recipename do_foo: first
   recipename do_foo: second
   recipename do_foo: third
   recipename do_foo: fourth

.. note::

   Overrides and override-style operators can be applied to any shell
   function, not just :ref:`tasks <bitbake-user-manual/bitbake-user-manual-metadata:tasks>`.

You can use the ``bitbake -e recipename`` command to view the final
assembled function after all overrides have been applied.

BitBake-Style Python Functions
------------------------------

These functions are written in Python and executed by BitBake or other
Python functions using ``bb.build.exec_func()``.

An example BitBake function is::

   python some_python_function () {
       d.setVar("TEXT", "Hello World")
       print d.getVar("TEXT")
   }

Because the
Python "bb" and "os" modules are already imported, you do not need to
import these modules. Also in these types of functions, the datastore
("d") is a global variable and is always automatically available.

.. note::

   Variable expressions (e.g.  ``${X}`` ) are no longer expanded within Python
   functions. This behavior is intentional in order to allow you to freely set
   variable values to expandable expressions without having them expanded
   prematurely. If you do wish to expand a variable within a Python function,
   use ``d.getVar("X")`` . Or, for more complicated expressions, use ``d.expand()``.

Similar to shell functions, you can also apply overrides and
override-style operators to BitBake-style Python functions.

As an example, consider the following::

   python do_foo:prepend() {
       bb.plain("first")
   }

   python do_foo() {
       bb.plain("second")
   }

   python do_foo:append() {
       bb.plain("third")
   }

Running ``do_foo`` prints the following::

   recipename do_foo: first
   recipename do_foo: second
   recipename do_foo: third

You can use the ``bitbake -e recipename`` command to view
the final assembled function after all overrides have been applied.

Python Functions
----------------

These functions are written in Python and are executed by other Python
code. Examples of Python functions are utility functions that you intend
to call from in-line Python or from within other Python functions. Here
is an example::

   def get_depends(d):
       if d.getVar('SOMECONDITION'):
           return "dependencywithcond"
       else:
           return "dependency"

   SOMECONDITION = "1"
   DEPENDS = "${@get_depends(d)}"

This would result in :term:`DEPENDS` containing ``dependencywithcond``.

Here are some things to know about Python functions:

-  Python functions can take parameters.

-  The BitBake datastore is not automatically available. Consequently,
   you must pass it in as a parameter to the function.

-  The "bb" and "os" Python modules are automatically available. You do
   not need to import them.

BitBake-Style Python Functions Versus Python Functions
------------------------------------------------------

Following are some important differences between BitBake-style Python
functions and regular Python functions defined with "def":

-  Only BitBake-style Python functions can be :ref:`tasks <bitbake-user-manual/bitbake-user-manual-metadata:tasks>`.

-  Overrides and override-style operators can only be applied to
   BitBake-style Python functions.

-  Only regular Python functions can take arguments and return values.

-  :ref:`Variable flags <bitbake-user-manual/bitbake-user-manual-metadata:variable flags>` such as
   ``[dirs]``, ``[cleandirs]``, and ``[lockfiles]`` can be used on BitBake-style
   Python functions, but not on regular Python functions.

-  BitBake-style Python functions generate a separate
   ``${``\ :term:`T`\ ``}/run.``\ function-name\ ``.``\ pid
   script that is executed to run the function, and also generate a log
   file in ``${T}/log.``\ function-name\ ``.``\ pid if they are executed
   as tasks.

   Regular Python functions execute "inline" and do not generate any
   files in ``${T}``.

-  Regular Python functions are called with the usual Python syntax.
   BitBake-style Python functions are usually tasks and are called
   directly by BitBake, but can also be called manually from Python code
   by using the ``bb.build.exec_func()`` function. Here is an example::

      bb.build.exec_func("my_bitbake_style_function", d)

   .. note::

      ``bb.build.exec_func()`` can also be used to run shell functions from Python
      code. If you want to run a shell function before a Python function within
      the same task, then you can use a parent helper Python function that
      starts by running the shell function with ``bb.build.exec_func()`` and then
      runs the Python code.

   To detect errors from functions executed with
   ``bb.build.exec_func()``, you can catch the ``bb.build.FuncFailed``
   exception.

   .. note::

      Functions in metadata (recipes and classes) should not themselves raise
      ``bb.build.FuncFailed``. Rather, ``bb.build.FuncFailed`` should be viewed as a
      general indicator that the called function failed by raising an
      exception. For example, an exception raised by ``bb.fatal()`` will be caught
      inside ``bb.build.exec_func()``, and a ``bb.build.FuncFailed`` will be raised in
      response.

Due to their simplicity, you should prefer regular Python functions over
BitBake-style Python functions unless you need a feature specific to
BitBake-style Python functions. Regular Python functions in metadata are
a more recent invention than BitBake-style Python functions, and older
code tends to use ``bb.build.exec_func()`` more often.

Anonymous Python Functions
--------------------------

Sometimes it is useful to set variables or perform other operations
programmatically during parsing. To do this, you can define special
Python functions, called anonymous Python functions, that run at the end
of parsing. For example, the following conditionally sets a variable
based on the value of another variable::

   python () {
       if d.getVar('SOMEVAR') == 'value':
           d.setVar('ANOTHERVAR', 'value2')
   }

An equivalent way to mark a function as an anonymous function is to give it
the name "__anonymous", rather than no name.

Anonymous Python functions always run at the end of parsing, regardless
of where they are defined. If a recipe contains many anonymous
functions, they run in the same order as they are defined within the
recipe. As an example, consider the following snippet::

   python () {
       d.setVar('FOO', 'foo 2')
   }

   FOO = "foo 1"

   python () {
       d.appendVar('BAR',' bar 2')
   }

   BAR = "bar 1"

The previous example is conceptually
equivalent to the following snippet::

   FOO = "foo 1"
   BAR = "bar 1"
   FOO = "foo 2"
   BAR += "bar 2"

``FOO`` ends up with the value "foo 2", and
``BAR`` with the value "bar 1 bar 2". Just as in the second snippet, the
values set for the variables within the anonymous functions become
available to tasks, which always run after parsing.

Overrides and override-style operators such as "``:append``" are applied
before anonymous functions run. In the following example, ``FOO`` ends
up with the value "foo from anonymous"::

   FOO = "foo"
   FOO:append = " from outside"

   python () {
       d.setVar("FOO", "foo from anonymous")
   }

For methods
you can use with anonymous Python functions, see the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:functions you can call from within python`"
section. For a different method to run Python code during parsing, see
the ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:inline python variable expansion`" section.

Flexible Inheritance for Class Functions
----------------------------------------

Through coding techniques and the use of ``EXPORT_FUNCTIONS``, BitBake
supports exporting a function from a class such that the class function
appears as the default implementation of the function, but can still be
called if a recipe inheriting the class needs to define its own version
of the function.

To understand the benefits of this feature, consider the basic scenario
where a class defines a task function and your recipe inherits the
class. In this basic scenario, your recipe inherits the task function as
defined in the class. If desired, your recipe can add to the start and
end of the function by using the ":prepend" or ":append" operations
respectively, or it can redefine the function completely. However, if it
redefines the function, there is no means for it to call the class
version of the function. ``EXPORT_FUNCTIONS`` provides a mechanism that
enables the recipe's version of the function to call the original
version of the function.

To make use of this technique, you need the following things in place:

-  The class needs to define the function as follows::

      classname_functionname

   For example, if you have a class file
   ``bar.bbclass`` and a function named ``do_foo``, the class must
   define the function as follows::

      bar_do_foo

-  The class needs to contain the ``EXPORT_FUNCTIONS`` statement as
   follows::

      EXPORT_FUNCTIONS functionname

   For example, continuing with
   the same example, the statement in the ``bar.bbclass`` would be as
   follows::

      EXPORT_FUNCTIONS do_foo

-  You need to call the function appropriately from within your recipe.
   Continuing with the same example, if your recipe needs to call the
   class version of the function, it should call ``bar_do_foo``.
   Assuming ``do_foo`` was a shell function and ``EXPORT_FUNCTIONS`` was
   used as above, the recipe's function could conditionally call the
   class version of the function as follows::

      do_foo() {
          if [ somecondition ] ; then
              bar_do_foo
          else
              # Do something else
          fi
      }

   To call your modified version of the function as defined in your recipe,
   call it as ``do_foo``.

With these conditions met, your single recipe can freely choose between
the original function as defined in the class file and the modified
function in your recipe. If you do not set up these conditions, you are
limited to using one function or the other.

Tasks
=====

Tasks are BitBake execution units that make up the steps that BitBake
can run for a given recipe. Tasks are only supported in recipes and
classes (i.e. in ``.bb`` files and files included or inherited from
``.bb`` files). By convention, tasks have names that start with "do\_".

Promoting a Function to a Task
------------------------------

Tasks are either :ref:`shell functions <bitbake-user-manual/bitbake-user-manual-metadata:shell functions>` or
:ref:`BitBake-style Python functions <bitbake-user-manual/bitbake-user-manual-metadata:bitbake-style python functions>`
that have been promoted to tasks by using the ``addtask`` command. The
``addtask`` command can also optionally describe dependencies between
the task and other tasks. Here is an example that shows how to define a
task and declare some dependencies::

   python do_printdate () {
       import time
       print time.strftime('%Y%m%d', time.gmtime())
   }
   addtask printdate after do_fetch before do_build

The first argument to ``addtask`` is the name
of the function to promote to a task. If the name does not start with
"do\_", "do\_" is implicitly added, which enforces the convention that all
task names start with "do\_".

In the previous example, the ``do_printdate`` task becomes a dependency
of the ``do_build`` task, which is the default task (i.e. the task run
by the ``bitbake`` command unless another task is specified explicitly).
Additionally, the ``do_printdate`` task becomes dependent upon the
``do_fetch`` task. Running the ``do_build`` task results in the
``do_printdate`` task running first.

.. note::

   If you try out the previous example, you might see that the
   ``do_printdate``
   task is only run the first time you build the recipe with the
   ``bitbake``
   command. This is because BitBake considers the task "up-to-date"
   after that initial run. If you want to force the task to always be
   rerun for experimentation purposes, you can make BitBake always
   consider the task "out-of-date" by using the
   :ref:`[nostamp] <bitbake-user-manual/bitbake-user-manual-metadata:Variable Flags>`
   variable flag, as follows::

      do_printdate[nostamp] = "1"

   You can also explicitly run the task and provide the
   -f option as follows::

      $ bitbake recipe -c printdate -f

   When manually selecting a task to run with the bitbake ``recipe
   -c task`` command, you can omit the "do\_" prefix as part of the task
   name.

You might wonder about the practical effects of using ``addtask``
without specifying any dependencies as is done in the following example::

   addtask printdate

In this example, assuming dependencies have not been
added through some other means, the only way to run the task is by
explicitly selecting it with ``bitbake`` recipe ``-c printdate``. You
can use the ``do_listtasks`` task to list all tasks defined in a recipe
as shown in the following example::

   $ bitbake recipe -c listtasks

For more information on task dependencies, see the
":ref:`bitbake-user-manual/bitbake-user-manual-execution:dependencies`" section.

See the ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:variable flags`" section for information
on variable flags you can use with tasks.

.. note::

   While it's infrequent, it's possible to define multiple tasks as
   dependencies when calling ``addtask``. For example, here's a snippet
   from the OpenEmbedded class file ``package_tar.bbclass``::

     addtask package_write_tar before do_build after do_packagedata do_package

   Note how the ``package_write_tar`` task has to wait until both of
   ``do_packagedata`` and ``do_package`` complete.

Deleting a Task
---------------

As well as being able to add tasks, you can delete them. Simply use the
``deltask`` command to delete a task. For example, to delete the example
task used in the previous sections, you would use::

   deltask printdate

If you delete a task using the ``deltask`` command and the task has
dependencies, the dependencies are not reconnected. For example, suppose
you have three tasks named ``do_a``, ``do_b``, and ``do_c``.
Furthermore, ``do_c`` is dependent on ``do_b``, which in turn is
dependent on ``do_a``. Given this scenario, if you use ``deltask`` to
delete ``do_b``, the implicit dependency relationship between ``do_c``
and ``do_a`` through ``do_b`` no longer exists, and ``do_c``
dependencies are not updated to include ``do_a``. Thus, ``do_c`` is free
to run before ``do_a``.

If you want dependencies such as these to remain intact, use the
``[noexec]`` varflag to disable the task instead of using the
``deltask`` command to delete it::

   do_b[noexec] = "1"

Passing Information Into the Build Task Environment
---------------------------------------------------

When running a task, BitBake tightly controls the shell execution
environment of the build tasks to make sure unwanted contamination from
the build machine cannot influence the build.

.. note::

   By default, BitBake cleans the environment to include only those
   things exported or listed in its passthrough list to ensure that the
   build environment is reproducible and consistent. You can prevent this
   "cleaning" by setting the :term:`BB_PRESERVE_ENV` variable.

Consequently, if you do want something to get passed into the build task
environment, you must take these two steps:

#. Tell BitBake to load what you want from the environment into the
   datastore. You can do so through the
   :term:`BB_ENV_PASSTHROUGH` and
   :term:`BB_ENV_PASSTHROUGH_ADDITIONS` variables. For
   example, assume you want to prevent the build system from accessing
   your ``$HOME/.ccache`` directory. The following command adds the
   the environment variable ``CCACHE_DIR`` to BitBake's passthrough
   list to allow that variable into the datastore::

      export BB_ENV_PASSTHROUGH_ADDITIONS="$BB_ENV_PASSTHROUGH_ADDITIONS CCACHE_DIR"

#. Tell BitBake to export what you have loaded into the datastore to the
   task environment of every running task. Loading something from the
   environment into the datastore (previous step) only makes it
   available in the datastore. To export it to the task environment of
   every running task, use a command similar to the following in your
   local configuration file ``local.conf`` or your distribution
   configuration file::

      export CCACHE_DIR

   .. note::

      A side effect of the previous steps is that BitBake records the
      variable as a dependency of the build process in things like the
      setscene checksums. If doing so results in unnecessary rebuilds of
      tasks, you can also flag the variable so that the setscene code
      ignores the dependency when it creates checksums.

Sometimes, it is useful to be able to obtain information from the
original execution environment. BitBake saves a copy of the original
environment into a special variable named :term:`BB_ORIGENV`.

The :term:`BB_ORIGENV` variable returns a datastore object that can be
queried using the standard datastore operators such as
``getVar(, False)``. The datastore object is useful, for example, to
find the original ``DISPLAY`` variable. Here is an example::

   origenv = d.getVar("BB_ORIGENV", False)
   bar = origenv.getVar("BAR", False)

The previous example returns ``BAR`` from the original execution
environment.

Variable Flags
==============

Variable flags (varflags) help control a task's functionality and
dependencies. BitBake reads and writes varflags to the datastore using
the following command forms::

   variable = d.getVarFlags("variable")
   self.d.setVarFlags("FOO", {"func": True})

When working with varflags, the same syntax, with the exception of
overrides, applies. In other words, you can set, append, and prepend
varflags just like variables. See the
":ref:`bitbake-user-manual/bitbake-user-manual-metadata:variable flag syntax`" section for details.

BitBake has a defined set of varflags available for recipes and classes.
Tasks support a number of these flags which control various
functionality of the task:

-  ``[cleandirs]``: Empty directories that should be created before
   the task runs. Directories that already exist are removed and
   recreated to empty them.

-  ``[depends]``: Controls inter-task dependencies. See the
   :term:`DEPENDS` variable and the
   ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:inter-task
   dependencies`" section for more information.

-  ``[deptask]``: Controls task build-time dependencies. See the
   :term:`DEPENDS` variable and the ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:build dependencies`" section for more information.

-  ``[dirs]``: Directories that should be created before the task
   runs. Directories that already exist are left as is. The last
   directory listed is used as the current working directory for the
   task.

- ``[file-checksums]``: Controls the file dependencies for a task. The
  baseline file list is the set of files associated with
  :term:`SRC_URI`. May be used to set additional dependencies on
  files not associated with :term:`SRC_URI`.

  The value set to the list is a file-boolean pair where the first
  value is the file name and the second is whether or not it
  physically exists on the filesystem. ::

    do_configure[file-checksums] += "${MY_DIRPATH}/my-file.txt:True"

  It is important to record any paths which the task looked at and
  which didn't exist. This means that if these do exist at a later
  time, the task can be rerun with the new additional files. The
  "exists" True or False value after the path allows this to be
  handled.

-  ``[lockfiles]``: Specifies one or more lockfiles to lock while the
   task executes. Only one task may hold a lockfile, and any task that
   attempts to lock an already locked file will block until the lock is
   released. You can use this variable flag to accomplish mutual
   exclusion.

-  ``[network]``: When set to "1", allows a task to access the network. By
   default, only the ``do_fetch`` task is granted network access. Recipes
   shouldn't access the network outside of ``do_fetch`` as it usually
   undermines fetcher source mirroring, image and licence manifests, software
   auditing and supply chain security.

-  ``[noexec]``: When set to "1", marks the task as being empty, with
   no execution required. You can use the ``[noexec]`` flag to set up
   tasks as dependency placeholders, or to disable tasks defined
   elsewhere that are not needed in a particular recipe.

-  ``[nostamp]``: When set to "1", tells BitBake to not generate a
   stamp file for a task, which implies the task should always be
   executed.

   .. caution::

      Any task that depends (possibly indirectly) on a ``[nostamp]`` task will
      always be executed as well. This can cause unnecessary rebuilding if you
      are not careful.

-  ``[number_threads]``: Limits tasks to a specific number of
   simultaneous threads during execution. This varflag is useful when
   your build host has a large number of cores but certain tasks need to
   be rate-limited due to various kinds of resource constraints (e.g. to
   avoid network throttling). ``number_threads`` works similarly to the
   :term:`BB_NUMBER_THREADS` variable but is task-specific.

   Set the value globally. For example, the following makes sure the
   ``do_fetch`` task uses no more than two simultaneous execution
   threads: do_fetch[number_threads] = "2"

   .. warning::

      -  Setting the varflag in individual recipes rather than globally
         can result in unpredictable behavior.

      -  Setting the varflag to a value greater than the value used in
         the :term:`BB_NUMBER_THREADS` variable causes ``number_threads`` to
         have no effect.

-  ``[postfuncs]``: List of functions to call after the completion of
   the task.

-  ``[prefuncs]``: List of functions to call before the task executes.

-  ``[rdepends]``: Controls inter-task runtime dependencies. See the
   :term:`RDEPENDS` variable, the
   :term:`RRECOMMENDS` variable, and the
   ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:inter-task dependencies`" section for
   more information.

-  ``[rdeptask]``: Controls task runtime dependencies. See the
   :term:`RDEPENDS` variable, the
   :term:`RRECOMMENDS` variable, and the
   ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:runtime dependencies`" section for more
   information.

-  ``[recideptask]``: When set in conjunction with ``recrdeptask``,
   specifies a task that should be inspected for additional
   dependencies.

-  ``[recrdeptask]``: Controls task recursive runtime dependencies.
   See the :term:`RDEPENDS` variable, the
   :term:`RRECOMMENDS` variable, and the
   ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:recursive dependencies`" section for
   more information.

-  ``[stamp-extra-info]``: Extra stamp information to append to the
   task's stamp. As an example, OpenEmbedded uses this flag to allow
   machine-specific tasks.

-  ``[umask]``: The umask to run the task under.

Several varflags are useful for controlling how signatures are
calculated for variables. For more information on this process, see the
":ref:`bitbake-user-manual/bitbake-user-manual-execution:checksums (signatures)`" section.

-  ``[vardeps]``: Specifies a space-separated list of additional
   variables to add to a variable's dependencies for the purposes of
   calculating its signature. Adding variables to this list is useful,
   for example, when a function refers to a variable in a manner that
   does not allow BitBake to automatically determine that the variable
   is referred to.

-  ``[vardepsexclude]``: Specifies a space-separated list of variables
   that should be excluded from a variable's dependencies for the
   purposes of calculating its signature.

-  ``[vardepvalue]``: If set, instructs BitBake to ignore the actual
   value of the variable and instead use the specified value when
   calculating the variable's signature.

-  ``[vardepvalueexclude]``: Specifies a pipe-separated list of
   strings to exclude from the variable's value when calculating the
   variable's signature.

Events
======

BitBake allows installation of event handlers within recipe and class
files. Events are triggered at certain points during operation, such as
the beginning of operation against a given recipe (i.e. ``*.bb``), the
start of a given task, a task failure, a task success, and so forth. The
intent is to make it easy to do things like email notification on build
failures.

Following is an example event handler that prints the name of the event
and the content of the :term:`FILE` variable::

   addhandler myclass_eventhandler
   python myclass_eventhandler() {
       from bb.event import getName
       print("The name of the Event is %s" % getName(e))
       print("The file we run for is %s" % d.getVar('FILE'))
   }
   myclass_eventhandler[eventmask] = "bb.event.BuildStarted
   bb.event.BuildCompleted"

In the previous example, an eventmask has been
set so that the handler only sees the "BuildStarted" and
"BuildCompleted" events. This event handler gets called every time an
event matching the eventmask is triggered. A global variable "e" is
defined, which represents the current event. With the ``getName(e)``
method, you can get the name of the triggered event. The global
datastore is available as "d". In legacy code, you might see "e.data"
used to get the datastore. However, realize that "e.data" is deprecated
and you should use "d" going forward.

The context of the datastore is appropriate to the event in question.
For example, "BuildStarted" and "BuildCompleted" events run before any
tasks are executed so would be in the global configuration datastore
namespace. No recipe-specific metadata exists in that namespace. The
"BuildStarted" and "BuildCompleted" events also run in the main
cooker/server process rather than any worker context. Thus, any changes
made to the datastore would be seen by other cooker/server events within
the current build but not seen outside of that build or in any worker
context. Task events run in the actual tasks in question consequently
have recipe-specific and task-specific contents. These events run in the
worker context and are discarded at the end of task execution.

During a standard build, the following common events might occur. The
following events are the most common kinds of events that most metadata
might have an interest in viewing:

-  ``bb.event.ConfigParsed()``: Fired when the base configuration; which
   consists of ``bitbake.conf``, ``base.bbclass`` and any global
   :term:`INHERIT` statements; has been parsed. You can see multiple such
   events when each of the workers parse the base configuration or if
   the server changes configuration and reparses. Any given datastore
   only has one such event executed against it, however. If
   :term:`BB_INVALIDCONF` is set in the datastore by the event
   handler, the configuration is reparsed and a new event triggered,
   allowing the metadata to update configuration.

-  ``bb.event.HeartbeatEvent()``: Fires at regular time intervals of one
   second. You can configure the interval time using the
   ``BB_HEARTBEAT_EVENT`` variable. The event's "time" attribute is the
   ``time.time()`` value when the event is triggered. This event is
   useful for activities such as system state monitoring.

-  ``bb.event.ParseStarted()``: Fired when BitBake is about to start
   parsing recipes. This event's "total" attribute represents the number
   of recipes BitBake plans to parse.

-  ``bb.event.ParseProgress()``: Fired as parsing progresses. This
   event's "current" attribute is the number of recipes parsed as well
   as the "total" attribute.

-  ``bb.event.ParseCompleted()``: Fired when parsing is complete. This
   event's "cached", "parsed", "skipped", "virtuals", "masked", and
   "errors" attributes provide statistics for the parsing results.

-  ``bb.event.BuildStarted()``: Fired when a new build starts. BitBake
   fires multiple "BuildStarted" events (one per configuration) when
   multiple configuration (multiconfig) is enabled.

-  ``bb.build.TaskStarted()``: Fired when a task starts. This event's
   "taskfile" attribute points to the recipe from which the task
   originates. The "taskname" attribute, which is the task's name,
   includes the ``do_`` prefix, and the "logfile" attribute point to
   where the task's output is stored. Finally, the "time" attribute is
   the task's execution start time.

-  ``bb.build.TaskInvalid()``: Fired if BitBake tries to execute a task
   that does not exist.

-  ``bb.build.TaskFailedSilent()``: Fired for setscene tasks that fail
   and should not be presented to the user verbosely.

-  ``bb.build.TaskFailed()``: Fired for normal tasks that fail.

-  ``bb.build.TaskSucceeded()``: Fired when a task successfully
   completes.

-  ``bb.event.BuildCompleted()``: Fired when a build finishes.

-  ``bb.cooker.CookerExit()``: Fired when the BitBake server/cooker
   shuts down. This event is usually only seen by the UIs as a sign they
   should also shutdown.

This next list of example events occur based on specific requests to the
server. These events are often used to communicate larger pieces of
information from the BitBake server to other parts of BitBake such as
user interfaces:

-  ``bb.event.TreeDataPreparationStarted()``
-  ``bb.event.TreeDataPreparationProgress()``
-  ``bb.event.TreeDataPreparationCompleted()``
-  ``bb.event.DepTreeGenerated()``
-  ``bb.event.CoreBaseFilesFound()``
-  ``bb.event.ConfigFilePathFound()``
-  ``bb.event.FilesMatchingFound()``
-  ``bb.event.ConfigFilesFound()``
-  ``bb.event.TargetsTreeGenerated()``

.. _variants-class-extension-mechanism:

Variants --- Class Extension Mechanism
======================================

BitBake supports multiple incarnations of a recipe file via the
:term:`BBCLASSEXTEND` variable.

The :term:`BBCLASSEXTEND` variable is a space separated list of classes used
to "extend" the recipe for each variant. Here is an example that results in a
second incarnation of the current recipe being available. This second
incarnation will have the "native" class inherited. ::

      BBCLASSEXTEND = "native"

.. note::

   The mechanism for this class extension is extremely specific to the
   implementation. Usually, the recipe's :term:`PROVIDES` , :term:`PN` , and
   :term:`DEPENDS` variables would need to be modified by the extension
   class. For specific examples, see the OE-Core native , nativesdk , and
   multilib classes.

Dependencies
============

To allow for efficient parallel processing, BitBake handles dependencies
at the task level. Dependencies can exist both between tasks within a
single recipe and between tasks in different recipes. Following are
examples of each:

-  For tasks within a single recipe, a recipe's ``do_configure`` task
   might need to complete before its ``do_compile`` task can run.

-  For tasks in different recipes, one recipe's ``do_configure`` task
   might require another recipe's ``do_populate_sysroot`` task to finish
   first such that the libraries and headers provided by the other
   recipe are available.

This section describes several ways to declare dependencies. Remember,
even though dependencies are declared in different ways, they are all
simply dependencies between tasks.

.. _dependencies-internal-to-the-bb-file:

Dependencies Internal to the ``.bb`` File
-----------------------------------------

BitBake uses the ``addtask`` directive to manage dependencies that are
internal to a given recipe file. You can use the ``addtask`` directive
to indicate when a task is dependent on other tasks or when other tasks
depend on that recipe. Here is an example::

   addtask printdate after do_fetch before do_build

In this example, the ``do_printdate`` task
depends on the completion of the ``do_fetch`` task, and the ``do_build``
task depends on the completion of the ``do_printdate`` task.

.. note::

   For a task to run, it must be a direct or indirect dependency of some
   other task that is scheduled to run.

   For illustration, here are some examples:

   -  The directive ``addtask mytask before do_configure`` causes
      ``do_mytask`` to run before ``do_configure`` runs. Be aware that
      ``do_mytask`` still only runs if its :ref:`input
      checksum <bitbake-user-manual/bitbake-user-manual-execution:checksums (signatures)>` has changed since the last time it was
      run. Changes to the input checksum of ``do_mytask`` also
      indirectly cause ``do_configure`` to run.

   -  The directive ``addtask mytask after do_configure`` by itself
      never causes ``do_mytask`` to run. ``do_mytask`` can still be run
      manually as follows::

         $ bitbake recipe -c mytask

      Declaring ``do_mytask`` as a dependency of some other task that is
      scheduled to run also causes it to run. Regardless, the task runs after
      ``do_configure``.

Build Dependencies
------------------

BitBake uses the :term:`DEPENDS` variable to manage
build time dependencies. The ``[deptask]`` varflag for tasks signifies
the task of each item listed in :term:`DEPENDS` that must complete before
that task can be executed. Here is an example::

   do_configure[deptask] = "do_populate_sysroot"

In this example, the ``do_populate_sysroot`` task
of each item in :term:`DEPENDS` must complete before ``do_configure`` can
execute.

Runtime Dependencies
--------------------

BitBake uses the :term:`PACKAGES`, :term:`RDEPENDS`, and :term:`RRECOMMENDS`
variables to manage runtime dependencies.

The :term:`PACKAGES` variable lists runtime packages. Each of those packages
can have :term:`RDEPENDS` and :term:`RRECOMMENDS` runtime dependencies. The
``[rdeptask]`` flag for tasks is used to signify the task of each item
runtime dependency which must have completed before that task can be
executed. ::

   do_package_qa[rdeptask] = "do_packagedata"

In the previous
example, the ``do_packagedata`` task of each item in :term:`RDEPENDS` must
have completed before ``do_package_qa`` can execute.
Although :term:`RDEPENDS` contains entries from the
runtime dependency namespace, BitBake knows how to map them back
to the build-time dependency namespace, in which the tasks are defined.

Recursive Dependencies
----------------------

BitBake uses the ``[recrdeptask]`` flag to manage recursive task
dependencies. BitBake looks through the build-time and runtime
dependencies of the current recipe, looks through the task's inter-task
dependencies, and then adds dependencies for the listed task. Once
BitBake has accomplished this, it recursively works through the
dependencies of those tasks. Iterative passes continue until all
dependencies are discovered and added.

The ``[recrdeptask]`` flag is most commonly used in high-level recipes
that need to wait for some task to finish "globally". For example,
``image.bbclass`` has the following::

   do_rootfs[recrdeptask] += "do_packagedata"

This statement says that the ``do_packagedata`` task of
the current recipe and all recipes reachable (by way of dependencies)
from the image recipe must run before the ``do_rootfs`` task can run.

BitBake allows a task to recursively depend on itself by
referencing itself in the task list::

   do_a[recrdeptask] = "do_a do_b"

In the same way as before, this means that the ``do_a``
and ``do_b`` tasks of the current recipe and all
recipes reachable (by way of dependencies) from the recipe
must run before the ``do_a`` task can run. In this
case BitBake will ignore the current recipe's ``do_a``
task circular dependency on itself.

Inter-Task Dependencies
-----------------------

BitBake uses the ``[depends]`` flag in a more generic form to manage
inter-task dependencies. This more generic form allows for
inter-dependency checks for specific tasks rather than checks for the
data in :term:`DEPENDS`. Here is an example::

   do_patch[depends] = "quilt-native:do_populate_sysroot"

In this example, the ``do_populate_sysroot`` task of the target ``quilt-native``
must have completed before the ``do_patch`` task can execute.

The ``[rdepends]`` flag works in a similar way but takes targets in the
runtime namespace instead of the build-time dependency namespace.

Functions You Can Call From Within Python
=========================================

BitBake provides many functions you can call from within Python
functions. This section lists the most commonly used functions, and
mentions where to find others.

Functions for Accessing Datastore Variables
-------------------------------------------

It is often necessary to access variables in the BitBake datastore using
Python functions. The BitBake datastore has an API that allows you this
access. Here is a list of available operations:

.. list-table::
   :widths: auto
   :header-rows: 1

   * - *Operation*
     - *Description*
   * - ``d.getVar("X", expand)``
     - Returns the value of variable "X". Using "expand=True" expands the
       value. Returns "None" if the variable "X" does not exist.
   * - ``d.setVar("X", "value")``
     - Sets the variable "X" to "value"
   * - ``d.appendVar("X", "value")``
     - Adds "value" to the end of the variable "X". Acts like ``d.setVar("X",
       "value")`` if the variable "X" does not exist.
   * - ``d.prependVar("X", "value")``
     - Adds "value" to the start of the variable "X". Acts like
       ``d.setVar("X","value")`` if the variable "X" does not exist.
   * - ``d.delVar("X")``
     - Deletes the variable "X" from the datastore. Does nothing if the variable
       "X" does not exist.
   * - ``d.renameVar("X", "Y")``
     - Renames the variable "X" to "Y". Does nothing if the variable "X" does
       not exist.
   * - ``d.getVarFlag("X", flag, expand)``
     - Returns the value of variable "X". Using "expand=True" expands the
       value. Returns "None" if either the variable "X" or the named flag does
       not exist.
   * - ``d.setVarFlag("X", flag, "value")``
     - Sets the named flag for variable "X" to "value".
   * - ``d.appendVarFlag("X", flag, "value")``
     - Appends "value" to the named flag on the variable "X". Acts like
       ``d.setVarFlag("X", flag, "value")`` if the named flag does not exist.
   * - ``d.prependVarFlag("X", flag, "value")``
     - Prepends "value" to the named flag on the variable "X". Acts like
       ``d.setVarFlag("X", flag, "value")`` if the named flag does not exist.
   * - ``d.delVarFlag("X", flag)``
     - Deletes the named flag on the variable "X" from the datastore.
   * - ``d.setVarFlags("X", flagsdict)``
     - Sets the flags specified in the ``flagsdict()``
       parameter. ``setVarFlags`` does not clear previous flags. Think of this
       operation as ``addVarFlags``.
   * - ``d.getVarFlags("X")``
     - Returns a ``flagsdict`` of the flags for the variable "X". Returns "None"
       if the variable "X" does not exist.
   * - ``d.delVarFlags("X")``
     - Deletes all the flags for the variable "X". Does nothing if the variable
       "X" does not exist.
   * - ``d.expand(expression)``
     - Expands variable references in the specified string
       expression. References to variables that do not exist are left as is. For
       example, ``d.expand("foo ${X}")`` expands to the literal string "foo
       ${X}" if the variable "X" does not exist.

Other Functions
---------------

You can find many other functions that can be called from Python by
looking at the source code of the ``bb`` module, which is in
``bitbake/lib/bb``. For example, ``bitbake/lib/bb/utils.py`` includes
the commonly used functions ``bb.utils.contains()`` and
``bb.utils.mkdirhier()``, which come with docstrings.

Extending Python Library Code
-----------------------------

If you wish to add your own Python library code (e.g. to provide
functions/classes you can use from Python functions in the metadata)
you can do so from any layer using the ``addpylib`` directive.
This directive is typically added to your layer configuration (
``conf/layer.conf``) although it will be handled in any ``.conf`` file.

Usage is of the form::

   addpylib <directory> <namespace>

Where <directory> specifies the directory to add to the library path.
The specified <namespace> is imported automatically, and if the imported
module specifies an attribute named ``BBIMPORTS``, that list of
sub-modules is iterated and imported too.

Testing and Debugging BitBake Python code
-----------------------------------------

The OpenEmbedded build system implements a convenient ``pydevshell`` target which
you can use to access the BitBake datastore and experiment with your own Python
code. See :yocto_docs:`Using a Python Development Shell
</dev-manual/python-development-shell.html#using-a-python-development-shell>` in the Yocto
Project manual for details.

Task Checksums and Setscene
===========================

BitBake uses checksums (or signatures) along with the setscene to
determine if a task needs to be run. This section describes the process.
To help understand how BitBake does this, the section assumes an
OpenEmbedded metadata-based example.

These checksums are stored in :term:`STAMP`. You can
examine the checksums using the following BitBake command::

   $ bitbake-dumpsigs

This command returns the signature data in a readable
format that allows you to examine the inputs used when the OpenEmbedded
build system generates signatures. For example, using
``bitbake-dumpsigs`` allows you to examine the ``do_compile`` task's
"sigdata" for a C application (e.g. ``bash``). Running the command also
reveals that the "CC" variable is part of the inputs that are hashed.
Any changes to this variable would invalidate the stamp and cause the
``do_compile`` task to run.

The following list describes related variables:

-  :term:`BB_HASHCHECK_FUNCTION`:
   Specifies the name of the function to call during the "setscene" part
   of the task's execution in order to validate the list of task hashes.

-  :term:`BB_SETSCENE_DEPVALID`:
   Specifies a function BitBake calls that determines whether BitBake
   requires a setscene dependency to be met.

-  :term:`BB_TASKHASH`: Within an executing task,
   this variable holds the hash of the task as returned by the currently
   enabled signature generator.

-  :term:`STAMP`: The base path to create stamp files.

-  :term:`STAMPCLEAN`: Again, the base path to
   create stamp files but can use wildcards for matching a range of
   files for clean operations.

Wildcard Support in Variables
=============================

Support for wildcard use in variables varies depending on the context in
which it is used. For example, some variables and filenames allow
limited use of wildcards through the "``%``" and "``*``" characters.
Other variables or names support Python's
`glob <https://docs.python.org/3/library/glob.html>`_ syntax,
`fnmatch <https://docs.python.org/3/library/fnmatch.html#module-fnmatch>`_
syntax, or
`Regular Expression (re) <https://docs.python.org/3/library/re.html>`_
syntax.

For variables that have wildcard suport, the documentation describes
which form of wildcard, its use, and its limitations.
