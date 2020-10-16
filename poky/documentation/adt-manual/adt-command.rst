.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

**********************
Using the Command Line
**********************

Recall that earlier the manual discussed how to use an existing
toolchain tarball that had been installed into the default installation
directory, ``/opt/poky/DISTRO``, which is outside of the :term:`Build Directory`
(see the section
"`Using a Cross-Toolchain
Tarball) <#using-an-existing-toolchain-tarball>`__". And, that sourcing
your architecture-specific environment setup script initializes a
suitable cross-toolchain development environment.

During this setup, locations for the compiler, QEMU scripts, QEMU
binary, a special version of ``pkgconfig`` and other useful utilities
are added to the ``PATH`` variable. Also, variables to assist
``pkgconfig`` and ``autotools`` are also defined so that, for example,
``configure.sh`` can find pre-generated test results for tests that need
target hardware on which to run. You can see the "`Setting Up the
Cross-Development
Environment <#setting-up-the-cross-development-environment>`__" section
for the list of cross-toolchain environment variables established by the
script.

Collectively, these conditions allow you to easily use the toolchain
outside of the OpenEmbedded build environment on both Autotools-based
projects and Makefile-based projects. This chapter provides information
for both these types of projects.

Autotools-Based Projects
========================

Once you have a suitable cross-toolchain installed, it is very easy to
develop a project outside of the OpenEmbedded build system. This section
presents a simple "Helloworld" example that shows how to set up,
compile, and run the project.

Creating and Running a Project Based on GNU Autotools
-----------------------------------------------------

Follow these steps to create a simple Autotools-based project:

1.  *Create your directory:* Create a clean directory for your project
    and then make that directory your working location: $ mkdir
    $HOME/helloworld $ cd $HOME/helloworld

2.  *Populate the directory:* Create ``hello.c``, ``Makefile.am``, and
    ``configure.in`` files as follows:

    -  For ``hello.c``, include these lines: #include <stdio.h> main() {
       printf("Hello World!\n"); }

    -  For ``Makefile.am``, include these lines: bin_PROGRAMS = hello
       hello_SOURCES = hello.c

    -  For ``configure.in``, include these lines: AC_INIT(hello.c)
       AM_INIT_AUTOMAKE(hello,0.1) AC_PROG_CC AC_PROG_INSTALL
       AC_OUTPUT(Makefile)

3.  *Source the cross-toolchain environment setup file:* Installation of
    the cross-toolchain creates a cross-toolchain environment setup
    script in the directory that the ADT was installed. Before you can
    use the tools to develop your project, you must source this setup
    script. The script begins with the string "environment-setup" and
    contains the machine architecture, which is followed by the string
    "poky-linux". Here is an example that sources a script from the
    default ADT installation directory that uses the 32-bit Intel x86
    Architecture and the DISTRO_NAME Yocto Project release: $ source
    /opt/poky/DISTRO/environment-setup-i586-poky-linux

4.  *Generate the local aclocal.m4 files and create the configure
    script:* The following GNU Autotools generate the local
    ``aclocal.m4`` files and create the configure script: $ aclocal $
    autoconf

5.  *Generate files needed by GNU coding standards:* GNU coding
    standards require certain files in order for the project to be
    compliant. This command creates those files: $ touch NEWS README
    AUTHORS ChangeLog

6.  *Generate the configure file:* This command generates the
    ``configure``: $ automake -a

7.  *Cross-compile the project:* This command compiles the project using
    the cross-compiler. The
    :term:`CONFIGURE_FLAGS`
    environment variable provides the minimal arguments for GNU
    configure: $ ./configure ${CONFIGURE_FLAGS}

8.  *Make and install the project:* These two commands generate and
    install the project into the destination directory: $ make $ make
    install DESTDIR=./tmp

9.  *Verify the installation:* This command is a simple way to verify
    the installation of your project. Running the command prints the
    architecture on which the binary file can run. This architecture
    should be the same architecture that the installed cross-toolchain
    supports. $ file ./tmp/usr/local/bin/hello

10. *Execute your project:* To execute the project in the shell, simply
    enter the name. You could also copy the binary to the actual target
    hardware and run the project there as well: $ ./hello As expected,
    the project displays the "Hello World!" message.

Passing Host Options
--------------------

For an Autotools-based project, you can use the cross-toolchain by just
passing the appropriate host option to ``configure.sh``. The host option
you use is derived from the name of the environment setup script found
in the directory in which you installed the cross-toolchain. For
example, the host option for an ARM-based target that uses the GNU EABI
is ``armv5te-poky-linux-gnueabi``. You will notice that the name of the
script is ``environment-setup-armv5te-poky-linux-gnueabi``. Thus, the
following command works to update your project and rebuild it using the
appropriate cross-toolchain tools: $ ./configure
--host=armv5te-poky-linux-gnueabi \\ --with-libtool-sysroot=sysroot_dir

.. note::

   If the
   configure
   script results in problems recognizing the
   --with-libtool-sysroot=
   sysroot-dir
   option, regenerate the script to enable the support by doing the
   following and then run the script again:
   ::

           $ libtoolize --automake
           $ aclocal -I ${OECORE_NATIVE_SYSROOT}/usr/share/aclocal \
              [-I dir_containing_your_project-specific_m4_macros]
           $ autoconf
           $ autoheader
           $ automake -a
                      

Makefile-Based Projects
=======================

For Makefile-based projects, the cross-toolchain environment variables
established by running the cross-toolchain environment setup script are
subject to general ``make`` rules.

To illustrate this, consider the following four cross-toolchain
environment variables:
:term:`CC`\ =i586-poky-linux-gcc -m32
-march=i586 --sysroot=/opt/poky/1.8/sysroots/i586-poky-linux
:term:`LD`\ =i586-poky-linux-ld
--sysroot=/opt/poky/1.8/sysroots/i586-poky-linux
:term:`CFLAGS`\ =-O2 -pipe -g
-feliminate-unused-debug-types
:term:`CXXFLAGS`\ =-O2 -pipe -g
-feliminate-unused-debug-types Now, consider the following three cases:

-  *Case 1 - No Variables Set in the ``Makefile``:* Because these
   variables are not specifically set in the ``Makefile``, the variables
   retain their values based on the environment.

-  *Case 2 - Variables Set in the ``Makefile``:* Specifically setting
   variables in the ``Makefile`` during the build results in the
   environment settings of the variables being overwritten.

-  *Case 3 - Variables Set when the ``Makefile`` is Executed from the
   Command Line:* Executing the ``Makefile`` from the command line
   results in the variables being overwritten with command-line content
   regardless of what is being set in the ``Makefile``. In this case,
   environment variables are not considered unless you use the "-e" flag
   during the build: $ make -e file If you use this flag, then the
   environment values of the variables override any variables
   specifically set in the ``Makefile``.

.. note::

   For the list of variables set up by the cross-toolchain environment
   setup script, see the "
   Setting Up the Cross-Development Environment
   " section.
