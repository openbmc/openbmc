..
 # Copyright (c) 2022-2023, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

##########
User Guide
##########

Notice
------
The Corstone-1000 software stack uses the `Yocto Project <https://www.yoctoproject.org/>`__ to build
a tiny Linux distribution suitable for the Corstone-1000 platform (kernel and initramfs filesystem less than 5 MB on the flash).
The Yocto Project relies on the `Bitbake <https://docs.yoctoproject.org/bitbake.html#bitbake-documentation>`__
tool as its build tool. Please see `Yocto Project documentation <https://docs.yoctoproject.org/>`__
for more information.

Prerequisites
-------------

This guide assumes that your host PC is running Ubuntu 20.04 LTS, with at least
32GB of free disk space and 16GB of RAM as minimum requirement.

The following prerequisites must be available on the host system:

- Git 1.8.3.1 or greater
- tar 1.28 or greater
- Python 3.8.0 or greater.
- gcc 8.0 or greater.
- GNU make 4.0 or greater

Please follow the steps described in the Yocto mega manual:

- `Compatible Linux Distribution <https://docs.yoctoproject.org/singleindex.html#compatible-linux-distribution>`__
- `Build Host Packages <https://docs.yoctoproject.org/singleindex.html#build-host-packages>`__

Targets
-------

- `Arm Corstone-1000 Ecosystem FVP (Fixed Virtual Platform) <https://developer.arm.com/downloads/-/arm-ecosystem-fvps>`__
- `Arm Corstone-1000 for MPS3 <https://developer.arm.com/documentation/dai0550/latest/>`__

Yocto stable branch
-------------------

Corstone-1000 software stack is built on top of Yocto mickledore.

Provided components
-------------------
Within the Yocto Project, each component included in the Corstone-1000 software stack is specified as
a `bitbake recipe <https://docs.yoctoproject.org/bitbake/2.2/bitbake-user-manual/bitbake-user-manual-intro.html#recipes>`__.
The recipes specific to the Corstone-1000 BSP are located at:
``<_workspace>/meta-arm/meta-arm-bsp/``.

The Yocto machine config files for the Corstone-1000 FVP and FPGA targets are:

 - ``<_workspace>/meta-arm/meta-arm-bsp/conf/machine/include/corstone1000.inc``
 - ``<_workspace>/meta-arm/meta-arm-bsp/conf/machine/corstone1000-fvp.conf``
 - ``<_workspace>/meta-arm/meta-arm-bsp/conf/machine/corstone1000-mps3.conf``

**NOTE:** All the paths stated in this document are absolute paths.

*****************
Software for Host
*****************

Trusted Firmware-A
==================
Based on `Trusted Firmware-A <https://git.trustedfirmware.org/TF-A/trusted-firmware-a.git>`__

+----------+-----------------------------------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.8.%.bbappend |
+----------+-----------------------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.8.0.bb           |
+----------+-----------------------------------------------------------------------------------------------------+

OP-TEE
======
Based on `OP-TEE <https://git.trustedfirmware.org/OP-TEE/optee_os.git>`__

+----------+------------------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-security/optee/optee-os_3.20.0.bbappend |
+----------+------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm/recipes-security/optee/optee-os_3.20.0.bb           |
+----------+------------------------------------------------------------------------------------+

U-Boot
======
Based on `U-Boot repo`_

+----------+-------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm/recipes-bsp/u-boot/u-boot_%.bbappend     |
+----------+-------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-bsp/u-boot/u-boot_%.bbappend |
+----------+-------------------------------------------------------------------------+
| Recipe   | <_workspace>/poky/meta/recipes-bsp/u-boot/u-boot_2023.01.bb             |
+----------+-------------------------------------------------------------------------+

Linux
=====
The distro is based on the `poky-tiny <https://wiki.yoctoproject.org/wiki/Poky-Tiny>`__
distribution which is a Linux distribution stripped down to a minimal configuration.

The provided distribution is based on busybox and built using musl libc. The
recipe responsible for building a tiny version of Linux is listed below.

+-----------+----------------------------------------------------------------------------------------------+
| bbappend  | <_workspace>/meta-arm/meta-arm-bsp/recipes-kernel/linux/linux-yocto_%.bbappend               |
+-----------+----------------------------------------------------------------------------------------------+
| Recipe    | <_workspace>/poky/meta/recipes-kernel/linux/linux-yocto_6.1.bb                               |
+-----------+----------------------------------------------------------------------------------------------+
| defconfig | <_workspace>/meta-arm/meta-arm-bsp/recipes-kernel/linux/files/corstone1000/defconfig         |
+-----------+----------------------------------------------------------------------------------------------+

External System Tests
=====================
Based on `Corstone-1000/applications <https://git.gitlab.arm.com/arm-reference-solutions/corstone1000/applications>`__

+------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Recipe     | <_workspace>/meta-arm/meta-arm-bsp/recipes-test/corstone1000-external-sys-tests/corstone1000-external-sys-tests_1.0.bb                                                                              |
+------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

The recipe provides the systems-comms-tests command run in Linux and used for testing the External System.

**************************************************
Software for Boot Processor (a.k.a Secure Enclave)
**************************************************
Based on `Trusted Firmware-M <https://git.trustedfirmware.org/TF-M/trusted-firmware-m.git>`__

+----------+-----------------------------------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-m/trusted-firmware-m_1.7.%.bbappend |
+----------+-----------------------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm/recipes-bsp/trusted-firmware-m/trusted-firmware-m_1.7.0.bb           |
+----------+-----------------------------------------------------------------------------------------------------+

