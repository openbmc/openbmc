..
 # Copyright (c) 2022, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

##########
Change Log
##########

This document contains a summary of the new features, changes and
fixes in each release of Corstone-1000 software stack.

******************
Version 2022.11.23
******************

Changes
=======

- Booting the External System (Cortex-M3) with RTX RTOS
- Adding MHU communication between the HOST (Cortex-A35) and the External System
- Adding a Linux application to test the External System
- Adding ESRT (EFI System Resource Table) support
- Upgrading the SW stack recipes
- Upgrades for the U-Boot FF-A driver and MM communication

Corstone-1000 components versions
=======================================

+-------------------------------------------+------------+
| arm-ffa-tee                               | 1.1.1      |
+-------------------------------------------+------------+
| arm-ffa-user                              | 5.0.0      |
+-------------------------------------------+------------+
| corstone1000-external-sys-tests           | 1.0        |
+-------------------------------------------+------------+
| external-system                           | 0.1.0      |
+-------------------------------------------+------------+
| linux-yocto                               | 5.19       |
+-------------------------------------------+------------+
| u-boot                                    | 2022.07    |
+-------------------------------------------+------------+
| optee-client                              | 3.18.0     |
+-------------------------------------------+------------+
| optee-os                                  | 3.18.0     |
+-------------------------------------------+------------+
| trusted-firmware-a                        | 2.7.0      |
+-------------------------------------------+------------+
| trusted-firmware-m                        | 1.6.0      |
+-------------------------------------------+------------+
| ts-newlib                                 | 4.1.0      |
+-------------------------------------------+------------+
| ts-psa-{crypto, iat, its. ps}-api-test    | 451aa087a4 |
+-------------------------------------------+------------+
| ts-sp-{se-proxy, smm-gateway}             | 3d4956770f |
+-------------------------------------------+------------+

Yocto distribution components versions
=======================================

+-------------------------------------------+---------------------+
| meta-arm                                  | langdale            |
+-------------------------------------------+---------------------+
| poky                                      | langdale            |
+-------------------------------------------+---------------------+
| meta-openembedded                         | langdale            |
+-------------------------------------------+---------------------+
| busybox                                   | 1.35.0              |
+-------------------------------------------+---------------------+
| musl                                      | 1.2.3+git37e18b7bf3 |
+-------------------------------------------+---------------------+
| gcc-arm-none-eabi-native                  | 11.2-2022.02        |
+-------------------------------------------+---------------------+
| gcc-cross-aarch64                         | 12.2                |
+-------------------------------------------+---------------------+
| openssl                                   | 3.0.5               |
+-------------------------------------------+---------------------+

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
- Building and running psa-arch-tests on Corstone-1000 FVP
- Enabled smm-gateway partition in Trusted Service on Corstone-1000 FVP
- Enabled MHU driver in Trusted Service on Corstone-1000 FVP
- Enabled OpenAMP support in SE proxy SP on Corstone-1000 FVP

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
- TF-M: fix capsule instability issue for Corstone-1000

******************
Version 2022.01.07
******************

Changes
=======
- Corstone-1000: fix SystemReady-IR ACS test (SCT, FWTS) failures.
- U-Boot: send bootcomplete event to secure enclave.
- U-Boot: support populating Corstone-1000 image_info to ESRT table.
- U-Boot: add ethernet device and enable configs to support bootfromnetwork SCT.

******************
Version 2021.12.15
******************

Changes
=======
- Enabling Corstone-1000 FPGA support on:
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
- Enabling Corstone-1000 FVP support on:
  - Linux 5.10
  - OP-TEE 3.14
  - Trusted Firmware-A 2.5
  - Trusted Firmware-M 1.4
- Linux kernel: enabling EFI, adding FF-A debugfs driver, integrating ARM_FFA_TRANSPORT.
- U-Boot: Extending EFI support
- python3-imgtool: adding recipe for Trusted-firmware-m
- python3-imgtool: adding the Yocto recipe used in signing host images (based on MCUBOOT format)

--------------

*Copyright (c) 2022, Arm Limited. All rights reserved.*
