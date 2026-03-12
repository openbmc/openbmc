.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Creating New Configuration Fragments In Your Build
**************************************************

:term:`Configuration Fragments <Configuration Fragment>` define top level build
configuration features that can be independently enabled and disabled using
standard tooling. Such features are made of one or several build configuration
statements that are either contained in a fragment file, or are set indirectly
using the :term:`Built-in Fragment` mechanism.

This section will describe how to create new fragments for your builds.

There are two kinds of configuration fragments:

-  Standard :term:`Configuration Fragments <Configuration Fragment>` which a
   stored in a file. These fragments include a summary and a description,
   following by configuration statements.

-  :term:`Built-in Fragments <Built-in Fragment>` which can be used to assign a
   value to a single variable and do not require a separate definition file.
   They are especially useful when a list of possible values is very long (or
   infinite).

Creating A Standard Configuration Fragment
==========================================

By default, all configuration fragments are located within the
``conf/fragments`` directory of a :term:`layer`. This location is defined by the
:term:`OE_FRAGMENTS_PREFIX` variable which, in turn, is used as a parameter in an
:ref:`addfragments <bitbake-user-manual/bitbake-user-manual-metadata:\`\`addfragments\`\`
directive>` directive in :oe_git:`bitbake.conf </openembedded-core/tree/meta/conf/bitbake.conf>`.

You can create one or more :term:`configuration fragment` files in your
:term:`layer` in this directory. Let's take the following example, where
``custom-fragment.conf`` is our custom fragment file::

   meta-custom
   ├── conf
   │   ├── fragments
   │   │   └── custom-fragment.conf
   │   └── layer.conf
   ...

For our ``custom-fragment.conf`` file, the following variables **must** be set
for our fragment to be considered a valid fragment by the :term:`OpenEmbedded
Build System`:

-  :term:`BB_CONF_FRAGMENT_SUMMARY`: a one-line summary of this fragment.

-  :term:`BB_CONF_FRAGMENT_DESCRIPTION`: a description of this fragment.

.. note::

   The :term:`BB_CONF_FRAGMENT_SUMMARY` and :term:`BB_CONF_FRAGMENT_DESCRIPTION`
   variables are also passed as parameters in an :ref:`addfragments
   <bitbake-user-manual/bitbake-user-manual-metadata:\`\`addfragments\`\`
   directive>` directive in :oe_git:`bitbake.conf
   </openembedded-core/tree/meta/conf/bitbake.conf>`.

After creating these variables, our custom fragment should look like the
following:

.. code-block::
   :caption: custom-fragment.conf

   BB_CONF_FRAGMENT_SUMMARY = "This fragment sets a limit of 4 bitbake threads and 4 parsing threads"
   BB_CONF_FRAGMENT_DESCRIPTION = "This fragment is useful to constrain resource consumption when the Yocto default \
   is causing an overload of host machine's memory and CPU resources."

For now, our fragment does not have any additional configuration statement.
Let's add the following assignments to our fragment:

.. code-block::
   :caption: custom-fragment.conf (continued)

   BB_NUMBER_THREADS = "4"
   BB_NUMBER_PARSE_THREADS = "4"

This means that our fragment can be enabled to set a limit on the number of
threads :term:`BitBake` will use with the :term:`BB_NUMBER_THREADS` and
:term:`BB_NUMBER_PARSE_THREADS` variables.

For now, our fragment exists and is listed by the
:ref:`ref-bitbake-config-build-list-fragments` command, but is not enabled. To
enable this fragment, use the :ref:`ref-bitbake-config-build-enable-fragment`
command::

   bitbake-config-build enable-fragment meta-custom/custom-fragment

.. note::

   The ``meta-custom`` prefix in the above command depends on the name of your
   layer. This name is defined by the :term:`BBFILE_COLLECTIONS` variable in
   the ``conf/layer.conf`` file of your layer.

Standard Configuration fragments can be organized in a more complex way. For
example, it's possible to create sub-directories to organize your fragments::

   meta-custom
   ├── conf
   │   ├── fragments
   │   │   ├── networking
   │   │   │   └── mirrors.conf
   │   │   └── resources
   │   │       └── numberthreads.conf
   │   └── layer.conf
   ...

In the above example, the ``meta-custom/networking/mirrors`` and
``meta-custom/resources/numberthreads`` fragments will be available in your
build.

Creating A Built-in Fragment
============================

Within the :term:`OpenEmbedded Build System`, Built-in Fragments are defined
with the :term:`OE_FRAGMENTS_BUILTIN` variable, which is passed as a
parameter in an :ref:`addfragments <bitbake-user-manual/bitbake-user-manual-metadata:\`\`addfragments\`\`
directive>` directive in :oe_git:`bitbake.conf </openembedded-core/tree/meta/conf/bitbake.conf>`.

Adding new :term:`Built-in Fragments <Built-in Fragment>` can be done by
appending the :term:`OE_FRAGMENTS_BUILTIN` variable from your :term:`layer`
configuration file:

.. code-block::
   :caption: layer.conf

   OE_FRAGMENTS_BUILTIN:append = " custom-builtin-fragment:CUSTOM_VARIABLE"

.. warning::

   Make sure to use the ``:append`` override in the above assignment, as using
   ``+=`` can lead to unexpected behavior.

.. warning::

   Due to the way :term:`BitBake` parses files, it is not possible to modify
   :term:`OE_FRAGMENTS_BUILTIN` from any kind of :term:`configuration file`.
   Setting it from the :term:`layer` configuration file (``conf/layer.conf``) is
   the retained solution to create new built-in fragments.

You can then use the :ref:`ref-bitbake-config-build-enable-fragment` command to
set a value to the ``CUSTOM_VARIABLE`` variable::

   bitbake-config-build enable-fragment custom-builtin-fragment/somevalue