********************************
Software for the External System
********************************

RTX
====
Based on `RTX RTOS <https://git.gitlab.arm.com/arm-reference-solutions/corstone1000/external_system/rtx>`__

+----------+-------------------------------------------------------------------------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm-bsp/recipes-bsp/external-system/external-system_0.1.0.bb                                                               |
+----------+-------------------------------------------------------------------------------------------------------------------------------------------------------+

Building the software stack
---------------------------
Create a new folder that will be your workspace and will henceforth be referred
to as ``<_workspace>`` in these instructions. To create the folder, run:

::

    mkdir <_workspace>
    cd <_workspace>

Corstone-1000 software is based on the Yocto Project which uses kas and bitbake
commands to build the stack. To install kas tool, run:

::

    pip3 install kas

If 'kas' command is not found in command-line, please make sure the user installation directories are visible on $PATH. If you have sudo rights, try 'sudo pip3 install kas'. 

In the top directory of the workspace ``<_workspace>``, run:

::

    git clone https://git.yoctoproject.org/git/meta-arm -b CORSTONE1000-2023.06

To build a Corstone-1000 image for MPS3 FPGA, run:

::

    kas build meta-arm/kas/corstone1000-mps3.yml

Alternatively, to build a Corstone-1000 image for FVP, you need to accept
the EULA at https://developer.arm.com/downloads/-/arm-ecosystem-fvps/eula
by setting the ARM_FVP_EULA_ACCEPT environment variable as follows:

::
  
    export ARM_FVP_EULA_ACCEPT="True"

then run:

::

    kas build meta-arm/kas/corstone1000-fvp.yml

The initial clean build will be lengthy, given that all host utilities are to
be built as well as the target images. This includes host executables (python,
cmake, etc.) and the required toolchain(s).

Once the build is successful, all output binaries will be placed in the following folders:
 - ``<_workspace>/build/tmp/deploy/images/corstone1000-fvp/`` folder for FVP build;
 - ``<_workspace>/build/tmp/deploy/images/corstone1000-mps3/`` folder for FPGA build.

Everything apart from the Secure Enclave ROM firmware and External System firmware, is bundled into a single binary, the
``corstone1000-image-corstone1000-{mps3,fvp}.wic`` file.

The output binaries run in the Corstone-1000 platform are the following:
 - The Secure Enclave ROM firmware: ``<_workspace>/build/tmp/deploy/images/corstone1000-{mps3,fvp}/bl1.bin``
 - The External System firmware: ``<_workspace>/build/tmp/deploy/images/corstone1000-{mps3,fvp}/es_flashfw.bin``
 - The flash image: ``<_workspace>/build/tmp/deploy/images/corstone1000-{mps3,fvp}/corstone1000-image-corstone1000-{mps3,fvp}.wic``

Flash the firmware image on FPGA
--------------------------------

The user should download the FPGA bit file image ``AN550:  Arm® Corstone™-1000 for MPS3 Version 2.0``
from `this link <https://developer.arm.com/tools-and-software/development-boards/fpga-prototyping-boards/download-fpga-images>`__
and under the section ``Arm® Corstone™-1000 for MPS3``. The download is available after logging in.

The directory structure of the FPGA bundle is shown below.

::

   Boardfiles
   ├── config.txt
   ├── MB
   │   ├── BRD_LOG.TXT
   │   ├── HBI0309B
   │   │   ├── AN550
   │   │   │   ├── AN550_v2.bit
   │   │   │   ├── an550_v2.txt
   │   │   │   └── images.txt
   │   │   ├── board.txt
   │   │   └── mbb_v210.ebf
   │   └── HBI0309C
   │       ├── AN550
   │       │   ├── AN550_v2.bit
   │       │   ├── an550_v2.txt
   │       │   └── images.txt
   │       ├── board.txt
   │       └── mbb_v210.ebf
   └── SOFTWARE
        ├── an550_st.axf
        ├── bl1.bin
        ├── cs1000.bin
        └── ES0.bin

Depending upon the MPS3 board version (printed on the MPS3 board) you should update the images.txt file
(in corresponding HBI0309x folder. Boardfiles/MB/HBI0309<board_revision>/AN550/images.txt) so that the file points to the images under SOFTWARE directory.

The images.txt file that is compatible with the latest version of the software
stack can be seen below;

::

  ;************************************************
  ;       Preload port mapping                    *
  ;************************************************
  ;  PORT 0 & ADDRESS: 0x00_0000_0000 QSPI Flash (XNVM) (32MB)
  ;  PORT 0 & ADDRESS: 0x00_8000_0000 OCVM (DDR4 2GB)
  ;  PORT 1        Secure Enclave (M0+) ROM (64KB)
  ;  PORT 2        External System 0 (M3) Code RAM (256KB)
  ;  PORT 3        Secure Enclave OTP memory (8KB)
  ;  PORT 4        CVM (4MB)
  ;************************************************

  [IMAGES]
  TOTALIMAGES: 3      ;Number of Images (Max: 32)
   
  IMAGE0PORT: 1
  IMAGE0ADDRESS: 0x00_0000_0000
  IMAGE0UPDATE: RAM
  IMAGE0FILE: \SOFTWARE\bl1.bin
   
  IMAGE1PORT: 0
  IMAGE1ADDRESS: 0x00_0000_0000
  IMAGE1UPDATE: AUTOQSPI
  IMAGE1FILE: \SOFTWARE\cs1000.bin
   
  IMAGE2PORT: 2
  IMAGE2ADDRESS: 0x00_0000_0000
  IMAGE2UPDATE: RAM
  IMAGE2FILE: \SOFTWARE\es0.bin

