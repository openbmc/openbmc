.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

*****************************
Using Configuration Fragments
*****************************

:term:`Configuration Fragments <Configuration Fragment>` define top level build
configuration features that can be independently enabled and disabled using
standard tooling. Such features are made of one or several build configuration
statements that are either contained in a fragment file, or are set indirectly
using the :term:`Built-in Fragment` mechanism.

This document provides a quick reference of the :oe_git:`bitbake-config-build
</bitbake/tree/bin/bitbake-config-build>` tool and lists the
:term:`Configuration Fragments <Configuration Fragment>` and :term:`Built-in
Fragments <Built-in Fragment>` available in the :term:`OpenEmbedded Build
System` core repositories.

.. note::

   For details on how to define new fragments in your build, see the
   :doc:`/dev-manual/creating-fragments` section of the Yocto Project Development
   Tasks Manual.

.. _ref-bitbake-config-build-qf:

``bitbake-config-build`` Quick Reference
========================================

:term:`Configuration Fragments <Configuration Fragment>` are managed with the
:oe_git:`bitbake-config-build </bitbake/tree/bin/bitbake-config-build>`
command-line tool, which is available after :ref:`dev-manual/start:Initializing
the Build Environment`.

The ``bitbake-config-build`` command-line tool uses sub-commands to manage
fragments, which are detailed in the sections below. For each sub-command, the
``--help`` flag can be passed to get more information on the sub-command.

.. _ref-bitbake-config-build-list-fragments:

``bitbake-config-build list-fragments``
---------------------------------------

The :ref:`ref-bitbake-config-build-list-fragments` command will list the :term:`Built-in
Fragments <Built-in Fragment>` and :term:`Configuration Fragments <Configuration
Fragment>` that are currently available, and will also print which fragments are
enabled or disabled.

.. _ref-bitbake-config-build-show-fragment:

``bitbake-config-build show-fragment``
--------------------------------------

The :ref:`ref-bitbake-config-build-show-fragment` command is used to show the
location and value of a fragment. For example, running ``bitbake-config-build
show-fragment core/yocto/sstate-mirror-cdn`` will show the content of the
:ref:`ref-fragments-core-yocto-sstate-mirror-cdn` fragment.

.. _ref-bitbake-config-build-enable-fragment:

``bitbake-config-build enable-fragment``
----------------------------------------

The :ref:`ref-bitbake-config-build-enable-fragment` command is used to enable a
fragment. When a fragment is enabled, the configuration variables of this
fragment are parsed by :term:`BitBake` and their values are available globally
in your build.

From the list obtained with the :ref:`ref-bitbake-config-build-list-fragments`
command, you can determine which fragments can be enabled for your build.

For example, the following command would enable the
:ref:`ref-fragments-core-yocto-sstate-mirror-cdn` fragment::

   bitbake-config-build enable-fragment core/yocto/sstate-mirror-cdn

.. note::

   Multiple fragments can be enabled at once with the same command::

      bitbake-config-build enable-fragment <fragment1> <fragment2> ...

:term:`Built-in fragments <Built-in Fragment>` are enabled the same way, and
their values are defined from the command-line directly. For example, the
following command sets the ``qemuarm64`` :term:`MACHINE` through the
:ref:`ref-fragments-builtin-core-machine` fragment::

   bitbake-config-build enable-fragment machine/qemuarm64

This fragment can be overridden from the command-line by setting it to another
value, for example::

   bitbake-config-build enable-fragment machine/qemux86-64

In the above example, the new value of :term:`MACHINE` is now equal to
``qemux86-64``.

When a fragment is enabled with :ref:`ref-bitbake-config-build-enable-fragment`,
its name is automatically appended to the :term:`OE_FRAGMENTS` variable in
:ref:`structure-build-conf-toolcfg.conf`.

.. note::

   It is also possible to manually remove or add fragments by modifying the
   :term:`OE_FRAGMENTS` variable in a configuration file such as
   :ref:`structure-build-conf-local.conf`.

.. _ref-bitbake-config-build-disable-fragment:

``bitbake-config-build disable-fragment``
-----------------------------------------

Any fragment enabled with the :ref:`ref-bitbake-config-build-enable-fragment`
command can be disabled with the :ref:`ref-bitbake-config-build-disable-fragment`
command. The list of enabled fragments can be obtained with
:ref:`ref-bitbake-config-build-list-fragments`.

For example, the following command disables the
:ref:`ref-fragments-core-yocto-sstate-mirror-cdn` fragment::

   bitbake-config-build disable-fragment core/yocto/sstate-mirror-cdn

Likewise, :term:`Built-in Fragments <Built-in Fragment>` are disabled the
same way. For example, this would disable the ``machine/qemuarm64`` fragment::

   bitbake-config-build disable-fragment machine/qemuarm64

