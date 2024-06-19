..
 # Copyright (c) 2022-2024, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

#############
Release notes
#############


*************************
Disclaimer
*************************

You expressly assume all liabilities and risks relating to your use or operation
of Your Software and Your Hardware designed or modified using the Arm Tools,
including without limitation, Your software or Your Hardware designed or
intended for safety-critical applications. Should Your Software or Your Hardware
prove defective, you assume the entire cost of all necessary servicing, repair
or correction.

***********************
Release notes - 2024.06
***********************

Known Issues or Limitations
---------------------------

 - Use Ethernet over VirtIO due to lan91c111 Ethernet driver support dropped from U-Boot.
 - Due to the performance uplimit of MPS3 FPGA and FVP, some Linux distros like Fedora Rawhide can not boot on Corstone-1000 (i.e. user may experience timeouts or boot hang).
 - Corstone-1000 SoC on FVP doesn't have a secure debug peripheral. It does on the MPS3.
 - See previous release notes for the known limitations regarding ACS tests.

Platform Support
-----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v2
   https://developer.arm.com/downloads/-/download-fpga-images
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.23_25
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

***********************
Release notes - 2023.11
***********************

Known Issues or Limitations
---------------------------

 - Use Ethernet over VirtIO due to lan91c111 Ethernet driver support dropped from U-Boot.
 - Temporally removing the External system support in Linux due to it using multiple custom devicetree bindings that caused problems with SystemReady IR 2.0 certification. For External system support please refer to the version 2023.06. We are aiming to restore it in a more standardised manner in our next release.
 - Due to the performance uplimit of MPS3 FPGA and FVP, some Linux distros like Fedora Rawhide can not boot on Corstone-1000 (i.e. user may experience timeouts or boot hang).
 - PSA Crypto tests (psa-crypto-api-test command) approximately take 30 minutes to complete for FVP and MPS3.
 - Corstone-1000 SoC on FVP doesn't have a secure debug peripheral. It does on the MPS3.
 - See previous release notes for the known limitations regarding ACS tests.

Platform Support
-----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v2
   https://developer.arm.com/downloads/-/download-fpga-images
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.23_25
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

***********************
Release notes - 2023.06
***********************

Known Issues or Limitations
---------------------------
 - FPGA supports Linux distro install and boot through installer. However, FVP only supports openSUSE raw image installation and boot.
 - Due to the performance uplimit of MPS3 FPGA and FVP, some Linux distros like Fedora Rawhide can not boot on Corstone-1000 (i.e. user may experience timeouts or boot hang).
 - PSA Crypto tests (psa-crypto-api-test command) take 30 minutes to complete for FVP and 1 hour for MPS3.
 - Corstone-1000 SoC on FVP doesn't have a secure debug peripheral. It does on the MPS3 .
 - The following limitations listed in the previous release are still applicable:

   - UEFI Compliant - Boot from network protocols must be implemented -- FAILURE

   - Known limitations regarding ACS tests - see previous release's notes.

Platform Support
-----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v2
   https://developer.arm.com/downloads/-/download-fpga-images
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.19_21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

**************************
Release notes - 2022.11.23
**************************

Known Issues or Limitations
---------------------------
 - The external-system can not be reset individually on (or using) AN550_v1 FPGA release. However, the system-wide reset still applies to the external-system.
 - FPGA supports Linux distro install and boot through installer. However, FVP only supports openSUSE raw image installation and boot.
 - Due to the performance uplimit of MPS3 FPGA and FVP, some Linux distros like Fedora Rawhide can not boot on Corstone-1000 (i.e. user may experience timeouts or boot hang).
 - Below SCT FAILURE is a known issues in the FVP:
   UEFI Compliant - Boot from network protocols must be implemented -- FAILURE
 - Below SCT FAILURE is a known issue when a terminal emulator (in the system where the user connects to serial ports) does not support 80x25 or 80x50 mode:
   EFI_SIMPLE_TEXT_OUT_PROTOCOL.SetMode - SetMode() with valid mode -- FAILURE
 - Known limitations regarding ACS tests: The behavior after running ACS tests on FVP is not consistent.  Both behaviors are expected and are valid;
   The system might boot till the Linux prompt. Or, the system might wait after finishing the ACS tests.
   In both cases, the system executes the entire test suite and writes the results as stated in the user guide.


Platform Support
-----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v1
   https://developer.arm.com/downloads/-/download-fpga-images
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.19_21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

**************************
Release notes - 2022.04.04
**************************

Known Issues or Limitations
---------------------------
 - FPGA support Linux distro install and boot through installer. However,
   FVP only support openSUSE raw image installation and boot.
 - Due to the performance uplimit of MPS3 FPGA and FVP, some Linux distros like Fedora Rawhide
   cannot boot on Corstone-1000 (i.e. user may experience timeouts or boot hang).
 - Below SCT FAILURE is a known issues in the FVP:
   UEFI Compliant - Boot from network protocols must be implemented -- FAILURE

Platform Support
-----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v1
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.17_23
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

**************************
Release notes - 2022.02.25
**************************

Known Issues or Limitations
---------------------------
 - The following tests only work on Corstone-1000 FPGA: ACS tests (SCT, FWTS,
   BSA), manual capsule update test, Linux distro install and boot.

Platform Support
----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v1
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.17_23
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Release notes - 2022.02.21
--------------------------

Known Issues or Limitations
---------------------------
 - The following tests only work on Corstone-1000 FPGA: ACS tests (SCT, FWTS,
   BSA), manual capsule update test, Linux distro install and boot, psa-arch-test.

Platform Support
----------------
 - This software release is tested on Corstone-1000 FPGA version AN550_v1
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.16.21
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
 - This software release is tested on Corstone-1000 FPGA version AN550_v1
 - This software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.16.21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Known Issues or Limitations
---------------------------
 - The following tests only work on Corstone-1000 FPGA: ACS tests (SCT, FWTS,
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
This initial release of Corstone-1000 supports booting Linux on the Cortex-A35
and TF-M/MCUBOOT in the Secure Enclave. The following components are present in
the release:

 - Linux kernel version 5.10
 - U-Boot 2021.07
 - OP-TEE version 3.14
 - Trusted Firmware-A 2.5
 - Trusted Firmware-M 1.4

Platform Support
----------------
 - This Software release is tested on Corstone-1000 Fast Model platform (FVP) version 11.16.21
   https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps

Known Issues or Limitations
---------------------------
 - No software support for external system(Cortex M3)
 - No communication established between A35 and M0+
 - Very basic functionality of booting Secure Enclave, Trusted Firmware-A , OP-TEE , u-boot and Linux are performed

Support
-------
For technical support email: support-subsystem-iot@arm.com

For all security issues, contact Arm by email at psirt@arm.com.

--------------

*Copyright (c) 2022-2023, Arm Limited. All rights reserved.*
