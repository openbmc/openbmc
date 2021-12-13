.. SPDX-License-Identifier: CC-BY-2.5

===================
Hello World Example
===================

BitBake Hello World
===================

The simplest example commonly used to demonstrate any new programming
language or tool is the "`Hello
World <http://en.wikipedia.org/wiki/Hello_world_program>`__" example.
This appendix demonstrates, in tutorial form, Hello World within the
context of BitBake. The tutorial describes how to create a new project
and the applicable metadata files necessary to allow BitBake to build
it.

Obtaining BitBake
=================

See the :ref:`bitbake-user-manual/bitbake-user-manual-hello:obtaining bitbake` section for
information on how to obtain BitBake. Once you have the source code on
your machine, the BitBake directory appears as follows::

   $ ls -al
   total 100
   drwxrwxr-x. 9 wmat wmat  4096 Jan 31 13:44 .
   drwxrwxr-x. 3 wmat wmat  4096 Feb  4 10:45 ..
   -rw-rw-r--. 1 wmat wmat   365 Nov 26 04:55 AUTHORS
   drwxrwxr-x. 2 wmat wmat  4096 Nov 26 04:55 bin
   drwxrwxr-x. 4 wmat wmat  4096 Jan 31 13:44 build
   -rw-rw-r--. 1 wmat wmat 16501 Nov 26 04:55 ChangeLog
   drwxrwxr-x. 2 wmat wmat  4096 Nov 26 04:55 classes
   drwxrwxr-x. 2 wmat wmat  4096 Nov 26 04:55 conf
   drwxrwxr-x. 3 wmat wmat  4096 Nov 26 04:55 contrib
   -rw-rw-r--. 1 wmat wmat 17987 Nov 26 04:55 COPYING
   drwxrwxr-x. 3 wmat wmat  4096 Nov 26 04:55 doc
   -rw-rw-r--. 1 wmat wmat    69 Nov 26 04:55 .gitignore
   -rw-rw-r--. 1 wmat wmat   849 Nov 26 04:55 HEADER
   drwxrwxr-x. 5 wmat wmat  4096 Jan 31 13:44 lib
   -rw-rw-r--. 1 wmat wmat   195 Nov 26 04:55 MANIFEST.in
   -rw-rw-r--. 1 wmat wmat  2887 Nov 26 04:55 TODO

At this point, you should have BitBake cloned to a directory that
matches the previous listing except for dates and user names.

Setting Up the BitBake Environment
==================================

First, you need to be sure that you can run BitBake. Set your working
directory to where your local BitBake files are and run the following
command::

  $ ./bin/bitbake --version
  BitBake Build Tool Core version 1.23.0, bitbake version 1.23.0

The console output tells you what version
you are running.

The recommended method to run BitBake is from a directory of your
choice. To be able to run BitBake from any directory, you need to add
the executable binary to your binary to your shell's environment
``PATH`` variable. First, look at your current ``PATH`` variable by
entering the following::

  $ echo $PATH

Next, add the directory location
for the BitBake binary to the ``PATH``. Here is an example that adds the
``/home/scott-lenovo/bitbake/bin`` directory to the front of the
``PATH`` variable::

  $ export PATH=/home/scott-lenovo/bitbake/bin:$PATH

You should now be able to enter the ``bitbake`` command from the command
line while working from any directory.

The Hello World Example
=======================

The overall goal of this exercise is to build a complete "Hello World"
example utilizing task and layer concepts. Because this is how modern
projects such as OpenEmbedded and the Yocto Project utilize BitBake, the
example provides an excellent starting point for understanding BitBake.

To help you understand how to use BitBake to build targets, the example
starts with nothing but the ``bitbake`` command, which causes BitBake to
fail and report problems. The example progresses by adding pieces to the
build to eventually conclude with a working, minimal "Hello World"
example.

While every attempt is made to explain what is happening during the
example, the descriptions cannot cover everything. You can find further
information throughout this manual. Also, you can actively participate
in the :oe_lists:`/g/bitbake-devel`
discussion mailing list about the BitBake build tool.

.. note::

   This example was inspired by and drew heavily from
   `Mailing List post - The BitBake equivalent of "Hello, World!"
   <https://www.mail-archive.com/yocto@yoctoproject.org/msg09379.html>`_.