.. note::

   Multiple fragments can be disabled at once with the same command::

      bitbake-config-build disable-fragment <fragment1> <fragment2>

.. _ref-bitbake-config-build-disable-all-fragments:

``bitbake-config-build disable-all-fragments``
----------------------------------------------

The :ref:`ref-bitbake-config-build-disable-all-fragments` command disables all of the
currently enabled fragments.  The list of enabled fragments can be obtained with
:ref:`ref-bitbake-config-build-list-fragments`.

This command is run without arguments::

   bitbake-config-build disable-all-fragments

Core Fragments
==============

Core Built-in Fragments
-----------------------

:term:`Built-in Fragments <Built-in Fragment>` are used to assign a single
variable globally. The :term:`OpenEmbedded Build System` defines multiple
built-in fragments that are detailed in this section.

.. _ref-fragments-builtin-core-machine:

``machine/``
~~~~~~~~~~~~

The ``machine/`` :term:`built-in fragment` can be used to assign the value of
the :term:`MACHINE` variable globally.

.. _ref-fragments-builtin-core-distro:

``distro/``
~~~~~~~~~~~

The ``distro/`` :term:`built-in fragment` can be used to assign the value of
the :term:`DISTRO` variable globally.

Core Configuration Fragments
----------------------------

Yocto Project Fragments
~~~~~~~~~~~~~~~~~~~~~~~

This group defines fragments related to the Yocto Project infrastructure in
general.

.. _ref-fragments-core-yocto-sstate-mirror-cdn:

``core/yocto/sstate-mirror-cdn``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto/sstate-mirror-cdn`` :term:`configuration fragment` can be used
to set up :term:`BB_HASHSERVE_UPSTREAM` and :term:`SSTATE_MIRRORS` to use
pre-built :ref:`shared state cache <overview-manual/concepts:shared state
cache>` artifacts for standard Yocto build configurations.

This will mean the build will query the Yocto Project mirrors to check for
artifacts at the start of builds, which does slow it down initially but it will
then speed up the builds by not having to build things if they are present in
the cache. It assumes you can download something faster than you can build it
which will depend on your network configuration.

.. _ref-fragments-root-login-with-empty-password:

``core/yocto/root-login-with-empty-password``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto/root-login-with-empty-password`` :term:`configuration fragment`
can be used to allow to login as the ``root`` user to login without a password
on the serial console and over SSH.

Yocto Project Autobuilder Fragments
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This group defines fragment used for the Yocto Project Autobuilder. For details,
see the :ref:`test-manual/intro:Yocto Project Autobuilder Overview` section of
the Yocto Project Test Environment Manual.

.. _ref-fragment-core-yocto-autobuilder-autobuilder:

``core/yocto-autobuilder/autobuilder``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto-autobuilder/autobuilder`` fragment defines common variables
used in builds started by the Yocto Project Autobuilder.

.. _ref-fragment-core-yocto-autobuilder-autobuilder-resource-constraints:

``core/yocto-autobuilder/autobuilder-resource-constraints``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto-autobuilder/autobuilder`` fragment defines variables for
limiting the resources used by the Yocto Project Autobuilder during builds. For
more details on how to limit resources, see the :doc:`/dev-manual/limiting-resources`
section of the Yocto Project Development Tasks Manual.

.. _ref-fragment-core-yocto-autobuilder-multilib-mips64-n32:

``core/yocto-autobuilder/multilib-mips64-n32``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto-autobuilder/multilib-mips64-n32`` fragment enables
tri-architecture :ref:`multilib <dev-manual/libraries:Combining Multiple
Versions of Library Files into One Image>` configurations for :wikipedia:`MIPS64
<MIPS_architecture>` machines, which includes ``mips64-n32``, ``mips64``, and
``mips32r2``.

.. _ref-fragment-core-yocto-autobuilder-multilib-x86-lib32:

``core/yocto-autobuilder/multilib-x86-lib32``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto-autobuilder/multilib-x86-lib32`` fragment enables
:ref:`multilib <dev-manual/libraries:Combining Multiple Versions of Library
Files into One Image>` configurations for supporting 32-bit libraries on 64-bit
:wikipedia:`X86 <X86>` builds.

.. _ref-fragment-core-yocto-autobuilder-multilib-x86-lib64:

``core/yocto-autobuilder/multilib-x86-lib64``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``core/yocto-autobuilder/multilib-x86-lib64`` fragment enables
:ref:`multilib <dev-manual/libraries:Combining Multiple Versions of Library
Files into One Image>` configurations for supporting 64-bit libraries on 32-bit
:wikipedia:`X86 <X86>` builds.
