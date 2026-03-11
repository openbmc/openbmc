.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Optionally Using an External Toolchain
**************************************

You might want to use an external toolchain as part of your development.
If this is the case, the fundamental steps you need to accomplish are as
follows:

-  Understand where the installed toolchain resides. For cases where you
   need to build the external toolchain, you would need to take separate
   steps to build and install the toolchain.

-  Make sure you add the layer that contains the toolchain to your
   ``bblayers.conf`` file through the
   :term:`BBLAYERS` variable.

-  Set the :term:`EXTERNAL_TOOLCHAIN` variable in your ``local.conf`` file
   to the location in which you installed the toolchain.

The toolchain configuration is very flexible and customizable. It
is primarily controlled with the :term:`TCMODE` variable. This variable
controls which ``tcmode-*.inc`` file to include from the
``meta/conf/distro/include`` directory within the :term:`Source Directory`.

The default value of :term:`TCMODE` is "default", which tells the
OpenEmbedded build system to use its internally built toolchain (i.e.
``tcmode-default.inc``). However, other patterns are accepted. In
particular, "external-\*" refers to external toolchains. One example is
the Mentor Graphics Sourcery G++ Toolchain. Support for this toolchain resides
in the separate ``meta-sourcery`` layer at
https://github.com/MentorEmbedded/meta-sourcery/.
See its ``README`` file for details about how to use this layer.

Another example of external toolchain layer is
:yocto_git:`meta-arm-toolchain </meta-arm/tree/meta-arm-toolchain/>`
supporting GNU toolchains released by ARM.

You can find further information by reading about the :term:`TCMODE` variable
in the Yocto Project Reference Manual's variable glossary.