As stated earlier, the goal of this example is to eventually compile
"Hello World". However, it is unknown what BitBake needs and what you
have to provide in order to achieve that goal. Recall that BitBake
utilizes three types of metadata files:
:ref:`bitbake-user-manual/bitbake-user-manual-intro:configuration files`,
:ref:`bitbake-user-manual/bitbake-user-manual-intro:classes`, and
:ref:`bitbake-user-manual/bitbake-user-manual-intro:recipes`.
But where do they go? How does BitBake find
them? BitBake's error messaging helps you answer these types of
questions and helps you better understand exactly what is going on.

Following is the complete "Hello World" example.

#.  **Create a Project Directory:** First, set up a directory for the
    "Hello World" project. Here is how you can do so in your home
    directory::

      $ mkdir ~/hello
      $ cd ~/hello

    This is the directory that
    BitBake will use to do all of its work. You can use this directory
    to keep all the metafiles needed by BitBake. Having a project
    directory is a good way to isolate your project.

#.  **Run BitBake:** At this point, you have nothing but a project
    directory. Run the ``bitbake`` command and see what it does::

       $ bitbake
       The BBPATH variable is not set and bitbake did not
       find a conf/bblayers.conf file in the expected location.
       Maybe you accidentally invoked bitbake from the wrong directory?
       DEBUG: Removed the following variables from the environment:
       GNOME_DESKTOP_SESSION_ID, XDG_CURRENT_DESKTOP,
       GNOME_KEYRING_CONTROL, DISPLAY, SSH_AGENT_PID, LANG, no_proxy,
       XDG_SESSION_PATH, XAUTHORITY, SESSION_MANAGER, SHLVL,
       MANDATORY_PATH, COMPIZ_CONFIG_PROFILE, WINDOWID, EDITOR,
       GPG_AGENT_INFO, SSH_AUTH_SOCK, GDMSESSION, GNOME_KEYRING_PID,
       XDG_SEAT_PATH, XDG_CONFIG_DIRS, LESSOPEN, DBUS_SESSION_BUS_ADDRESS,
       _, XDG_SESSION_COOKIE, DESKTOP_SESSION, LESSCLOSE, DEFAULTS_PATH,
       UBUNTU_MENUPROXY, OLDPWD, XDG_DATA_DIRS, COLORTERM, LS_COLORS

    The majority of this output is specific to environment variables that
    are not directly relevant to BitBake. However, the very first
    message regarding the :term:`BBPATH` variable and the
    ``conf/bblayers.conf`` file is relevant.

    When you run BitBake, it begins looking for metadata files. The
    :term:`BBPATH` variable is what tells BitBake where
    to look for those files. :term:`BBPATH` is not set and you need to set
    it. Without :term:`BBPATH`, BitBake cannot find any configuration files
    (``.conf``) or recipe files (``.bb``) at all. BitBake also cannot
    find the ``bitbake.conf`` file.

#.  **Setting BBPATH:** For this example, you can set :term:`BBPATH` in
    the same manner that you set ``PATH`` earlier in the appendix. You
    should realize, though, that it is much more flexible to set the
    :term:`BBPATH` variable up in a configuration file for each project.

    From your shell, enter the following commands to set and export the
    :term:`BBPATH` variable::

      $ BBPATH="projectdirectory"
      $ export BBPATH

    Use your actual project directory in the command. BitBake uses that
    directory to find the metadata it needs for your project.

    .. note::

       When specifying your project directory, do not use the tilde
       ("~") character as BitBake does not expand that character as the
       shell would.

#.  **Run BitBake:** Now that you have :term:`BBPATH` defined, run the
    ``bitbake`` command again::

       $ bitbake
       ERROR: Traceback (most recent call last):
         File "/home/scott-lenovo/bitbake/lib/bb/cookerdata.py", line 163, in wrapped
           return func(fn, *args)
         File "/home/scott-lenovo/bitbake/lib/bb/cookerdata.py", line 173, in parse_config_file
           return bb.parse.handle(fn, data, include)
         File "/home/scott-lenovo/bitbake/lib/bb/parse/__init__.py", line 99, in handle
           return h['handle'](fn, data, include)
         File "/home/scott-lenovo/bitbake/lib/bb/parse/parse_py/ConfHandler.py", line 120, in handle
           abs_fn = resolve_file(fn, data)
         File "/home/scott-lenovo/bitbake/lib/bb/parse/__init__.py", line 117, in resolve_file
           raise IOError("file %s not found in %s" % (fn, bbpath))
       IOError: file conf/bitbake.conf not found in /home/scott-lenovo/hello

       ERROR: Unable to parse conf/bitbake.conf: file conf/bitbake.conf not found in /home/scott-lenovo/hello

    This sample output shows that BitBake could not find the
    ``conf/bitbake.conf`` file in the project directory. This file is
    the first thing BitBake must find in order to build a target. And,
    since the project directory for this example is empty, you need to
    provide a ``conf/bitbake.conf`` file.