OUTPUT_DIR = ``<_workspace>/build/tmp/deploy/images/corstone1000-mps3``

1. Copy ``bl1.bin`` from OUTPUT_DIR directory to SOFTWARE directory of the FPGA bundle.
2. Copy ``es_flashfw.bin`` from OUTPUT_DIR directory to SOFTWARE directory of the FPGA bundle
   and rename the binary to ``es0.bin``.
3. Copy ``corstone1000-image-corstone1000-mps3.wic`` from OUTPUT_DIR directory to SOFTWARE
   directory of the FPGA bundle and rename the wic image to ``cs1000.bin``.

**NOTE:** Renaming of the images are required because MCC firmware has
limitation of 8 characters before .(dot) and 3 characters after .(dot).

Now, copy the entire folder to board's SDCard and reboot the board.

Running the software on FPGA
----------------------------

On the host machine, open 4 serial port terminals. In case of Linux machine it will
be ttyUSB0, ttyUSB1, ttyUSB2, ttyUSB3 and it might be different on Windows machines.

  - ttyUSB0 for MCC, OP-TEE and Secure Partition
  - ttyUSB1 for Boot Processor (Cortex-M0+)
  - ttyUSB2 for Host Processor (Cortex-A35)
  - ttyUSB3 for External System Processor (Cortex-M3)

Run following commands to open serial port terminals on Linux:

::

  sudo picocom -b 115200 /dev/ttyUSB0  # in one terminal
  sudo picocom -b 115200 /dev/ttyUSB1  # in another terminal
  sudo picocom -b 115200 /dev/ttyUSB2  # in another terminal.
  sudo picocom -b 115200 /dev/ttyUSB3  # in another terminal.

**NOTE:** The MPS3 expects an ethernet cable to be plugged in, otherwise it will
wait for the network for a considerable amount of time, printing the following
logs:

::

  Generic PHY 40100000.ethernet-ffffffff:01: attached PHY driver (mii_bus:phy_addr=40100000.ethernet-ffffffff:01, irq=POLL)
  smsc911x 40100000.ethernet eth0: SMSC911x/921x identified at 0xffffffc008e50000, IRQ: 17
  Waiting up to 100 more seconds for network.

Once the system boot is completed, you should see console
logs on the serial port terminals. Once the HOST(Cortex-A35) is
booted completely, user can login to the shell using
**"root"** login.

If system does not boot and only the ttyUSB1 logs are visible, please follow the
steps in `Clean Secure Flash Before Testing (applicable to FPGA only)`_ under
`SystemReady-IR tests`_ section. The previous image used in FPGA (MPS3) might
have filled the Secure Flash completely. The best practice is to clean the
secure flash in this case.


Running the software on FVP
---------------------------

An FVP (Fixed Virtual Platform) model of the Corstone-1000 platform must be available to run the
Corstone-1000 FVP software image.

A Yocto recipe is provided and allows to download the latest supported FVP version.

The recipe is located at <_workspace>/meta-arm/meta-arm/recipes-devtools/fvp/fvp-corstone1000.bb

The latest supported Fixed Virtual Platform (FVP) version is 11.19_21 and is automatically downloaded and installed when using the runfvp command as detailed below. The FVP version can be checked by running the following command:

::

<_workspace>/meta-arm/scripts/runfvp <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf -- --version

The FVP can also be manually downloaded from the `Arm Ecosystem FVPs`_ page. On this page, navigate
to "Corstone IoT FVPs" section to download the Corstone-1000 platform FVP installer.  Follow the
instructions of the installer and setup the FVP.

To run the FVP using the runfvp command, please run the following command:

::

<_workspace>/meta-arm/scripts/runfvp --terminals=xterm <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf

When the script is executed, three terminal instances will be launched, one for the boot processor
(aka Secure Enclave) processing element and two for the Host processing element. Once the FVP is
executing, the Boot Processor will start to boot, wherein the relevant memory contents of the .wic
file are copied to their respective memory locations within the model, enforce firewall policies
on memories and peripherals and then, bring the host out of reset.

The host will boot trusted-firmware-a, OP-TEE, U-Boot and then Linux, and present a login prompt
(FVP host_terminal_0):

::

    corstone1000-fvp login:

Login using the username root.

The External System can be released out of reset on demand using the systems-comms-tests command.

SystemReady-IR tests
--------------------

*************
Testing steps
*************

**NOTE**: Running the SystemReady-IR tests described below requires the user to
work with USB sticks. In our testing, not all USB stick models work well with
MPS3 FPGA. Here are the USB sticks models that are stable in our test
environment.

 - HP V165W 8 GB USB Flash Drive
 - SanDisk Ultra 32GB Dual USB Flash Drive USB M3.0
 - SanDisk Ultra 16GB Dual USB Flash Drive USB M3.0

**NOTE**:
Before running each of the tests in this chapter, the user should follow the
steps described in following section "Clean Secure Flash Before Testing" to
erase the SecureEnclave flash cleanly and prepare a clean board environment for
the testing.

Clean Secure Flash Before Testing (applicable to FPGA only)
===========================================================

To prepare a clean board environment with clean secure flash for the testing,
the user should prepare an image that erases the secure flash cleanly during
boot. Run following commands to build such image.

