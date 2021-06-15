OpenEmbedded/Yocto/OpenBMC BSP layer for OpenPOWER reference platforms
======================================================================

This layer provides support for the BMC firmware on OpenPOWER reference
platforms, and/or a base upon which to create a BMC firmware implementation on
any POWER system.

```
This layer depends on:

URI: git://git.openembedded.org/openembedded-core
layers: meta
branch: master
revision: HEAD

URI: https://github.com/openbmc/meta-phosphor
branch: master
revision: HEAD

URI: https://github.com/openbmc/meta-aspeed
branch: master
revision: HEAD
```

More information on OpenPOWER can be found
[here](https://openpowerfoundation.org/).

Contributing
------------

meta-openpower patches are reviewed using the Gerrit instance at
https://gerrit.openbmc-project.xyz.

Please submit patches to Gerrit.  More information on using Gerrit can be found
[here](https://github.com/openbmc/docs/blob/master/CONTRIBUTING.md#submitting-changes-via-gerrit-server).
https://github.com/openbmc/meta-openpower is a hosting mirror only and GitHub
pull requests are not monitored and will not be accepted.

Patch checklist.  Please ensure patches adhere to the following guidelines:

 - meta-openpower uses the [OE style
   guidelines](https://www.openembedded.org/wiki/Styleguide).
 - follow [the seven rules of a great git commit
   message](https://chris.beams.io/posts/git-commit/#seven-rules)

For questions or help please come join us on the [mailing
list](https://lists.ozlabs.org/listinfo/openbmc) or in
[IRC](irc://freenode.net/openbmc).