#.  **Creating conf/bitbake.conf:** The ``conf/bitbake.conf`` includes
    a number of configuration variables BitBake uses for metadata and
    recipe files. For this example, you need to create the file in your
    project directory and define some key BitBake variables. For more
    information on the ``bitbake.conf`` file, see
    https://git.openembedded.org/bitbake/tree/conf/bitbake.conf.

    Use the following commands to create the ``conf`` directory in the
    project directory::

      $ mkdir conf

    From within the ``conf`` directory,
    use some editor to create the ``bitbake.conf`` so that it contains
    the following::

       PN  = "${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[0] or 'defaultpkgname'}"

       TMPDIR  = "${TOPDIR}/tmp"
       CACHE   = "${TMPDIR}/cache"
       STAMP   = "${TMPDIR}/${PN}/stamps"
       T       = "${TMPDIR}/${PN}/work"
       B       = "${TMPDIR}/${PN}"

    .. note::

       Without a value for PN , the variables STAMP , T , and B , prevent more
       than one recipe from working. You can fix this by either setting PN to
       have a value similar to what OpenEmbedded and BitBake use in the default
       bitbake.conf file (see previous example). Or, by manually updating each
       recipe to set PN . You will also need to include PN as part of the STAMP
       , T , and B variable definitions in the local.conf file.

    The ``TMPDIR`` variable establishes a directory that BitBake uses
    for build output and intermediate files other than the cached
    information used by the
    :ref:`bitbake-user-manual/bitbake-user-manual-execution:setscene`
    process. Here, the ``TMPDIR`` directory is set to ``hello/tmp``.

    .. tip::

       You can always safely delete the tmp directory in order to rebuild a
       BitBake target. The build process creates the directory for you when you
       run BitBake.

    For information about each of the other variables defined in this
    example, check :term:`PN`, :term:`TOPDIR`, :term:`CACHE`, :term:`STAMP`,
    :term:`T` or :term:`B` to take you to the definitions in the
    glossary.

#.  **Run BitBake:** After making sure that the ``conf/bitbake.conf`` file
    exists, you can run the ``bitbake`` command again::

       $ bitbake
       ERROR: Traceback (most recent call last):
         File "/home/scott-lenovo/bitbake/lib/bb/cookerdata.py", line 163, in wrapped
           return func(fn, *args)
         File "/home/scott-lenovo/bitbake/lib/bb/cookerdata.py", line 177, in _inherit
           bb.parse.BBHandler.inherit(bbclass, "configuration INHERITs", 0, data)
         File "/home/scott-lenovo/bitbake/lib/bb/parse/parse_py/BBHandler.py", line 92, in inherit
           include(fn, file, lineno, d, "inherit")
         File "/home/scott-lenovo/bitbake/lib/bb/parse/parse_py/ConfHandler.py", line 100, in include
           raise ParseError("Could not %(error_out)s file %(fn)s" % vars(), oldfn, lineno)
       ParseError: ParseError in configuration INHERITs: Could not inherit file classes/base.bbclass

       ERROR: Unable to parse base: ParseError in configuration INHERITs: Could not inherit file classes/base.bbclass

    In the sample output,
    BitBake could not find the ``classes/base.bbclass`` file. You need
    to create that file next.

#.  **Creating classes/base.bbclass:** BitBake uses class files to
    provide common code and functionality. The minimally required class
    for BitBake is the ``classes/base.bbclass`` file. The ``base`` class
    is implicitly inherited by every recipe. BitBake looks for the class
    in the ``classes`` directory of the project (i.e ``hello/classes``
    in this example).

    Create the ``classes`` directory as follows::

      $ cd $HOME/hello
      $ mkdir classes

    Move to the ``classes`` directory and then create the
    ``base.bbclass`` file by inserting this single line: addtask build
    The minimal task that BitBake runs is the ``do_build`` task. This is
    all the example needs in order to build the project. Of course, the
    ``base.bbclass`` can have much more depending on which build
    environments BitBake is supporting.

