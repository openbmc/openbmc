..
 # Copyright (c) 2022, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

##########
User Guide
##########

Notice
------
The corstone1000 software stack uses the `Yocto Project <https://www.yoctoproject.org/>`__ to build
a tiny Linux distribution suitable for the corstone1000 platform. The Yocto Project relies on the
`Bitbake <https://docs.yoctoproject.org/bitbake.html#bitbake-documentation>`__
tool as its build tool. Please see `Yocto Project documentation <https://docs.yoctoproject.org/>`__
for more information.


Prerequisites
-------------
These instructions assume your host PC is running Ubuntu Linux 18.04 or 20.04 LTS, with
at least 32GB of free disk space and 16GB of RAM as minimum requirement. The
following instructions expect that you are using a bash shell.

The following prerequisites must be available on the host system. To resolve these dependencies, run:

::

    sudo apt-get update
    sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib \
     build-essential chrpath socat cpio python3 python3-pip python3-pexpect \
     xz-utils debianutils iputils-ping python3-git libegl1-mesa libsdl1.2-dev \
     xterm zstd liblz4-tool picocom
    sudo apt-get upgrade libstdc++6

Provided components
-------------------
Within the Yocto Project, each component included in the corstone1000 software stack is specified as
a `bitbake recipe <https://www.yoctoproject.org/docs/1.6/bitbake-user-manual/bitbake-user-manual.html#recipes>`__.
The recipes specific to the corstone1000 BSP are located at:
``<_workspace>/meta-arm/meta-arm-bsp/``.

The Yocto machine config files for the corstone1000 FVP and FPGA are:

 - ``<_workspace>/meta-arm/meta-arm-bsp/conf/machine/include/corstone1000.inc``
 - ``<_workspace>/meta-arm/meta-arm-bsp/conf/machine/corstone1000-fvp.conf``
 - ``<_workspace>/meta-arm/meta-arm-bsp/conf/machine/corstone1000-mps3.conf``

*****************
Software for Host
*****************

Trusted Firmware-A
==================
Based on `Trusted Firmware-A <https://git.trustedfirmware.org/TF-A/trusted-firmware-a.git>`__

+----------+---------------------------------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.7.bbappend |
+----------+---------------------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.7.bb           |
+----------+---------------------------------------------------------------------------------------------------+

OP-TEE
======
Based on `OP-TEE <https://git.trustedfirmware.org/OP-TEE/optee_os.git>`__

+----------+------------------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-security/optee/optee-os_3.18.0.bbappend |
+----------+------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm/recipes-security/optee/optee-os_3.18.0.bb           |
+----------+------------------------------------------------------------------------------------+

U-Boot
=======
Based on `U-Boot <https://gitlab.com/u-boot>`__

+----------+---------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm/recipes-bsp/u-boot/u-boot_%.bbappend |
+----------+---------------------------------------------------------------------+
| Recipe   | <_workspace>/poky/meta/recipes-bsp/u-boot/u-boot_2022.07.bb         |
+----------+---------------------------------------------------------------------+

Linux
=====
The distro is based on the `poky-tiny <https://wiki.yoctoproject.org/wiki/Poky-Tiny>`__
distribution which is a Linux distribution stripped down to a minimal configuration.

The provided distribution is based on busybox and built using muslibc. The
recipe responsible for building a tiny version of linux is listed below.

+-----------+----------------------------------------------------------------------------------------------+
| bbappend  | <_workspace>/meta-arm/meta-arm-bsp/recipes-kernel/linux/linux-yocto_%.bbappend               |
+-----------+----------------------------------------------------------------------------------------------+
| Recipe    | <_workspace>/poky/meta/recipes-kernel/linux/linux-yocto_5.19.bb                              |
+-----------+----------------------------------------------------------------------------------------------+
| defconfig | <_workspace>/meta-arm/meta-arm-bsp/recipes-kernel/linux/files/corstone1000/defconfig         |
+-----------+----------------------------------------------------------------------------------------------+

**************************************************
Software for Boot Processor (a.k.a Secure Enclave)
**************************************************
Based on `Trusted Firmware-M <https://git.trustedfirmware.org/TF-M/trusted-firmware-m.git>`__

+----------+-------------------------------------------------------------------------------------------------+
| bbappend | <_workspace>/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-m/trusted-firmware-m_%.bbappend |
+----------+-------------------------------------------------------------------------------------------------+
| Recipe   | <_workspace>/meta-arm/meta-arm/recipes-bsp/trusted-firmware-m/trusted-firmware-m_1.6.0.bb       |
+----------+-------------------------------------------------------------------------------------------------+

