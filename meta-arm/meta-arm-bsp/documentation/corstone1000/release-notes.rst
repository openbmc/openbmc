..
 # Copyright (c) 2022, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

#############
Release notes
#############

**************************
Release notes - 2022.04.04
**************************

Known Issues or Limitations
---------------------------
 - FGPA support Linux distro install and boot through installer. However,
   FVP only support openSUSE raw image installation and boot.
 - Due to the performance uplimit of MPS3 FPGA and FVP, some Linux distros like Fedora Rawhide
   cannot boot on corstone1000 (i.e. user may experience timeouts or boot hang).
 - Below SCT FAILURE is a known issues in the FVP:
   UEFI Compliant - Boot from network protocols must be implemented -- FAILURE

Platform Support
-----------------
 - This software release is tested on corstone1000 FPGA version AN550_v1
 - This software release is tested on corstone1000 Fast Model platform (FVP) version 11.17_23
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

**************************
Release notes - 2022.02.25
**************************

Known Issues or Limitations
---------------------------
 - The following tests only work on corstone1000 FPGA: ACS tests (SCT, FWTS,
   BSA), manual capsule update test, Linux distro install and boot.

Platform Support
----------------
 - This software release is tested on corstone1000 FPGA version AN550_v1
 - This software release is tested on corstone1000 Fast Model platform (FVP) version 11.17_23
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Release notes - 2022.02.21
--------------------------

Known Issues or Limitations
---------------------------
 - The following tests only work on corstone1000 FPGA: ACS tests (SCT, FWTS,
   BSA), manual capsule update test, Linux distro install and boot, psa-arch-test.

Platform Support
----------------
 - This software release is tested on corstone1000 FPGA version AN550_v1
 - This software release is tested on corstone1000 Fast Model platform (FVP) version 11.16.21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Release notes - 2022.01.18
--------------------------

Known Issues or Limitations
---------------------------

 - Before running each SystemReady-IR tests: ACS tests (SCT, FWTS, BSA), manual
   capsule update test, Linux distro install and boot, etc., the SecureEnclave
   flash must be cleaned. See user-guide "Clean Secure Flash Before Testing"
   section.

Release notes - 2021.12.15
--------------------------

Software Features
------------------
The following components are present in the release:

 - Yocto version Honister
 - Linux kernel version 5.10
 - U-Boot 2021.07
 - OP-TEE version 3.14
 - Trusted Firmware-A 2.5
 - Trusted Firmware-M 1.5
 - OpenAMP 347397decaa43372fc4d00f965640ebde042966d
 - Trusted Services a365a04f937b9b76ebb2e0eeade226f208cbc0d2


Platform Support
----------------
 - This software release is tested on corstone1000 FPGA version AN550_v1
 - This software release is tested on corstone1000 Fast Model platform (FVP) version 11.16.21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Known Issues or Limitations
---------------------------
 - The following tests only work on corstone1000 FPGA: ACS tests (SCT, FWTS,
   BSA), manual capsule update test, Linux distro install and boot, and
   psa-arch-tests.
 - Only the manual capsule update from UEFI shell is supported on FPGA.
 - Due to flash size limitation and to support A/B banks,the wic image provided
   by the user should be smaller than 15MB.
 - The failures in PSA Arch Crypto Test are known limitations with crypto
   library. It requires further investigation. The user can refer to `PSA Arch Crypto Test Failure Analysis In TF-M V1.5 Release <https://developer.trustedfirmware.org/w/tf_m/release/psa_arch_crypto_test_failure_analysis_in_tf-m_v1.5_release/>`__
   for the reason for each failing test.


Release notes - 2021.10.29
--------------------------

Software Features
-----------------
This initial release of corstone1000 supports booting Linux on the Cortex-A35
and TF-M/MCUBOOT in the Secure Enclave. The following components are present in
the release:

 - Linux kernel version 5.10
 - U-Boot 2021.07
 - OP-TEE version 3.14
 - Trusted Firmware-A 2.5
 - Trusted Firmware-M 1.4

Platform Support
----------------
 - This Software release is tested on corstone1000 Fast Model platform (FVP) version 11.16.21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Known Issues or Limitations
---------------------------
 - No software support for external system(Cortex M3)
 - No communication established between A35 and M0+
 - Very basic functionality of booting Secure Enclave, Trusted Firmware-A , OP-TEE , u-boot and Linux are performed

Support
-------
For support email: support-subsystem-iot@arm.com

--------------

*Copyright (c) 2021, Arm Limited. All rights reserved.*