#.  **Run BitBake:** After making sure that the ``classes/base.bbclass``
    file exists, you can run the ``bitbake`` command again::

       $ bitbake
       Nothing to do. Use 'bitbake world' to build everything, or run 'bitbake --help' for usage information.

    BitBake is finally reporting
    no errors. However, you can see that it really does not have
    anything to do. You need to create a recipe that gives BitBake
    something to do.

#.  **Creating a Layer:** While it is not really necessary for such a
    small example, it is good practice to create a layer in which to
    keep your code separate from the general metadata used by BitBake.
    Thus, this example creates and uses a layer called "mylayer".

    .. note::

       You can find additional information on layers in the
       ":ref:`bitbake-user-manual/bitbake-user-manual-intro:Layers`" section.

    Minimally, you need a recipe file and a layer configuration file in
    your layer. The configuration file needs to be in the ``conf``
    directory inside the layer. Use these commands to set up the layer
    and the ``conf`` directory::

       $ cd $HOME
       $ mkdir mylayer
       $ cd mylayer
       $ mkdir conf

    Move to the ``conf`` directory and create a ``layer.conf`` file that has the
    following::

      BBPATH .= ":${LAYERDIR}"
      BBFILES += "${LAYERDIR}/*.bb"
      BBFILE_COLLECTIONS += "mylayer"
      BBFILE_PATTERN_mylayer := "^${LAYERDIR_RE}/"

    For information on these variables, click on :term:`BBFILES`,
    :term:`LAYERDIR`, :term:`BBFILE_COLLECTIONS` or :term:`BBFILE_PATTERN_mylayer <BBFILE_PATTERN>`
    to go to the definitions in the glossary.

    You need to create the recipe file next. Inside your layer at the
    top-level, use an editor and create a recipe file named
    ``printhello.bb`` that has the following::

       DESCRIPTION = "Prints Hello World"
       PN = 'printhello'
       PV = '1'

       python do_build() {
          bb.plain("********************");
          bb.plain("*                  *");
          bb.plain("*  Hello, World!   *");
          bb.plain("*                  *");
          bb.plain("********************");
       }

    The recipe file simply provides
    a description of the recipe, the name, version, and the ``do_build``
    task, which prints out "Hello World" to the console. For more
    information on :term:`DESCRIPTION`, :term:`PN` or :term:`PV`
    follow the links to the glossary.

#. **Run BitBake With a Target:** Now that a BitBake target exists, run
    the command and provide that target::

      $ cd $HOME/hello
      $ bitbake printhello
      ERROR: no recipe files to build, check your BBPATH and BBFILES?

      Summary: There was 1 ERROR message shown, returning a non-zero exit code.

    We have created the layer with the recipe and
    the layer configuration file but it still seems that BitBake cannot
    find the recipe. BitBake needs a ``conf/bblayers.conf`` that lists
    the layers for the project. Without this file, BitBake cannot find
    the recipe.

#. **Creating conf/bblayers.conf:** BitBake uses the
    ``conf/bblayers.conf`` file to locate layers needed for the project.
    This file must reside in the ``conf`` directory of the project (i.e.
    ``hello/conf`` for this example).

    Set your working directory to the ``hello/conf`` directory and then
    create the ``bblayers.conf`` file so that it contains the following::

       BBLAYERS ?= " \
           /home/<you>/mylayer \
       "

    You need to provide your own information for ``you`` in the file.

#. **Run BitBake With a Target:** Now that you have supplied the
    ``bblayers.conf`` file, run the ``bitbake`` command and provide the
    target::

       $ bitbake printhello
       Parsing recipes: 100% |##################################################################################|
       Time: 00:00:00
       Parsing of 1 .bb files complete (0 cached, 1 parsed). 1 targets, 0 skipped, 0 masked, 0 errors.
       NOTE: Resolving any missing task queue dependencies
       NOTE: Preparing RunQueue
       NOTE: Executing RunQueue Tasks
       ********************
       *                  *
       *  Hello, World!   *
       *                  *
       ********************
       NOTE: Tasks Summary: Attempted 1 tasks of which 0 didn't need to be rerun and all succeeded.

    .. note::

       After the first execution, re-running bitbake printhello again will not
       result in a BitBake run that prints the same console output. The reason
       for this is that the first time the printhello.bb recipe's do_build task
       executes successfully, BitBake writes a stamp file for the task. Thus,
       the next time you attempt to run the task using that same bitbake
       command, BitBake notices the stamp and therefore determines that the task
       does not need to be re-run. If you delete the tmp directory or run
       bitbake -c clean printhello and then re-run the build, the "Hello,
       World!" message will be printed again.