Building the software stack
---------------------------
Create a new folder that will be your workspace and will henceforth be referred
to as ``<_workspace>`` in these instructions. To create the folder, run:

::

    mkdir <_workspace>
    cd <_workspace>

corstone1000 is a Bitbake based Yocto Project which uses kas and bitbake
commands to build the stack. To install kas tool, run:

::

    pip3 install kas

In the top directory of the workspace ``<_workspace>``, run:

::

    git clone https://git.yoctoproject.org/git/meta-arm -b CORSTONE1000-2022.04.07

To build corstone1000 image for MPS3 FPGA, run:

::

    kas build meta-arm/kas/corstone1000-mps3.yml

Alternatively, to build corstone1000 image for FVP, run:

::

    kas build meta-arm/kas/corstone1000-fvp.yml

The initial clean build will be lengthy, given that all host utilities are to
be built as well as the target images. This includes host executables (python,
cmake, etc.) and the required toolchain(s).

Once the build is successful, all output binaries will be placed in the following folders:
 - ``<_workspace>/build/tmp/deploy/images/corstone1000-fvp/`` folder for FVP build;
 - ``<_workspace>/build/tmp/deploy/images/corstone1000-mps3/`` folder for FPGA build.

Everything apart from the ROM firmware is bundled into a single binary, the
``corstone1000-image-corstone1000-{mps3,fvp}.wic.nopt`` file. The ROM firmware is the
``bl1.bin`` file.

The output binaries used by FVP are the following:
 - The ROM firmware: ``<_workspace>/build/tmp/deploy/images/corstone1000-fvp/bl1.bin``
 - The flash image: ``<_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.wic.nopt``

The output binaries used by FPGA are the following:
 - The ROM firmware: ``<_workspace>/build/tmp/deploy/images/corstone1000-mps3/bl1.bin``
 - The flash image: ``<_workspace>/build/tmp/deploy/images/corstone1000-mps3/corstone1000-image-corstone1000-mps3.wic.nopt``

Flash the firmware image on FPGA
--------------------------------

The user should download the FPGA bit file image from `this link <https://developer.arm.com/tools-and-software/development-boards/fpga-prototyping-boards/download-fpga-images>`__
and under the section ``Arm® Corstone™-1000 for MPS3``.

The directory structure of the FPGA bundle is shown below.

::

    Boardfiles
    ├── MB
    │   ├── BRD_LOG.TXT
    │   ├── HBI0309B
    │   │   ├── AN550
    │   │   │   ├── AN550_v1.bit
    │   │   │   ├── an550_v1.txt
    │   │   │   └── images.txt
    │   │   ├── board.txt
    │   │   └── mbb_v210.ebf
    │   └── HBI0309C
    │       ├── AN550
    │       │   ├── AN550_v1.bit
    │       │   ├── an550_v1.txt
    │       │   └── images.txt
    │       ├── board.txt
    │       └── mbb_v210.ebf
    ├── SOFTWARE
    │   ├── ES0.bin
    │   ├── SE.bin
    │   └── an550_st.axf
    └── config.txt

Depending upon the MPS3 board version (printed on the MPS3 board) you should update the images.txt file
(in corresponding HBI0309x folder) so that the file points to the images under SOFTWARE directory.

Here is an example

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
  TOTALIMAGES: 2      ;Number of Images (Max: 32)

  IMAGE0PORT: 1
  IMAGE0ADDRESS: 0x00_0000_0000
  IMAGE0UPDATE: RAM
  IMAGE0FILE: \SOFTWARE\bl1.bin

  IMAGE1PORT: 0
  IMAGE1ADDRESS: 0x00_00010_0000
  IMAGE1UPDATE: AUTOQSPI
  IMAGE1FILE: \SOFTWARE\cs1000.bin

OUTPUT_DIR = ``<_workspace>/build/tmp/deploy/images/corstone1000-mps3``

1. Copy ``bl1.bin`` from OUTPUT_DIR directory to SOFTWARE directory of the FPGA bundle.
2. Copy ``corstone1000-image-corstone1000-mps3.wic.nopt`` from OUTPUT_DIR directory to SOFTWARE
   directory of the FPGA bundle and rename the wic image to ``cs1000.bin``.

**NOTE:** Renaming of the images are required because MCC firmware has
limitation of 8 characters before .(dot) and 3 characters after .(dot).

Now, copy the entire folder to board's SDCard and reboot the board.

Running the software on FPGA
----------------------------

On the host machine, open 3 minicom sessions. In case of Linux machine it will
be ttyUSB0, ttyUSB1, ttyUSB2 and it might be different on Window machine.

  - ttyUSB0 for MCC, OP-TEE and Secure Partition
  - ttyUSB1 for Boot Processor (Cortex-M0+)
  - ttyUSB2 for Host Processor (Cortex-A35)

