meta-arm-toolchain Yocto Layer
==============================

This layer contains recipes for GNU Arm toolchains which could either be built
from source or pre-built toolchain binaries.

Information regarding contributing, reporting bugs, etc can be found in the
top-level meta-arm readme file.

Source Arm toolchain for Linux development
------------------------------------------

Recipes for GNU Arm toolchain built from source are provided under
``recipes-devtools/gcc/``. In order to use Arm toolchain instead of OE core
toolchain, one just needs to override ``GCCVERSION`` in corresponding distro
conf file.

-  Eg. to use GNU Arm toolchain version ``9.2``
   GCCVERSION = "arm-9.2"

Pre-built Arm toolchain for Linux development
---------------------------------------------

Recipes for pre-built GNU Arm toolchain for Linux development are provided under
``recipes-devtools/external-arm-toolchain/``.

external-arm-toolchain.bb
~~~~~~~~~~~~~~~~~~~~~~~~~

This recipe provides support for pre-built GNU toolchains targeting processors
from the Arm Cortex-A family and implementing the Arm A-profile architecture.

Usage
^^^^^

In order to use any of pre-built Arm toolchain versions (8.2, 8.3, 9.2 and so
on), a user needs to download and untar tool-set on host machine at a particular
installation path eg: ``/opt/toolchain/``. Then user needs to specify following
in ``conf/local.conf`` in order to replace OE toolchain with pre-built GNU-A
toolchain:

TCMODE = "external-arm"
EXTERNAL_TOOLCHAIN = "<path-to-the-toolchain>"

-  Eg. for AArch64 (eg. qemuarm64 machine in poky distro)
   EXTERNAL_TOOLCHAIN = "\
     <installation-path>/gcc-arm-9.2-2019.12-x86_64-aarch64-none-linux-gnu \
   "

-  Eg. for AArch32 (eg. qemuarm machine in poky distro)
   EXTERNAL_TOOLCHAIN = "\
     <installation-path>/gcc-arm-9.2-2019.12-x86_64-arm-none-linux-gnueabihf \
   "

Supported distros and machines
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Since this pre-built GNU-A tool-set simply replaces OE toolchain, so it is
meant to be distro and machine agnostic as long as one is cross-compiling for
Arm A-profile architecture.

Tested distro and machines (for zeus stable release):
1.  Distro: poky; machines: qemuarm and qemuarm64 (build and boot tested)
2.  Distro: RPB; machines: dragonboard-410c (build and boot tested)
3.  Distro: world; machines: qemuarm and qemuarm64. Build tested for following
    layers:
    - poky/meta
    - poky/meta-poky
    - poky/meta-yocto-bsp
    - meta-openembedded/meta-oe
    - meta-openembedded/meta-python
    - meta-openembedded/meta-networking

SDK support
^^^^^^^^^^^

Pre-built toochain provides support to build OE SDK which has been tested using
following commands:

$ bitbake core-image-base -c populate_sdk
$ bitbake core-image-base -c testsdk

Note: Currently generated SDK only uses glibc provided by pre-built toolchain.
      The cross compiler, binutils, gdb/gdbserver etc. are built from source.
      This is something we would like to improve in future in order to package
      most of the components from pre-built toolchain instead.

Pre-built Arm toolchain for bare-metal development
--------------------------------------------------

Recipes for pre-built GNU Arm toolchain for bare-metal development are provided
under ``recipes-devtools/external-arm-toolchain/``.

gcc-arm-none-eabi_<version>.bb
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This recipe provides support for pre-built GNU Arm Embedded toolchain for
bare-metal software development on devices based on 32-bit Arm Cortex-A,
Cortex-R and Cortex-M processors.

Supported version: 9-2019-q4-major

gcc-aarch64-none-elf_<version>.bb
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This recipe provides support for pre-built GNU Arm toolchain for bare-metal
software development on devices based on 64-bit Arm Cortex-A processors.

Supported version: 9.2-2019.12

Layer maintainer(s)
-------------------
* Sumit Garg <sumit.garg@linaro.org>
* Denys Dmytriyenko <denis@denix.org>