::

  cd <_workspace>
  git clone https://git.yoctoproject.org/git/meta-arm -b CORSTONE1000-2023.06
  git clone https://git.gitlab.arm.com/arm-reference-solutions/systemready-patch.git -b CORSTONE1000-2023.06
  cp -f systemready-patch/embedded-a/corstone1000/erase_flash/0001-embedded-a-corstone1000-clean-secure-flash.patch meta-arm
  cd meta-arm
  git apply 0001-embedded-a-corstone1000-clean-secure-flash.patch
  cd ..
  kas build meta-arm/kas/corstone1000-mps3.yml

Replace the bl1.bin and cs1000.bin files on the SD card with following files:
  - The ROM firmware: <_workspace>/build/tmp/deploy/images/corstone1000-mps3/bl1.bin
  - The flash image: <_workspace>/build/tmp/deploy/images/corstone1000-mps3/corstone1000-image-corstone1000-mps3.wic

Now reboot the board. This step erases the Corstone-1000 SecureEnclave flash
completely, the user should expect following message from TF-M log (can be seen
in ttyUSB1):

::

  !!!SECURE FLASH HAS BEEN CLEANED!!!
  NOW YOU CAN FLASH THE ACTUAL CORSTONE1000 IMAGE
  PLEASE REMOVE THE LATEST ERASE SECURE FLASH PATCH AND BUILD THE IMAGE AGAIN

Then the user should follow "Building the software stack" to build a clean
software stack and flash the FPGA as normal. And continue the testing.

Run SystemReady-IR ACS tests
============================

Architecture Compliance Suite (ACS) is used to ensure architectural compliance
across different implementations of the architecture. Arm Enterprise ACS
includes a set of examples of the invariant behaviors that are provided by a
set of specifications for enterprise systems (For example: SBSA, SBBR, etc.),
so that implementers can verify if these behaviours have been interpreted correctly.

ACS image contains two partitions. BOOT partition and RESULT partition.
Following test suites and bootable applications are under BOOT partition:

 * SCT
 * FWTS
 * BSA uefi
 * BSA linux
 * grub
 * uefi manual capsule application

BOOT partition contains the following:

::

    ├── EFI
    │   └── BOOT
    │       ├── app
    │       ├── bbr
    │       ├── bootaa64.efi
    │       ├── bsa
    │       ├── debug
    │       ├── Shell.efi
    │       └── startup.nsh
    ├── grub
    ├── grub.cfg
    ├── Image
    └── ramdisk-busybox.img

RESULT partition is used to store the test results.
**NOTE**: PLEASE MAKE SURE THAT THE RESULT PARTITION IS EMPTY BEFORE YOU START THE TESTING. OTHERWISE THE TEST RESULTS
WILL NOT BE CONSISTENT

FPGA instructions for ACS image
===============================

This section describes how the user can build and run Architecture Compliance
Suite (ACS) tests on Corstone-1000.

First, the user should download the `Arm SystemReady ACS repository <https://github.com/ARM-software/arm-systemready/>`__.
This repository contains the infrastructure to build the Architecture
Compliance Suite (ACS) and the bootable prebuilt images to be used for the
certifications of SystemReady-IR. To download the repository, run command:

::

  cd <_workspace>
  git clone https://github.com/ARM-software/arm-systemready.git -b v21.09_REL1.0

Once the repository is successfully downloaded, the prebuilt ACS live image can be found in:
 - ``<_workspace>/arm-systemready/IR/prebuilt_images/v21.07_0.9_BETA/ir_acs_live_image.img.xz``

**NOTE**: This prebuilt ACS image includes v5.13 kernel, which doesn't provide
USB driver support for Corstone-1000. The ACS image with newer kernel version
and with full USB support for Corstone-1000 will be available in the next
SystemReady release in this repository.

Then, the user should prepare a USB stick with ACS image. In the given example here,
we assume the USB device is ``/dev/sdb`` (the user should use ``lsblk`` command to
confirm). Be cautious here and don't confuse your host PC's own hard drive with the
USB drive. Run the following commands to prepare the ACS image in USB stick:

::

  cd <_workspace>/arm-systemready/IR/prebuilt_images/v21.07_0.9_BETA
  unxz ir_acs_live_image.img.xz
  sudo dd if=ir_acs_live_image.img of=/dev/sdb iflag=direct oflag=direct bs=1M status=progress; sync

Once the USB stick with ACS image is prepared, the user should make sure that
ensure that only the USB stick with the ACS image is connected to the board,
and then boot the board.

The FPGA will reset multiple times during the test, and it might take approx. 24-36 hours to finish the test.


FVP instructions for ACS image and run
======================================

Download ACS image from:
 - ``https://gitlab.arm.com/systemready/acs/arm-systemready/-/tree/linux-5.17-rc7/IR/prebuilt_images/v22.04_1.0-Linux-v5.17-rc7``

Use the below command to run the FVP with ACS image support in the
SD card.

::

  unxz ${<path-to-img>/ir_acs_live_image.img.xz}

  tmux

  <_workspace>/meta-arm/scripts/runfvp <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf -- -C board.msd_mmc.p_mmc_file="${<path-to-img>/ir_acs_live_image.img}"

The test results can be fetched using following commands:

::

  sudo mkdir /mnt/test
  sudo mount -o rw,offset=<offset_2nd_partition> <path-to-img>/ir_acs_live_image.img /mnt/test/
  fdisk -lu <path-to-img>/ir_acs_live_image.img
  ->  Device                                                     Start     End Sectors  Size Type
      <path-to-img>/ir_acs_live_image_modified.img1    2048 1050622 1048575  512M Microsoft basic data
      <path-to-img>/ir_acs_live_image_modified.img2 1050624 1153022  102399   50M Microsoft basic data

  ->   <offset_2nd_partition> = 1050624 * 512 (sector size) = 537919488

