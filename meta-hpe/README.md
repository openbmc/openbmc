OpenEmbedded/Yocto/OpenBMC BSP layer for Hewlett Packard Enterprise servers
====================================================

This layer supports OpenBMC firmware on supported Hewlett Packard Enterprise server
products.

The following system is supported.

## HPE DL360 Gen10 (Modified Proof of Concept)
The HPE DL360 Gen10 OpenBMC Proof of Concept is a two-socket, 1U general purpose server.
For more information, click [here](https://buy.hpe.com/us/en/servers/rack-servers/proliant-dl300-servers/proliant-dl360-server/hpe-proliant-dl360-gen10-server/p/1010007891)

**This is for experimental use only at this time.  The thermal management configuration (fan PID calibration) is not complete.**

HPE ProLiant Gen10 products contain a custom BMC ASIC (called "GXP" here) that includes Hewlett Packard Enterprise Silicon Root of Trust technology.  This feature was designed to ensure that only firmware released by Hewlett Packard Enterprise can run.  In order to enable customers and partners to work with open firmware including OpenBMC, the DL360 layer described here will run only on a modified server or on the open source firmware CI system at https://osfci.tech/ci/.

This boot process for the GXP ASIC begins with a binary bootblock before starting U-Boot,
Linux, and the OpenBMC services.

### Configuration
Source the `setup` script as follows:

```
. ./setup dl360poc
```

Build

```
bitbake obmc-phosphor-image
```

Additional machine configurations will be made available in the future.

Contributing
------------

meta-hpe patches are reviewed using the Gerrit instance at
https://gerrit.openbmc-project.xyz.

Please submit patches to Gerrit.  More information about using Gerrit can be found
[here](https://github.com/openbmc/docs/blob/meta-hpe/master/CONTRIBUTING.md#submitting-changes-via-gerrit-server).
https://github.com/openbmc/meta-hpe is a hosting mirror only.  GitHub
pull requests are not monitored and will not be accepted.

Patch checklist.  Please ensure that patches adhere to the following guidelines:

 - meta-hpe uses the [OE style
   guidelines](https://www.openembedded.org/wiki/Styleguide).
 - Follow [the seven rules of a great git commit
   message](https://chris.beams.io/posts/git-commit/#seven-rules)

For questions or help, join us on the [mailing
list](https://lists.ozlabs.org/listinfo/openbmc) or in
[IRC](irc://freenode.net/openbmc).
