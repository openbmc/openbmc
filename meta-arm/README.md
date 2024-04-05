Introduction
------------
This repository contains the Arm layers for OpenEmbedded.

* meta-arm

  This layer contains general recipes for the Arm architecture, such as firmware, FVPs, and Arm-specific integration.

* meta-arm-bsp

  This layer contains machines for Arm reference platforms, for example FVP Base, N1SDP, and Juno.

* meta-arm-toolchain

  This layer contains recipes for Arm's binary toolchains (GCC and Clang for -A and -M), and a recipe to build Arm's GCC.

Other Directories
-----------------

* ci

  This directory contains gitlab continuous integration configuration files (KAS yaml files) as well as scripts needed for this

* kas

  This directory contains KAS yaml files to describe builds for systems not used in CI

* scripts

  This directory contains scripts used in running the CI tests

Mailing List
------------
To interact with the meta-arm developer community, please email the meta-arm mailing list at meta-arm@lists.yoctoproject.org
Currently, it is configured to only allow emails to members from those subscribed.
To subscribe to the meta-arm mailing list, please go to
https://lists.yoctoproject.org/g/meta-arm

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

Releases and Release Schedule
--------------
We follow the Yocto Project release methodology, schedule, and stable/LTS support timelines.  For more information on these, please reference:
https://docs.yoctoproject.org/ref-manual/release-process.html
https://wiki.yoctoproject.org/wiki/Releases
https://wiki.yoctoproject.org/wiki/Stable_Release_and_LTS

Reporting bugs
--------------
E-mail meta-arm@lists.yoctoproject.org with the error encountered and the steps
to reproduce the issue.

Maintainer(s)
-------------
* Jon Mason <jon.mason@arm.com>
* Ross Burton <ross.burton@arm.com>
