meta-arm-toolchain Yocto Layer
==============================

This layer contains recipes for the prebuilt GNU Arm toolchains.

Information regarding contributing, reporting bugs, etc can be found in the
top-level meta-arm readme file.

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
