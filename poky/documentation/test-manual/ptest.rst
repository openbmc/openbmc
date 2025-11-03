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

To add package testing to your build, add the :term:`DISTRO_FEATURES` and
:term:`EXTRA_IMAGE_FEATURES` variables to your ``local.conf`` file, which
is found in the :term:`Build Directory`::

   DISTRO_FEATURES:append = " ptest"
   EXTRA_IMAGE_FEATURES += "ptest-pkgs"

Once your build is complete, the ptest files are installed into the
``/usr/lib/package/ptest`` directory within the image, where ``package``
is the name of the package.

Running ptest
=============

The ``ptest-runner`` package installs a shell script that loops through
all installed ptest test suites and runs them in sequence. Consequently,
you might want to add this package to your image.

Getting Your Package Ready
==========================

In order to enable a recipe to run installed ptests on target hardware,
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
   :term:`SRC_URI`. Here is an
   example that starts a test for ``dbus``::

      #!/bin/sh
      cd test
      make -k runtest-TESTS

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