The FVP will reset multiple times during the test, and it might take up to 1 day to finish
the test. At the end of test, the FVP host terminal will halt showing a shell prompt.
Once test is finished, the FVP can be stoped, and result can be copied following above
instructions.

Common to FVP and FPGA
======================

U-Boot should be able to boot the grub bootloader from
the 1st partition and if grub is not interrupted, tests are executed
automatically in the following sequence:

 - SCT
 - UEFI BSA
 - FWTS

The results can be fetched from the ``acs_results`` folder in the RESULT partition of the USB stick (FPGA) / SD Card (FVP).

#####################################################

Manual capsule update and ESRT checks
-------------------------------------

The following section describes running manual capsule update with the ``direct`` method.

The steps described in this section perform manual capsule update and show how to use the ESRT feature
to retrieve the installed capsule details.

For the following tests two capsules are needed to perform 2 capsule updates. A positive update and a negative update.

A positive test case capsule which boots the platform correctly until the Linux prompt, and a negative test case with an
incorrect capsule (corrupted or outdated) which fails to boot to the host software.

Check the "Run SystemReady-IR ACS tests" section above to download and unpack the ACS image file
 - ``ir_acs_live_image.img.xz``

Download edk2 under <_workspace>:

::

  git clone https://github.com/tianocore/edk2.git
  cd edk2
  git checkout f2188fe5d1553ad1896e27b2514d2f8d0308da8a

Download systemready-patch repo under <_workspace>:
::

  git clone https://git.gitlab.arm.com/arm-reference-solutions/systemready-patch.git -b CORSTONE1000-2023.06

*******************
Generating Capsules
*******************

Generating FPGA Capsules
========================

::

   cd <_workspace>/build/tmp/deploy/images/corstone1000-mps3/
   sh <_workspace>/systemready-patch/embedded-a/corstone1000/capsule_gen/capsule_gen.sh -d mps3

This will generate a file called "corstone1000_image.nopt" which will be used to
generate a UEFI capsule.

::

   cd <_workspace>
   edk2/BaseTools/BinWrappers/PosixLike/GenerateCapsule -e -o cs1k_cap_mps3_v6 --fw-version 6 \
   --lsv 0 --guid    e2bb9c06-70e9-4b14-97a3-5a7913176e3f --verbose --update-image-index  0 \
   --verbose build/tmp/deploy/images/corstone1000-mps3/corstone1000_image.nopt

   edk2/BaseTools/BinWrappers/PosixLike/GenerateCapsule -e -o cs1k_cap_mps3_v5 --fw-version 5 \
   --lsv 0 --guid    e2bb9c06-70e9-4b14-97a3-5a7913176e3f --verbose --update-image-index  0 \
   --verbose build/tmp/deploy/images/corstone1000-mps3/corstone1000_image.nopt

Generating FVP Capsules
=======================

::

   cd <_workspace>/build/tmp/deploy/images/corstone1000-fvp/
   sh <_workspace>/systemready-patch/embedded-a/corstone1000/capsule_gen/capsule_gen.sh -d fvp

This will generate a file called "corstone1000_image.nopt" which will be used to
generate a UEFI capsule.


::

   cd <_workspace>
   edk2/BaseTools/BinWrappers/PosixLike/GenerateCapsule -e -o cs1k_cap_fvp_v6 \
   --fw-version 6 --lsv 0 --guid    e2bb9c06-70e9-4b14-97a3-5a7913176e3f --verbose --update-image-index \
   0 --verbose build/tmp/deploy/images/corstone1000-fvp/corstone1000_image.nopt

   edk2/BaseTools/BinWrappers/PosixLike/GenerateCapsule -e -o cs1k_cap_fvp_v5 --fw-version 5 \
   --lsv 0 --guid    e2bb9c06-70e9-4b14-97a3-5a7913176e3f --verbose --update-image-index \
   0 --verbose build/tmp/deploy/images/corstone1000-fvp/corstone1000_image.nopt


Common Notes for FVP and FPGA
=============================

The capsule binary size (wic file) should be less than 15 MB.

Based on the user's requirement, the user can change the firmware version
number given to ``--fw-version`` option (the version number needs to be >= 1).


****************
Copying Capsules
****************

Copying the FPGA capsules
=========================

The user should prepare a USB stick as explained in ACS image section `FPGA instructions for ACS image`_.
Place the generated ``cs1k_cap`` files in the root directory of the boot partition
in the USB stick. Note: As we are running the direct method, the ``cs1k_cap`` file
should not be under the EFI/UpdateCapsule directory as this may or may not trigger
the on disk method.

::

   sudo cp cs1k_cap_mps3_v6 <mounting path>/BOOT/
   sudo cp cs1k_cap_mps3_v5 <mounting path>/BOOT/
   sync

Copying the FVP capsules
========================

First, mount the IR image:

::

   sudo mkdir /mnt/test
   sudo mount -o rw,offset=1048576 <path-to-img>/ir_acs_live_image.img  /mnt/test

Then, copy the capsules:

::

   sudo cp cs1k_cap_fvp_v6 /mnt/test/
   sudo cp cs1k_cap_fvp_v5 /mnt/test/
   sync

Then, unmount the IR image:

::

   sudo umount /mnt/test

**NOTE:**

The size of first partition in the image file is calculated in the following way. The data is
just an example and might vary with different ir_acs_live_image.img files.

