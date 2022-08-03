Introduction
------------
This repository contains the Arm layers for OpenEmbedded.

* meta-arm

  This layer contains general recipes for the Arm architecture, such as firmware, FVPs, and Arm-specific integration.

* meta-arm-autonomy

  This layer is the distribution for a reference stack for autonomous systems.

* meta-arm-bsp

  This layer contains machines for Arm reference platforms, for example FVP Base, N1SDP, and Juno.

* meta-arm-toolchain

  This layer contains recipes for Arm's binary toolchains (GCC and Clang for -A and -M), and a recipe to build Arm's GCC.

* meta-atp

  This layer contains recipes for the Adaptive Traffic Generation integration into meta-gem5.

* meta-gem5

  This layer contains recipes and machines for gem5, a system-level and processor simulator.


Other Directories
-----------------

* ci

  This directory contains gitlab continuous integration configuration files (KAS yaml files) as well as scripts needed for this

* kas

  This directory contains KAS yaml files to describe builds for systems not used in CI

* scripts

  This directory contains scripts used in running the CI tests

Contributing
------------
Currently, we only accept patches from the meta-arm mailing list.  For general
information on how to submit a patch, please read
https://www.openembedded.org/wiki/How_to_submit_a_patch_to_OpenEmbedded

E-mail meta-arm@lists.yoctoproject.org with patches created using this process. You can configure git-send-email to automatically use this address for the meta-arm repository with the following git command:

$ git config --local --add sendemail.to meta-arm@lists.yoctoproject.org

Commits and patches added should follow the OpenEmbedded patch guidelines:

https://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines

The component being changed in the shortlog should be prefixed with the layer name (without the meta- prefix), for example:

  arm-bsp/trusted-firmware-a: decrease frobbing level

  arm-toolchain/gcc: enable foobar v2

Reporting bugs
--------------
E-mail meta-arm@lists.yoctoproject.org with the error encountered and the steps
to reproduce the issue.

Maintainer(s)
-------------
* Jon Mason <jon.mason@arm.com>
* Ross Burton <ross.burton@arm.com>
