.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

***************************
Testing Packages With ptest
***************************

A Package Test (ptest) runs tests against packages built by the
OpenEmbedded build system on the target machine. A ptest contains at
least two items: the actual test, and a shell script (``run-ptest``)
that starts the test. The shell script that starts the test must not
contain the actual test --- the script only starts the test. On the other
hand, the test can be anything from a simple shell script that runs a
binary and checks the output to an elaborate system of test binaries and
data files.

The test generates output in the format used by Automake::

   result: testname

where the result can be ``PASS``, ``FAIL``, or ``SKIP``, and
the testname can be any identifying string.

For a list of Yocto Project recipes that are already enabled with ptest,
see the :yocto_wiki:`Ptest </Ptest>` wiki page.

.. note::

   A recipe is "ptest-enabled" if it inherits the :ref:`ref-classes-ptest`
   class.

Adding ptest to Your Build
==========================

Adding package testing to your image is done in two steps:

#. Select that all ptest packages should be built and packaged, and

#. Identify which of those ptest packages to add to your image.

First, in order to build all ptest packages, add the following line
to a :term:`configuration file`::

   DISTRO_FEATURES:append = " ptest"

Note that this will cause all ptest packages to be built and packaged,
but will not add any of those packages to your image; that comes in
the next step. You can then add ptest packages to your image in one of two ways.

#. If you want to add *all* of the generated ptest packages, add the line::

      EXTRA_IMAGE_FEATURES += "ptest-pkgs"

#. On the other hand, if you want to add only a select few of the ptest
   packages, you can use some variation of::

      IMAGE_INSTALL:append = " e2fsprogs-ptest zlib-ptest"

Once your build is complete, the ptest files are installed into the
``/usr/lib/package/ptest`` directory within the image, where ``package``
is the name of the package.

.. note::

   If you want to list all ptest packages currently available from
   the local package feed that *could* be installed in your image
   (not just the ones you're selecting), you can use the
   ``oe-pkgdata-util`` command:

   .. code-block:: console

      $ oe-pkgdata-util list-pkgs "*-ptest"
      acl-ptest
      attr-ptest
      bash-ptest
      ... etc etc ...

Running ptest
=============

The ``ptest-runner`` package installs a shell script that loops through
all installed ptest test suites and runs them in sequence.

During the execution ``ptest-runner`` keeps count of total and failed
``ptests``. At end the execution summary is written to the console.
If any of the ``run-ptest`` fails, ``ptest-runner`` returns '1'.

The inclusion of any ptest packages in your image will automatically
include ``ptest-runner`` in your image.

Getting Your Package Ready
==========================

In order to enable a recipe to run installed ``ptests`` on target hardware,
you need to prepare the recipes that build the packages you want to
test. Here is what you have to do for each recipe:

-  *Be sure the recipe inherits the* :ref:`ref-classes-ptest` *class:*
   Include the following line in each recipe::

      inherit ptest

   .. note::

      Classes for common frameworks already exist in :term:`OpenEmbedded-Core
      (OE-Core)`, such as:

      -  :oe_git:`go-ptest </openembedded-core/tree/meta/classes-recipe/go-ptest.bbclass>`
      -  :ref:`ref-classes-ptest-cargo`
      -  :ref:`ref-classes-ptest-gnome`
      -  :oe_git:`ptest-perl </openembedded-core/tree/meta/classes-recipe/ptest-perl.bbclass>`
      -  :oe_git:`ptest-python-pytest </openembedded-core/tree/meta/classes-recipe/ptest-python-pytest.bbclass>`

      Inheriting these classes with the ``inherit`` keyword in your recipe will
      make the next steps automatic.

-  *Create run-ptest:* This script starts your test. Locate the
   script where you will refer to it using
   :term:`SRC_URI`. Be sure ``run-ptest`` exits with 0 to mark it
   as successfully executed otherwise will be marked as fail.
   Here is an example that starts a test for ``dbus``::

      #!/bin/sh
      cd test
      make -k runtest-TESTS

-  *Return an appropriate exit code*: The ``run-ptest`` script must return 0 on
   success, 1 on failure. This is needed by ``ptest-runner`` to keep track of
   the successful and failed tests.

-  *Make sure the test prints at least one test result*: The execution of the
   ``run-ptest`` script must result in at least one test result output on the
   console, with the following format::

      result: testname

   Where ``result`` can be one of ``PASS``, ``SKIP``, or ``FAIL``. ``testname``
   can be any name.

   There can be as many test results as desired.

   This information is read by the :ref:`ref-classes-testimage` class and
   :oe_git:`logparser </openembedded-core/tree/meta/lib/oeqa/utils/logparser.py>`
   module.

-  *Ensure dependencies are met:* If the test adds build or runtime
   dependencies that normally do not exist for the package (such as
   requiring "make" to run the test suite), use the
   :term:`DEPENDS` and
   :term:`RDEPENDS` variables in
   your recipe in order for the package to meet the dependencies. Here
   is an example where the package has a runtime dependency on "make"::

      RDEPENDS:${PN}-ptest += "make"

-  *Add a function to build the test suite:* Not many packages support
   cross-compilation of their test suites. Consequently, you usually
   need to add a cross-compilation function to the package.

   Many packages based on Automake compile and run the test suite by
   using a single command such as ``make check``. However, the host
   ``make check`` builds and runs on the same computer, while
   cross-compiling requires that the package is built on the host but
   executed for the target architecture (though often, as in the case
   for ptest, the execution occurs on the host). The built version of
   Automake that ships with the Yocto Project includes a patch that
   separates building and execution. Consequently, packages that use the
   unaltered, patched version of ``make check`` automatically
   cross-compiles.

   Regardless, you still must add a ``do_compile_ptest`` function to
   build the test suite. Add a function similar to the following to your
   recipe::

      do_compile_ptest() {
          oe_runmake buildtest-TESTS
      }

-  *Ensure special configurations are set:* If the package requires
   special configurations prior to compiling the test code, you must
   insert a ``do_configure_ptest`` function into the recipe.

-  *Install the test suite:* The :ref:`ref-classes-ptest` class
   automatically copies the file ``run-ptest`` to the target and then runs make
   ``install-ptest`` to run the tests. If this is not enough, you need
   to create a ``do_install_ptest`` function and make sure it gets
   called after the "make install-ptest" completes.
