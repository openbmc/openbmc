OpenEmbedded/Yocto/OpenBMC BSP layer for IBM systems
====================================================

This layer provides support for the BMC firmware on IBM POWER systems server
products.

```
This layer depends on:

URI: git://git.openembedded.org/openembedded-core
layers: meta
branch: master
revision: HEAD

URI: https://github.com/openbmc/meta-phosphor
branch: master
revision: HEAD

URI: https://github.com/openbmc/meta-openpower
branch: master
revision: HEAD

URI: https://github.com/openbmc/meta-aspeed
branch: master
revision: HEAD
```

The following systems are supported.

Witherspoon, or AC922, is an IBM POWER9 two-socket, 2U Accelerated Compute
Server with up to 6 NVIDIA Tesla GPUs. More information can be found
[here](https://www.ibm.com/us-en/marketplace/power-systems-ac922).

In addition to witherspoon, this layer contains additional machine
configurations such as swift. The default machine target is witherspoon,
so in order to build a different configuration, or to build witherspoon
after building a different one, set the MACHINE environment to the desired
configuration name (see the conf/machine/ subdirectory for available options):

    export MACHINE=machine_configuration_name

Then build:

    bitbake obmc-phosphor-image

Example:

    export MACHINE=swift
    bitbake obmc-phosphor-image
    bitbake phosphor-logging

    export MACHINE=witherspoon
    bitbake obmc-phosphor-image

Contributing
------------

meta-ibm patches are reviewed using the Gerrit instance at
https://gerrit.openbmc-project.xyz.

Please submit patches to Gerrit.  More information on using Gerrit can be found
[here](https://github.com/openbmc/docs/blob/master/CONTRIBUTING.md#submitting-changes-via-gerrit-server).
https://github.com/openbmc/meta-ibm is a hosting mirror only and GitHub
pull requests are not monitored and will not be accepted.

Patch checklist.  Please ensure patches adhere to the following guidelines:

 - meta-ibm uses the [OE style
   guidelines](https://www.openembedded.org/wiki/Styleguide).
 - follow [the seven rules of a great git commit
   message](https://chris.beams.io/posts/git-commit/#seven-rules)

For questions or help please come join us on the [mailing
list](https://lists.ozlabs.org/listinfo/openbmc) or in
[IRC](irc://freenode.net/openbmc).