Run following commands to open minicom sessions on Linux:

::

  sudo picocom -b 115200 /dev/ttyUSB0  # in one terminal
  sudo picocom -b 115200 /dev/ttyUSB1  # in another terminal
  sudo picocom -b 115200 /dev/ttyUSB2  # in another terminal.

Once the system boot is completed, you should see console
logs on the minicom sessions. Once the HOST(Cortex-A35) is
booted completely, user can login to the shell using
**"root"** login.

Running the software on FVP
---------------------------
An FVP (Fixed Virtual Platform) of the corstone1000 platform must be available to execute the
included run script.

The Fixed Virtual Platform (FVP) version 11.17_23 can be downloaded from the
`Arm Ecosystem FVPs`_ page. On this page, navigate to "Corstone IoT FVPs"
section to download the Corstone1000 platform FVP installer.  Follow the
instructions of the installer and setup the FVP.

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

Running test applications
-------------------------

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
-----------------------------------------------------------
To prepare a clean board environment with clean secure flash for the testing,
the user should prepare an image that erases the secure flash cleanly during
boot. Run following commands to build such image.

::

  cd <_workspace>
  git clone https://git.yoctoproject.org/git/meta-arm -b CORSTONE1000-2022.02.18
  git clone https://git.gitlab.arm.com/arm-reference-solutions/systemready-patch.git
  cp -f systemready-patch/embedded-a/corstone1000/erase_flash/0001-arm-bsp-trusted-firmware-m-corstone1000-Clean-Secure.patch meta-arm
  cd meta-arm
  git apply 0001-arm-bsp-trusted-firmware-m-corstone1000-Clean-Secure.patch
  cd ..
  kas build meta-arm/kas/corstone1000-mps3.yml

Replace the bl1.bin and cs1000.bin files on the SD card with following files:
  - The ROM firmware: <_workspace>/build/tmp/deploy/images/corstone1000-mps3/bl1.bin
  - The flash image: <_workspace>/build/tmp/deploy/images/corstone1000-mps3/corstone1000-image-corstone1000-mps3.wic.nopt

Now reboot the board. This step erases the Corstone1000 SecureEnclave flash
completely, the user should expect following message from TF-M log:

::

  !!!SECURE FLASH HAS BEEN CLEANED!!!
  NOW YOU CAN FLASH THE ACTUAL CORSTONE1000 IMAGE
  PLEASE REMOVE THE LATEST ERASE SECURE FLASH PATCH AND BUILD THE IMAGE AGAIN

Then the user should follow "Building the software stack" to build a clean
software stack and flash the FPGA as normal. And continue the testing.

Run SystemReady-IR ACS tests
-----------------------------

ACS image contains two partitions. BOOT partition and RESULTS partition.
Following packages are under BOOT partition

 * SCT
 * FWTS
 * BSA uefi
 * BSA linux
 * grub
 * uefi manual capsule application

RESULTS partition is used to store the test results.
PLEASE MAKE SURE THAT THE RESULTS PARTITION IS EMPTY BEFORE YOU START THE TESTING. OTHERWISE THE TEST RESULTS
WILL NOT BE CONSISTENT

FPGA instructions for ACS image
-------------------------------

This section describes how the user can build and run Architecture Compliance
Suite (ACS) tests on Corstone1000.

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
USB driver support for Corstone1000. The ACS image with newer kernel version
and with full USB support for Corstone1000 will be available in the next
SystemReady release in this repository.

Then, the user should prepare a USB stick with ACS image. In the given example here,
we assume the USB device is ``/dev/sdb`` (the user should use ``lsblk`` command to
confirm). Be cautious here and don't confuse your host PC's own hard drive with the
USB drive. Run the following commands to prepare the ACS image in USB stick:

::

  cd <_workspace>/arm-systemready/IR/scripts/output/
  unxz ir_acs_live_image.img.xz
  sudo dd if=ir_acs_live_image.img of=/dev/sdb iflag=direct oflag=direct bs=1M status=progress; sync

Once the USB stick with ACS image is prepared, the user should make sure that
ensure that only the USB stick with the ACS image is connected to the board,
and then boot the board.

FVP instructions for ACS image and run
---------------------------------------

Download acs image from:
 - ``https://gitlab.arm.com/systemready/acs/arm-systemready/-/tree/linux-5.17-rc7/IR/prebuilt_images/v22.04_1.0-Linux-v5.17-rc7``

Use the below command to run the FVP with acs image support in the
SD card.

