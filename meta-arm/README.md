Introduction
------------
This repository contains the Arm layers for OpenEmbedded.

* meta-arm

  This layer contains general recipes for the Arm architecture, such as firmware, FVPs, and Arm-specific integration.

* meta-arm-bsp

  This layer contains machines for Arm reference platforms, for example FVP Base, Corstone1000, and Juno.

* meta-arm-toolchain

  This layer contains recipes for Arm's binary toolchains (GCC and Clang for -A and -M), and a recipe to build Arm's GCC.

Other Directories
-----------------

* ci

  This directory contains gitlab continuous integration configuration files (KAS yaml files) as well as scripts needed for this.

* documentation

  This directory contains information on the files in this repository, building, and other relevant documents.

* kas

  This directory contains KAS yaml files to describe builds for systems not used in CI.

* scripts

  This directory contains scripts used in running the CI tests.

Mailing List
------------
To interact with the meta-arm developer community, please email the meta-arm mailing list at <meta-arm@lists.yoctoproject.org>.
Currently, it is configured to only allow emails to members from those subscribed.
To subscribe to the meta-arm mailing list, please go to
https://lists.yoctoproject.org/g/meta-arm

Contributing
------------
Currently, we only accept patches from the meta-arm mailing list.  For general
information on how to submit a patch, please read
https://www.openembedded.org/wiki/How_to_submit_a_patch_to_OpenEmbedded

E-mail <meta-arm@lists.yoctoproject.org> with patches created using this process. You can configure git-send-email to automatically use this address for the meta-arm repository with the following git command:

`$ git config --local --add sendemail.to meta-arm@lists.yoctoproject.org`

Commits and patches added should follow the OpenEmbedded patch guidelines:

https://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines

The component being changed in the shortlog should be prefixed with the layer name (without the meta- prefix), for example:
>  arm-bsp/trusted-firmware-a: decrease frobbing level

>  arm-toolchain/gcc: enable foobar v2

All contributions are under the [MIT License](/COPYING.MIT).

For a quick start guide on how to build and use meta-arm, go to [quick-start.md](/documentation/quick-start.md).

For information on the continuous integration done on meta-arm and how to use it, go to [continuous-integration-and-kas.md](/documentation/continuous-integration-and-kas.md).

Backporting
--------------
Backporting patches to older releases may be done upon request, but only after a version of the patch has been accepted into the master branch.  This is done by adding the branch name to email subject line.  This should be between the square brackets (e.g., "[" and "]"), and before or after the "PATCH".  For example,
> [nanbield PATCH] arm/linux-yocto: backport patch to fix 6.5.13 networking issues

Automatic backporting will be done to all branches if the "Fixes: <SHA>" wording is added to the patch commit message.  This is similar to how the Linux kernel community does their LTS kernel backporting.  For more information see the "Fixes" portion of
https://www.kernel.org/doc/html/latest/process/submitting-patches.html#submittingpatches

Releases and Release Schedule
--------------
We follow the Yocto Project release methodology, schedule, and stable/LTS support timelines.  For more information on these, please reference:
* https://docs.yoctoproject.org/ref-manual/release-process.html
* https://wiki.yoctoproject.org/wiki/Releases
* https://wiki.yoctoproject.org/wiki/Stable_Release_and_LTS

For more in-depth information on the meta-arm release and branch methodology, go to </documentation/releases.md>.

Reporting bugs
--------------
E-mail <meta-arm@lists.yoctoproject.org> with the error encountered and the steps
to reproduce the issue.

Security and Reporting Security Issues
--------------
For information on the security of meta-arm and how to report issues, please consult [SECURITY.md](/SECURITY.md).

Maintainer(s)
-------------
* Jon Mason <jon.mason@arm.com>
* Ross Burton <ross.burton@arm.com>
