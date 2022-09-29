# TC0 Platform Support in meta-arm-bsp

## Overview
The Total Compute platform provides an envelope for all of Arm's latest IP and
software solutions, optimised to work together. Further information can be
found on the Total Compute community page:
https://community.arm.com/developer/tools-software/oss-platforms/w/docs/606/total-compute

The user guide for TC0 platform with detailed instructions for
syncing and building the source code and running on TC0 Fixed Virtual Platform
for poky and android distributions is available at:
https://git.linaro.org/landing-teams/working/arm/arm-reference-platforms.git/tree/docs/tc0/user-guide.rst

## Building
In the local.conf file, MACHINE should be set as follows:
MACHINE = "tc0"

To build the required binaries for tc0, run the commmand:
```bash$ bitbake tc-artifacts-image```

Trusted-firmware-a is the final component to be built with the rest of the
components dependent of it, therefore building tc-artifacts-image which depends
on trusted-firmware-a will build all the required binaries.

## Running
To run the produced binaries in a TC0 Fixed Virtual Platform please get
the run scripts at:
https://git.linaro.org/landing-teams/working/arm/model-scripts.git/

and follow the instructions in the user-guide.rst available in:
https://git.linaro.org/landing-teams/working/arm/arm-reference-platforms.git/tree/docs/tc0/user-guide.rst