::

  unxz ${<path-to-img>/ir_acs_live_image.img.xz}

<_workspace>/meta-arm/scripts/runfvp --terminals=xterm <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf -- -C board.msd_mmc.p_mmc_file="${<path-to-img>/ir_acs_live_image.img}" 

The test results can be fetched using following commands:

::

  sudo mkdir /mnt/test
  sudo mount -o rw,offset=<offset_2nd_partition> <path-to-img>/ir_acs_live_image.img /mnt/test/
  fdisk -lu <path-to-img>/ir_acs_live_image.img
  ->  Device                                                     Start     End Sectors  Size Type
      /home/emeara01/Downloads/ir_acs_live_image_modified.img1    2048 1050622 1048575  512M Microsoft basic data
      /home/emeara01/Downloads/ir_acs_live_image_modified.img2 1050624 1153022  102399   50M Microsoft basic data

  ->   <offset_2nd_partition> = 1050624 * 512 (sector size) = 537919488

The FVP will reset multiple times during the test, and it might take up to 1 day to finish
the test. At the end of test, the FVP host terminal will halt showing a shell prompt.
Once test is finished, the FVP can be stoped, and result can be copied following above
instructions.

Common to FVP and FPGA
-----------------------

U-Boot should be able to boot the grub bootloader from
the 1st partition and if grub is not interrupted, tests are executed
automatically in the following sequence:

 - SCT
 - UEFI BSA
 - FWTS
 - BSA Linux

The results can be fetched from the ``acs_results`` partition of the USB stick (FPGA) / SD Card (FVP).

Manual capsule update test
--------------------------

The following steps describe running manual capsule update with the ``direct``
method.

Check the "Run SystemReady-IR ACS tests" section above to download and unpack the acs image file
 - ``ir_acs_live_image.img.xz``

Download edk2 and generate capsule file:

::

  git clone https://github.com/tianocore/edk2.git
  edk2/BaseTools/BinWrappers/PosixLike/GenerateCapsule -e -o \
    cs1k_cap --fw-version 1 --lsv 0 --guid \
    e2bb9c06-70e9-4b14-97a3-5a7913176e3f --verbose --update-image-index \
    0 --verbose <binary_file>

The <binary_file> here should be a corstone1000-image-corstone1000-fvp.wic.nopt image for FVP and
corstone1000-image-corstone1000-mps3.wic.nopt for FPGA. And this input binary file
(capsule) should be less than 15 MB.

Based on the user's requirement, the user can change the firmware version
number given to ``--fw-version`` option (the version number needs to be >= 1).

Capsule Copy instructions for FPGA
-----------------------------------

The user should prepare a USB stick as explained in ACS image section (see above).
Place the generated ``cs1k_cap`` file in the root directory of the boot partition
in the USB stick. Note: As we are running the direct method, the ``cs1k_cap`` file
should not be under the EFI/UpdateCapsule directory as this may or may not trigger
the on disk method.

Capsule Copy instructions for FVP
---------------------------------

Run below commands to copy capsule into the
image file and run FVP software.

::

  sudo mkdir /mnt/test
  sudo mount -o rw,offset=<offset_1st_partition> <path-to-img>/ir_acs_live_image.img /mnt/test/
  sudo cp cs1k_cap /mnt/test/
  sudo umount /mnt/test
  exit

<_workspace>/meta-arm/scripts/runfvp --terminals=xterm <_workspace>/build/tmp/deploy/images/corstone1000-fvp/corstone1000-image-corstone1000-fvp.fvpconf -- -C "board.msd_mmc.p_mmc_file ${<path-to-img>/ir_acs_live_image.img}" 

Size of first partition in the image file is calculated in the following way. The data is
just an example and might vary with different ir_acs_live_image.img files.

::

  fdisk -lu <path-to-img>/ir_acs_live_image.img
  ->  Device                                                     Start     End Sectors  Size Type
      /home/emeara01/Downloads/ir_acs_live_image_modified.img1    2048 1050622 1048575  512M Microsoft basic data
      /home/emeara01/Downloads/ir_acs_live_image_modified.img2 1050624 1153022  102399   50M Microsoft basic data

  ->  <offset_1st_partition> = 2048 * 512 (sector size) = 1048576

Common to FVP and FPGA
-----------------------
Reach u-boot then interrupt shell to reach EFI shell. Use below command at EFI shell.

::

  FS0:
  EFI/BOOT/app/CapsuleApp.efi cs1k_cap

For this test, the user can provide two capsules for testing: a positive test
case capsule which boots the board correctly, and a negative test case with an
incorrect capsule which fails to boot the host software.

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
  accept_full_capsule: exit: fwu state is changed to regular
  ...