::

   fdisk -lu <path-to-img>/ir_acs_live_image.img
   ->  Device                                                     Start     End Sectors  Size Type
       <path-to-img>/ir_acs_live_image_modified.img1    2048 1050622 1048575  512M Microsoft basic data
       <path-to-img>/ir_acs_live_image_modified.img2 1050624 1153022  102399   50M Microsoft basic data

   ->  <offset_1st_partition> = 2048 * 512 (sector size) = 1048576

******************************
Performing the capsule update
******************************

During this section we will be using the capsule with the higher version (cs1k_cap_<fvp/mps3>_v6) for the positive scenario
and the capsule with the lower version (cs1k_cap_<fvp/mps3>_v5) for the negative scenario.

Running the FVP with the IR prebuilt image
==========================================

Run the FVP with the IR prebuilt image:

::

   <_workspace>/meta-arm/scripts/runfvp --terminals=xterm <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf -- -C "board.msd_mmc.p_mmc_file=${<path-to-img>/ir_acs_live_image.img}"

Running the FPGA with the IR prebuilt image
===========================================

Insert the prepared USB stick then Power cycle the MPS3 board.

Executing capsule update for FVP and FPGA
=========================================

Reach u-boot then interrupt the boot  to reach the EFI shell.

::

   Press ESC in 4 seconds to skip startup.nsh or any other key to continue.

Then, type FS0: as shown below:

::

  FS0:

In case of the positive scenario run the update with the higher version capsule as shown below: 

::
  
  EFI/BOOT/app/CapsuleApp.efi cs1k_cap_<fvp/mps3>_v6

After successfully updating the capsule the system will reset.

In case of the negative scenario run the update with the lower version capsule as shown below: 

::
  
  EFI/BOOT/app/CapsuleApp.efi cs1k_cap_<fvp/mps3>_v5

The command above should fail and in the TF-M logs the following message should appear:

::

   ERROR: flash_full_capsule: version error 

Then, reboot manually:

::

   Shell> reset

FPGA: Select Corstone-1000 Linux kernel boot
============================================

Remove the USB stick before u-boot is reached so the Corstone-1000 kernel will be detected and used for booting.

**NOTE:** Otherwise, the execution ends up in the ACS live image.

FVP: Select Corstone-1000 Linux kernel boot
===========================================

Interrupt the u-boot shell.

::

   Hit any key to stop autoboot:

Run the following commands in order to run the Corstone-1000 Linux kernel and being able to check the ESRT table.

**NOTE:** Otherwise, the execution ends up in the ACS live image.

::

   $ unzip $kernel_addr 0x90000000
   $ loadm 0x90000000 $kernel_addr_r 0xf00000
   $ bootefi $kernel_addr_r $fdtcontroladdr


*********************
Capsule update status
*********************

Positive scenario
=================

In the positive case scenario, the user should see following log in TF-M log,
indicating the new capsule image is successfully applied, and the board boots
correctly.

::

  ...
  SysTick_Handler: counted = 10, expiring on = 360
  SysTick_Handler: counted = 20, expiring on = 360
  SysTick_Handler: counted = 30, expiring on = 360
  ...
  metadata_write: success: active = 1, previous = 0
  flash_full_capsule: exit
  corstone1000_fwu_flash_image: exit: ret = 0
  ...


It's possible to check the content of the ESRT table after the system fully boots.

In the Linux command-line run the following:

::

   # cd /sys/firmware/efi/esrt/entries/entry0
   # cat *
    
   0x0
   e2bb9c06-70e9-4b14-97a3-5a7913176e3f
   0
   6
   0
   6
   0

.. line-block::
   capsule_flags:	0x0
   fw_class:	e2bb9c06-70e9-4b14-97a3-5a7913176e3f
   fw_type:	0
   fw_version:	6
   last_attempt_status:	0 
   last_attempt_version:	6
   lowest_supported_fw_ver:	0


Negative scenario
=================

In the negative case scenario (rollback the capsule version), the user should 
see appropriate logs in the secure enclave terminal. 

::

  ...  
    uefi_capsule_retrieve_images: image 0 at 0xa0000070, size=15654928
    uefi_capsule_retrieve_images: exit
    flash_full_capsule: enter: image = 0x0xa0000070, size = 7764541, version = 5
    ERROR: flash_full_capsule: version error
    private_metadata_write: enter: boot_index = 1
    private_metadata_write: success
    fmp_set_image_info:133 Enter
    FMP image update: image id = 0
    FMP image update: status = 1version=6 last_attempt_version=5.
    fmp_set_image_info:157 Exit.
    corstone1000_fwu_flash_image: exit: ret = -1
    fmp_get_image_info:232 Enter
    pack_image_info:207 ImageInfo size = 105, ImageName size = 34, ImageVersionName
    size = 36
    fmp_get_image_info:236 Exit
  ...


If capsule pass initial verification, but fails verifications performed during 
boot time, secure enclave will try new images predetermined number of times 
(defined in the code), before reverting back to the previous good bank.

::

  ...
  metadata_write: success: active = 0, previous = 1
  fwu_select_previous: in regular state by choosing previous active bank
  ...

It's possible to check the content of the ESRT table after the system fully boots.

In the Linux command-line run the following:

::

   # cd /sys/firmware/efi/esrt/entries/entry0
   # cat *
    
   0x0
   e2bb9c06-70e9-4b14-97a3-5a7913176e3f
   0
   6
   1
   5
   0

