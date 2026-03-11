.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Using a Python Development Shell
********************************

Similar to working within a development shell as described in the
previous section, you can also spawn and work within an interactive
Python development shell. When debugging certain commands or even when
just editing packages, ``pydevshell`` can be a useful tool. When you
invoke the ``pydevshell`` task, all tasks up to and including
:ref:`ref-tasks-patch` are run for the
specified target. Then a new terminal is opened. Additionally, key
Python objects and code are available in the same way they are to
BitBake tasks, in particular, the data store 'd'. So, commands such as
the following are useful when exploring the data store and running
functions::

   pydevshell> d.getVar("STAGING_DIR")
   '/media/build1/poky/build/tmp/sysroots'
   pydevshell> d.getVar("STAGING_DIR", False)
   '${TMPDIR}/sysroots'
   pydevshell> d.setVar("FOO", "bar")
   pydevshell> d.getVar("FOO")
   'bar'
   pydevshell> d.delVar("FOO")
   pydevshell> d.getVar("FOO")
   pydevshell> bb.build.exec_func("do_unpack", d)
   pydevshell>

See the ":ref:`bitbake-user-manual/bitbake-user-manual-metadata:functions you can call from within python`"
section in the BitBake User Manual for details about available functions.

The commands execute just as if the OpenEmbedded build
system were executing them. Consequently, working this way can be
helpful when debugging a build or preparing software to be used with the
OpenEmbedded build system.

Here is an example that uses ``pydevshell`` on a target named
``matchbox-desktop``::

   $ bitbake matchbox-desktop -c pydevshell

This command spawns a terminal and places you in an interactive Python
interpreter within the OpenEmbedded build environment. The
:term:`OE_TERMINAL` variable
controls what type of shell is opened.

When you are finished using ``pydevshell``, you can exit the shell
either by using Ctrl+d or closing the terminal window.

