.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Using a Development Shell
*************************

When debugging certain commands or even when just editing packages,
``devshell`` can be a useful tool. When you invoke ``devshell``, all
tasks up to and including
:ref:`ref-tasks-patch` are run for the
specified target. Then, a new terminal is opened and you are placed in
``${``\ :term:`S`\ ``}``, the source
directory. In the new terminal, all the OpenEmbedded build-related
environment variables are still defined so you can use commands such as
``configure`` and ``make``. The commands execute just as if the
OpenEmbedded build system were executing them. Consequently, working
this way can be helpful when debugging a build or preparing software to
be used with the OpenEmbedded build system.

Here is an example that uses ``devshell`` on a target named
``matchbox-desktop``::

  $ bitbake matchbox-desktop -c devshell

This command spawns a terminal with a shell prompt within the
OpenEmbedded build environment. The
:term:`OE_TERMINAL` variable
controls what type of shell is opened.

For spawned terminals, the following occurs:

-  The ``PATH`` variable includes the cross-toolchain.

-  The ``pkgconfig`` variables find the correct ``.pc`` files.

-  The ``configure`` command finds the Yocto Project site files as well
   as any other necessary files.

Within this environment, you can run configure or compile commands as if
they were being run by the OpenEmbedded build system itself. As noted
earlier, the working directory also automatically changes to the Source
Directory (:term:`S`).

To manually run a specific task using ``devshell``, run the
corresponding ``run.*`` script in the
``${``\ :term:`WORKDIR`\ ``}/temp``
directory (e.g., ``run.do_configure.``\ `pid`). If a task's script does
not exist, which would be the case if the task was skipped by way of the
sstate cache, you can create the task by first running it outside of the
``devshell``::

   $ bitbake -c task

.. note::

   -  Execution of a task's ``run.*`` script and BitBake's execution of
      a task are identical. In other words, running the script re-runs
      the task just as it would be run using the ``bitbake -c`` command.

   -  Any ``run.*`` file that does not have a ``.pid`` extension is a
      symbolic link (symlink) to the most recent version of that file.

Remember, that the ``devshell`` is a mechanism that allows you to get
into the BitBake task execution environment. And as such, all commands
must be called just as BitBake would call them. That means you need to
provide the appropriate options for cross-compilation and so forth as
applicable.

When you are finished using ``devshell``, exit the shell or close the
terminal window.

.. note::

   -  It is worth remembering that when using ``devshell`` you need to
      use the full compiler name such as ``arm-poky-linux-gnueabi-gcc``
      instead of just using ``gcc``. The same applies to other
      applications such as ``binutils``, ``libtool`` and so forth.
      BitBake sets up environment variables such as :term:`CC` to assist
      applications, such as ``make`` to find the correct tools.

   -  It is also worth noting that ``devshell`` still works over X11
      forwarding and similar situations.