.. line-block::
   capsule_flags:	0x0
   fw_class:	e2bb9c06-70e9-4b14-97a3-5a7913176e3f
   fw_type:	0
   fw_version:	6
   last_attempt_status:	1
   last_attempt_version:	5
   lowest_supported_fw_ver:	0

Linux distros tests
-------------------

*************************************************************
Debian install and boot preparation (applicable to FPGA only)
*************************************************************

There is a known issue in the `Shim 15.7 <https://salsa.debian.org/efi-team/shim/-/tree/upstream/15.7?ref_type=tags>`__
provided with the Debian installer image (see below). This bug causes a fatal
error when attempting to boot media installer for Debian, and it resets the MPS3 before installation starts.
A patch to be applied to the Corstone-1000 stack (only applicable when
installing Debian) is provided to
`Skip the Shim <https://gitlab.arm.com/arm-reference-solutions/systemready-patch/-/blob/CORSTONE1000-2023.06/embedded-a/corstone1000/shim/0001-arm-bsp-u-boot-corstone1000-Skip-the-shim-by-booting.patch>`__.
This patch makes U-Boot automatically bypass the Shim and run grub and allows
the user to proceed with a normal installation. If at the moment of reading this
document the problem is solved in the Shim, the user is encouraged to try the
corresponding new installer image. Otherwise, please apply the patch as
indicated by the instructions listed below. These instructions assume that the
user has already built the stack by following the build steps of this
documentation.

::

  cd <_workspace>
  git clone https://git.gitlab.arm.com/arm-reference-solutions/systemready-patch.git -b CORSTONE1000-2023.06
  cp -f systemready-patch/embedded-a/corstone1000/shim/0001-arm-bsp-u-boot-corstone1000-Skip-the-shim-by-booting.patch meta-arm
  cd meta-arm
  git am 0001-arm-bsp-u-boot-corstone1000-Skip-the-shim-by-booting.patch
  cd ..
  kas shell meta-arm/kas/corstone1000-mps3.yml -c="bitbake u-boot trusted-firmware-a corstone1000-image -c cleansstate; bitbake corstone1000-image"

Please update the cs1000.bin on the SD card with the newly generated wic file.

*************************************************
Debian/openSUSE install (applicable to FPGA only)
*************************************************

To test Linux distro install and boot, the user should prepare two empty USB
sticks (minimum size should be 4GB and formatted with FAT32).

Download one of following Linux distro images:
 - `Debian 12.0.0 installer image <https://cdimage.debian.org/debian-cd/current/arm64/iso-dvd/debian-12.0.0-arm64-DVD-1.iso>`__
 - `OpenSUSE Tumbleweed installer image <http://download.opensuse.org/ports/aarch64/tumbleweed/iso/>`__

**NOTE:** For OpenSUSE Tumbleweed, the user should look for a DVD Snapshot like
openSUSE-Tumbleweed-DVD-aarch64-Snapshot<date>-Media.iso

Once the iso file is downloaded, the iso file needs to be flashed to your USB
drive. This can be done with your development machine.

In the example given below, we assume the USB device is ``/dev/sdb`` (the user
should use the `lsblk` command to confirm).

**NOTE:** Please don't confuse your host PC's own hard drive with the USB drive.
Then, copy the contents of the iso file into the first USB stick by running the
following command in the development machine:

::

  sudo dd if=<path-to-iso_file> of=/dev/sdb iflag=direct oflag=direct status=progress bs=1M; sync;

Unplug the first USB stick from the development machine and connect it to the
MSP3 board. At this moment, only the first USB stick should be connected. Open
the following picocom sessions in your development machine:

::

  sudo picocom -b 115200 /dev/ttyUSB0  # in one terminal
  sudo picocom -b 115200 /dev/ttyUSB2  # in another terminal.

When the installation screen is visible in ttyUSB2, plug in the second USB stick
in the MPS3 and start the distro installation process. If the installer does not
start, please try to reboot the board with both USB sticks connected and repeat
the process.

**NOTE:** Due to the performance limitation of Corstone-1000 MPS3 FPGA, the
distro installation process can take up to 24 hours to complete.

*******************************************************
Debian install clarifications (applicable to FPGA only)
*******************************************************

As the installation process for Debian is different than the one for openSUSE,
Debian may need some extra steps, that are indicated below:

During Debian installation, please answer the following question:
 - "Force GRUB installation to the EFI removable media path?" Yes
 - "Update NVRAM variables to automatically boot into Debian?" No

If the grub installation fails, these are the steps to follow on the subsequent
popups:

1. Select "Continue", then "Continue" again on the next popup
2. Scroll down and select "Execute a shell"
3. Select "Continue"
4. Enter the following command:

::

   in-target grub-install --no-nvram --force-extra-removable

5. Enter the following command:

::

   in-target update-grub

6. Enter the following command:

::

   exit

7. Select "Continue without boot loader", then select "Continue" on the next popup
8. At this stage, the installation should proceed as normal.

*****************************************************************
Debian/openSUSE boot after installation (applicable to FPGA only)
*****************************************************************

Once the installation is complete, unplug the first USB stick and reboot the
board.
The board will then enter recovery mode, from which the user can access a shell
after entering the password for the root user. Proceed to edit the following
files accordingly:

::

  vi /etc/systemd/system.conf
  DefaultDeviceTimeoutSec=infinity

The file to be editted next is different depending on the installed distro:

::

  vi /etc/login.defs # Only applicable to Debian
  vi /usr/etc/login.defs # Only applicable to openSUSE
  LOGIN_TIMEOUT   180

To make sure the changes are applied, please run:

