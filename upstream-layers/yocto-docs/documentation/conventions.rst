.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

=========================
Documentation Conventions
=========================

These conventions apply throughout the Yocto Project documentation.

Placeholders
============

Text enclosed in angle brackets is a placeholder. Replace it, including the
brackets, with a value of your own:

.. code-block:: none

   SRCREV:pn-<recipe> = "${AUTOREV}"

Here, ``<recipe>`` stands for the name of your recipe. A placeholder is
never text to be copied as it stands, and an example containing one will
not work until every placeholder in it has been replaced.

Commands and prompts
====================

Commands are shown with the prompt that runs them, and the prompt is not
part of the command. Do not type it.

A ``$`` introduces a command you run as your normal user:

.. code-block:: console

   $ bitbake core-image-minimal

Where a command needs administrative privileges on your
:term:`build host`, it is shown with ``sudo`` rather than with a root
prompt:

.. code-block:: console

   $ sudo apt install gawk

A full prompt ending in ``#`` introduces a command run as ``root``, usually
on a target machine rather than on your :term:`build host`:

.. code-block:: console

   root@machine:~# gdb /bin/cat

Within an example, a line beginning with ``#`` is a comment rather than a
command:

.. code-block:: text

   # This is a comment, not a command to run as root.
   $ bitbake core-image-minimal
