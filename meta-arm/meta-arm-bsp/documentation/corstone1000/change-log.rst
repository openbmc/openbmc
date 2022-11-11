..
 # Copyright (c) 2022, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

##########
Change Log
##########

This document contains a summary of the new features, changes and
fixes in each release of corstone1000 software stack.

******************
Version 2022.04.04
******************

Changes
=======
- Linux distro openSUSE, raw image installation and boot in the FVP.
- SCT test support in FVP.
- Manual capsule update support in FVP.

******************
Version 2022.02.25
******************

Changes
=======
- Building and running psa-arch-tests on corstone1000 FVP
- Enabled smm-gateway partition in Trusted Service on corstone1000 FVP
- Enabled MHU driver in Trusted Service on corstone1000 FVP
- Enabled OpenAMP support in SE proxy SP on corstone1000 FVP

******************
Version 2022.02.21
******************

Changes
=======
- psa-arch-tests: recipe is dropped and merged into the secure-partitons recipe.
- psa-arch-tests: The tests are align with latest tfm version for psa-crypto-api suite.

******************
Version 2022.01.18
******************

Changes
=======
- psa-arch-tests: change master to main for psa-arch-tests
- U-Boot: fix null pointer exception for get_image_info
- TF-M: fix capsule instability issue for corstone1000

******************
Version 2022.01.07
******************

Changes
=======
- corstone1000: fix SystemReady-IR ACS test (SCT, FWTS) failures.
- U-Boot: send bootcomplete event to secure enclave.
- U-Boot: support populating corstone1000 image_info to ESRT table.
- U-Boot: add ethernet device and enable configs to support bootfromnetwork SCT.

******************
Version 2021.12.15
******************

Changes
=======
- Enabling corstone1000 FPGA support on:
  - Linux 5.10
  - OP-TEE 3.14
  - Trusted Firmware-A 2.5
  - Trusted Firmware-M 1.5
- Building and running psa-arch-tests
- Adding openamp support in SE proxy SP
- OP-TEE: adding smm-gateway partition
- U-Boot: introducing Arm FF-A and MM support

******************
Version 2021.10.29
******************

Changes
=======
- Enabling corstone1000 FVP support on:
  - Linux 5.10
  - OP-TEE 3.14
  - Trusted Firmware-A 2.5
  - Trusted Firmware-M 1.4
- Linux kernel: enabling EFI, adding FF-A debugfs driver, integrating ARM_FFA_TRANSPORT.
- U-Boot: Extending EFI support
- python3-imgtool: adding recipe for Trusted-firmware-m
- python3-imgtool: adding the Yocto recipe used in signing host images (based on MCUBOOT format)

--------------

*Copyright (c) 2021, Arm Limited. All rights reserved.*