::

  systemctl daemon-reload

After applying the previous commands, please reboot the board. The user should
see a login prompt after booting, for example, for debian:

::

  debian login:

Login with the username root and its corresponding password (already set at
installation time).

************************************************************
OpenSUSE Raw image install and boot (applicable to FVP only)
************************************************************

Steps to download OpenSUSE Tumbleweed raw image:
  - Under `OpenSUSE Tumbleweed appliances <http://download.opensuse.org/ports/aarch64/tumbleweed/appliances/>`__
  - The user should look for a Tumbleweed-ARM-JeOS-efi.aarch64-* Snapshot, for example,
    ``openSUSE-Tumbleweed-ARM-JeOS-efi.aarch64-<date>-Snapshot<date>.raw.xz``

Once the .raw.xz file is downloaded, the raw image file needs to be extracted:

::

   unxz <file-name.raw.xz>


The above command will generate a file ending with extension .raw image. Now, use the following command
to run FVP with raw image installation process.

::

   <_workspace>/meta-arm/scripts/runfvp --terminals=xterm <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf -- -C board.msd_mmc.p_mmc_file="${openSUSE raw image file path}"

After successfully installing and booting the Linux distro, the user should see
a openSUSE login prompt.

::

   localhost login:

Login with the username 'root' and password 'linux'.

PSA API tests
-------------

***********************************************************
Run PSA API test commands (applicable to both FPGA and FVP)
***********************************************************

When running PSA API test commands (aka PSA Arch Tests) on MPS3 FPGA, the user should make sure there is no
USB stick connected to the board. Power on the board and boot the board to
Linux. Then, the user should follow the steps below to run the tests.

When running the tests on the Corstone-1000 FVP, the user should follow the
instructions in `Running the software on FVP`_ section to boot Linux in FVP
host_terminal_0, and login using the username ``root``.

First, load FF-A TEE kernel module:

::

  insmod /lib/modules/6.1.32-yocto-standard/extra/arm-ffa-tee.ko

Then, check whether the FF-A TEE driver is loaded correctly by using the following command:

::

  cat /proc/modules | grep arm_ffa_tee

The output should be:

::

   arm_ffa_tee 16384 - - Live 0xffffffc000510000 (O)

Now, run the PSA API tests in the following order:

::

  psa-iat-api-test
  psa-crypto-api-test
  psa-its-api-test
  psa-ps-api-test

**NOTE:** The psa-crypto-api-test takes between 30 minutes to 1 hour to run.

External System tests
---------------------

**************************************************************
Running the External System test command (systems-comms-tests)
**************************************************************

Test 1: Releasing the External System out of reset
==================================================

Run this command in the Linux command-line:

::

  systems-comms-tests 1

The output on the External System terminal should be:

::

    ___  ___
   |    / __|
   |=== \___
   |___ |___/
   External System Cortex-M3 Processor
   Running RTX RTOS
   v0.1.0_2022-10-19_16-41-32-8c9dca7
   MHUv2 module 'MHU0_H' started
   MHUv2 module 'MHU1_H' started
   MHUv2 module 'MHU0_SE' started
   MHUv2 module 'MHU1_SE' started

Test 2: Communication
=====================

Test 2 releases the External System out of reset if not already done. Then, it performs communication between host and External System.

After running Test 1, run this command in the Linux command-line:

::

  systems-comms-tests 2

Additional output on the External System terminal will be printed:

::

   MHUv2: Message from 'MHU0_H': 0xabcdef1
   Received 'abcdef1' From Host MHU0
   CMD: Increment and return to sender...
   MHUv2: Message from 'MHU1_H': 0xabcdef1
   Received 'abcdef1' From Host MHU1
   CMD: Increment and return to sender...

When running Test 2 the first, Test 1 will be run in the background.

The output on the External System terminal should be:

::

    ___  ___
   |    / __|
   |=== \___
   |___ |___/
   External System Cortex-M3 Processor
   Running RTX RTOS
   v0.1.0_2022-10-19_16-41-32-8c9dca7
   MHUv2 module 'MHU0_H' started
   MHUv2 module 'MHU1_H' started
   MHUv2 module 'MHU0_SE' started
   MHUv2 module 'MHU1_SE' started
   MHUv2: Message from 'MHU0_H': 0xabcdef1
   Received 'abcdef1' From Host MHU0
   CMD: Increment and return to sender...
   MHUv2: Message from 'MHU1_H': 0xabcdef1
   Received 'abcdef1' From Host MHU1
   CMD: Increment and return to sender...

The output on the Host terminal should be:

::

   Received abcdf00 from es0mhu0
   Received abcdf00 from es0mhu1


Tests results
-------------

As a reference for the end user, reports for various tests for `Corstone-1000 software (CORSTONE1000-2023.06) <https://git.yoctoproject.org/meta-arm/tag/?h=CORSTONE1000-2023.06>`__
can be found `here <https://gitlab.arm.com/arm-reference-solutions/arm-reference-solutions-test-report/-/tree/master/embedded-a/corstone1000>`__.

Running the software on FVP on Windows
--------------------------------------

If the user needs to run the Corstone-1000 software on FVP on Windows. The user
should follow the build instructions in this document to build on Linux host
PC, and copy the output binaries to the Windows PC where the FVP is located,
and launch the FVP binary.

--------------

*Copyright (c) 2022-2023, Arm Limited. All rights reserved.*

.. _Arm Ecosystem FVPs: https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps
.. _U-Boot repo: https://github.com/u-boot/u-boot.git