In the negative case scenario, the user should see appropriate logs in
the secure enclave terminal. If capsule pass initial verification, but fails
verifications performed during boot time, secure enclave will try new images
predetermined number of times (defined in the code), before reverting back to
the previous good bank.

::

  ...
  metadata_write: success: active = 0, previous = 1
  fwu_select_previous: in regular state by choosing previous active bank
  ...

*******************************************************
Linux distro install and boot (applicable to FPGA only)
*******************************************************

To test Linux distro install and boot, the user should prepare two empty USB sticks.

Download one of following Linux distro images:
 - Debian installer image: https://cdimage.debian.org/cdimage/weekly-builds/arm64/iso-dvd/
 - OpenSUSE Tumbleweed installer image: http://download.opensuse.org/ports/aarch64/tumbleweed/iso/
   - The user should look for a DVD Snapshot like openSUSE-Tumbleweed-DVD-aarch64-Snapshot20211125-Media.iso

Once the .iso file is downloaded, the .iso file needs to be flashed to your USB drive.

In the given example here, we assume the USB device is ``/dev/sdb`` (the user
should use `lsblk` command to confirm). Be cautious here and don't confuse your
host PC's own hard drive with the USB drive. Then copy the contents of an iso
file into the first USB stick, run:

::

  sudo dd if=</path/to/iso_file> of=/dev/sdb iflag=direct oflag=direct status=progress bs=1M; sync;

Boot the MSP3 board with the first USB stick connected. Open following minicom sessions:

::

  sudo picocom -b 115200 /dev/ttyUSB0  # in one terminal
  sudo picocom -b 115200 /dev/ttyUSB2  # in another terminal.

Press <Ctrl+x>.

Now plug in the second USB stick, the distro installation process will start.

**NOTE:** Due to the performance limitation of Corstone1000 MPS3 FPGA, the
distro installation process can take up to 24 hours to complete.

Once installation is complete, unplug the first USB stick and reboot the board.
After successfully installing and booting the Linux distro, the user should see
a login prompt:

::

  debian login:

Login with the username root.

Run psa-arch-test (applicable to both FPGA and FVP)
---------------------------------------------------

When running psa-arch-test on MPS3 FPGA, the user should make sure there is no
USB stick connected to the board. Power on the board and boot the board to
Linux. Then, the user should follow the steps below to run the psa_arch_tests.

When running psa-arch-test on Corstone1000 FVP, the user should follow the
instructions in `Running the software on FVP`_ section to boot Linux in FVP
host_terminal_0, and login using the username ``root``.

As a reference for the user's test results, the psa-arch-test report for `Corstone1000 software (CORSTONE1000-2022.02.18) <https://git.yoctoproject.org/meta-arm/tag/?h=CORSTONE1000-2022.02.18>`__
can be found in `here <https://gitlab.arm.com/arm-reference-solutions/arm-reference-solutions-test-report/-/tree/master/embedded-a/corstone1000>`__.

First, create a file containing SE_PROXY_SP UUID. Run:

::

  echo 46bb39d1-b4d9-45b5-88ff-040027dab249 > sp_uuid_list.txt

Then, load FFA driver module into Linux kernel. Run:

::

  load_ffa_debugfs.sh .

Then, check whether the FFA driver loaded correctly by using the following command:

::

  cat /proc/modules | grep arm_ffa_user

The output should be:

::

  arm_ffa_user 16384 - - Live 0xffffffc0084b0000 (O)

Now, run the PSA arch tests with following commands. The user should run the
tests in following order:

::

  psa-iat-api-test
  psa-crypto-api-test
  psa-its-api-test
  psa-ps-api-test

********************************************************
Linux distro: OpenSUSE Raw image installation (FVP Only)
********************************************************

Steps to download openSUSE Tumbleweed raw image:
  - Go to: http://download.opensuse.org/ports/aarch64/tumbleweed/appliances/
  - The user should look for a Tumbleweed-ARM-JeOS-efi.aarch64-* Snapshot, for example, ``openSUSE-Tumbleweed-ARM-JeOS-efi.aarch64-2022.03.18-Snapshot20220331.raw.xz``

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

**************************************
Running the software on FVP on Windows
**************************************
If the user needs to run the Corstone1000 software on FVP on Windows. The user
should follow the build instructions in this document to build on Linux host
PC, and copy the output binaries to the Windows PC where the FVP is located,
and launch the FVP binary.

--------------

*Copyright (c) 2021, Arm Limited. All rights reserved.*

.. _Arm Ecosystem FVPs: https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps
