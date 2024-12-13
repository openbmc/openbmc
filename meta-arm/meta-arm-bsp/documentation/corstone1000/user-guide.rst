..
 # Copyright (c) 2022-2024, Arm Limited.
 #
 # SPDX-License-Identifier: MIT

####################
Build, Flash and Run
####################

Notice
------
The Corstone-1000 software stack uses the `Yocto Project <https://www.yoctoproject.org/>`__ to build
a tiny Linux distribution suitable for the Corstone-1000 platform (kernel and initramfs filesystem less than 5 MB on the flash).
The Yocto Project relies on the `BitBake <https://docs.yoctoproject.org/bitbake.html#bitbake-documentation>`__
tool as its build tool. Please see `Yocto Project documentation <https://docs.yoctoproject.org/>`__
for more information.

Prerequisites
-------------

This guide assumes that your host machine is running Ubuntu 20.04 LTS ( with ``sudo`` rights), with at least
32GB of free disk space and 16GB of RAM as minimum requirement.

The following prerequisites must be available on the host system:

- Git 1.8.3.1 or greater.
- Python 3.8.0 or greater.
- GNU Tar 1.28 or greater.
- GNU Compiler Collection 8.0 or greater.
- GNU Make 4.0 or greater.
- tmux.

Please follow the steps described in the Yocto mega manual:

- `Compatible Linux Distribution <https://docs.yoctoproject.org/singleindex.html#compatible-linux-distribution>`__
- `Build Host Packages <https://docs.yoctoproject.org/singleindex.html#build-host-packages>`__

Targets
-------
The Corstone-1000 software stack can be run on:

- `Arm Corstone-1000 Ecosystem FVP (Fixed Virtual Platform) <https://developer.arm.com/downloads/-/arm-ecosystem-fvps>`__
- `Arm Corstone-1000 for MPS3 <https://developer.arm.com/documentation/dai0550/latest/>`__

    .. important::

        Arm Corstone-1000 for MPS3 requires an additional 32 MB QSPI flash PMOD module. For more information see the `Application Note AN550 document <https://developer.arm.com/documentation/dai0550/latest/>`__.


Yocto Stable Branch
-------------------

Corstone-1000 software stack is built on top of Yocto styhead release.

Software Components
-------------------
Within the Yocto Project, each component included in the Corstone-1000 software stack is specified as
a `BitBake recipe <https://docs.yoctoproject.org/bitbake/2.2/bitbake-user-manual/bitbake-user-manual-intro.html#recipes>`__.
The recipes specific to the Corstone-1000 BSP are located at:
``$WORKSPACE/meta-arm/meta-arm-bsp/``.

.. important::

    ``$WORKSPACE`` refers to the absolute path to your workspace where the `meta-arm` repository will be cloned.

    ``$TARGET`` is either ``mps3`` or ``fvp``.

The Yocto machine config files for the Corstone-1000 FVP and MPS3 targets are:

 - ``$WORKSPACE/meta-arm/meta-arm-bsp/conf/machine/include/corstone1000.inc``
 - ``$WORKSPACE/meta-arm/meta-arm-bsp/conf/machine/corstone1000-$TARGET.conf``

.. note::

    All the paths stated in this document are absolute paths.

*************************
Host Processor Components
*************************

`Trusted Firmware-A <https://git.trustedfirmware.org/TF-A/trusted-firmware-a.git>`__
====================================================================================

+----------+-----------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-a/trusted-firmware-a_%.bbappend``   |
+----------+-----------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.11.0.bb``        |
+----------+-----------------------------------------------------------------------------------------------------+

`Trusted Services <https://trusted-services.readthedocs.io/en/latest/index.html>`__
====================================================================================

+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/libts_%.bbappend``                   |
+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-crypto-api-test_%.bbappend``  |
+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-iat-api-test_%.bbappend``     |
+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-its-api-test_%.bbappend``     |
+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-psa-ps-api-test_%.bbappend``      |
+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-sp-se-proxy_%.bbappend``          |
+----------+-----------------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/trusted-services/ts-sp-smm-gateway_%.bbappend``       |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/libts_git.bb``                           |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-crypto-api-test_git.bb``          |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-iat-api-test_git.bb``             |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-its-api-test_git.bb``             |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/ts-psa-ps-api-test_git.bb``              |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/ts-sp-smm-gateway.bb``                   |
+----------+-----------------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/trusted-services/ts-sp-se-proxy.bb``                      |
+----------+-----------------------------------------------------------------------------------------------------------+

`OP-TEE <https://git.trustedfirmware.org/OP-TEE/optee_os.git>`__
================================================================

+----------+----------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-security/optee/optee-os_4.%.bbappend``      |
+----------+----------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-security/optee/optee-os_4.2.0.bb``              |
+----------+----------------------------------------------------------------------------------------+

`U-Boot <https://github.com/u-boot/u-boot.git>`__
=================================================

+----------+--------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm/recipes-bsp/u-boot/u-boot_%.bbappend``          |
+----------+--------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-bsp/u-boot/u-boot_%.bbappend``      |
+----------+--------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-bsp/u-boot/u-boot_2023.07.02.bb``   |
+----------+--------------------------------------------------------------------------------+

Linux
=====
The distribution is based on the `Poky <https://docs.yoctoproject.org/ref-manual/terms.html#term-Poky>`__
distribution which is a Linux distribution stripped down to a minimal configuration.

The provided distribution is based on `BusyBox <https://www.busybox.net/>`__ and built using `musl libc <https://musl.libc.org/>`__.

+-----------+----------------------------------------------------------------------------------------------+
| bbappend  | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-kernel/linux/linux-yocto_%.bbappend``             |
+-----------+----------------------------------------------------------------------------------------------+
| Recipe    | ``$WORKSPACE/poky/meta/recipes-kernel/linux/linux-yocto_6.10.bb``                            |
+-----------+----------------------------------------------------------------------------------------------+
| defconfig | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-kernel/linux/files/corstone1000/defconfig``       |
+-----------+----------------------------------------------------------------------------------------------+

*************************
Secure Enclave Components
*************************

`Trusted Firmware-M <https://git.trustedfirmware.org/TF-M/trusted-firmware-m.git>`__
====================================================================================

+----------+-----------------------------------------------------------------------------------------------------+
| bbappend | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-bsp/trusted-firmware-m/trusted-firmware-m_%.bbappend``   |
+----------+-----------------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm/recipes-bsp/trusted-firmware-m/trusted-firmware-m_2.1.0.bb``         |
+----------+-----------------------------------------------------------------------------------------------------+

************************************
External System Processor Components
************************************

RTX Real-Time operating system
==============================

An example application that uses the `RTX Real-Time Operating System <https://developer.arm.com/Tools%20and%20Software/Keil%20MDK/RTX5%20RTOS>`__.

The application project can be found `here <https://git.gitlab.arm.com/arm-reference-solutions/corstone1000/external_system/rtx>`__.

+----------+--------------------------------------------------------------------------------------------+
| Recipe   | ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-bsp/external-system/external-system_0.1.0.bb``  |
+----------+--------------------------------------------------------------------------------------------+

.. _building-the-software-stack:

Build
-----

.. warning::

  Building binaries natively on Windows and AArch64 Linux is not supported.
  
  Use an AMD64 Linux based development machine to build the software stack and transfer the binaries to run the software stack on an FVP in Windows or AArch64 Linux
  if required.


#. Create a new folder that will be your workspace.

    .. code-block:: console

        mkdir $WORKSPACE
        cd $WORKSPACE

#. Install kas version 4.4 with ``sudo`` rights.

    .. code-block:: console

        sudo pip3 install kas==4.4

    Ensure the kas installation directory is visible on the ``$PATH`` environment variable.

#. Clone the `meta-arm` Yocto layer in the workspace ``$WORKSPACE``.

    .. code-block:: console

        cd $WORKSPACE
        git clone https://git.yoctoproject.org/git/meta-arm -b CORSTONE1000-2024.11

#. Build a Corstone-1000 image:

    .. code-block:: console

        kas build meta-arm/kas/corstone1000-$TARGET.yml:meta-arm/ci/debug.yml

    .. important::

        Accept the EULA at https://developer.arm.com/downloads/-/arm-ecosystem-fvps/eula
        to build a Corstone-1000 image for FVP as follows:

        .. code-block:: console

            export ARM_FVP_EULA_ACCEPT="True"


    .. warning::

        Access to the External System Processor is disabled by default.
        To build the Corstone-1000 image with External System Processor enabled, run:

        .. code-block:: console

            kas build meta-arm/kas/corstone1000-$TARGET.yml:meta-arm/ci/debug.yml:meta-arm/kas/corstone1000-extsys.yml

A clean build takes a significant amount of time given that all of the development machine utilities are also
built along with the target images. Those development machine utilities include executables (Python,
CMake, etc.) and the required toolchains.


Once the build succeeds, all output binaries will be placed in ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/``

Everything apart from the Secure Enclave ROM firmware and External System firmware, is bundled into a single binary, the
``corstone1000-flash-firmware-image-corstone1000-$TARGET.wic`` file.

The output binaries run in the Corstone-1000 platform are the following:
 - The Secure Enclave ROM firmware: ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/bl1.bin``
 - The External System Processor firmware: ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/es_flashfw.bin``
 - The internal firmware flash image: ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/corstone1000-flash-firmware-image-corstone1000-$TARGET.wic``

.. _flashing-firmware-images:

Flash
-----

.. note::

    The steps below only apply to the MPS3. The FVP being a software application running on your development
    machine does not require any firmware flashing. Refer to `this <running-software-stack-fvp_>`__
    section for running the software stack on FVP. 

#. Download the FPGA bit file image ``AN550: Arm® Corstone™-1000 for MPS3 Version 2.0``
   on the `Arm Developer website <https://developer.arm.com/tools-and-software/development-boards/fpga-prototyping-boards/download-fpga-images>`__.
   Click on the ``Download AN550 bundle`` button and login to download the file.

    The directory structure of the FPGA bundle is as shown below:

    .. code-block:: console

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

#. Depending upon the MPS3 board version, you should update the ``images.txt`` file
   (found in the corresponding ``HBI0309x`` folder e.g. ``Boardfiles/MB/HBI0309$BOARD_VERSION/AN550/images.txt``)
   so it points to the images under the ``SOFTWARE`` directory.
   Where ``$BOARD_VERSION`` is a variable containing the board printed on the MPS3 board.

   The ``images.txt`` file compatible with the latest version of the software
   stack can be seen below;

    .. code-block:: console

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


#. Copy ``bl1.bin`` from ``$WORKSPACE/build/tmp/deploy/images/corstone1000-mps3`` to the ``SOFTWARE`` directory of the FPGA bundle.
#. Copy ``es_flashfw.bin`` from ``$WORKSPACE/build/tmp/deploy/images/corstone1000-mps3`` to the ``SOFTWARE`` directory of the FPGA bundle
   and rename the binary to ``es0.bin``.
#. Copy ``corstone1000-flash-firmware-image-corstone1000-mps3.wic`` from ``$WORKSPACE/build/tmp/deploy/images/corstone1000-mps3`` to the ``SOFTWARE``
   directory of the FPGA bundle and rename the wic image to ``cs1000.bin``.

.. note::
    Renaming of the images is required because the MCC firmware has
    a limit of 8 characters for file name and 3 characters for file extension.

After making all modifications above, copy the FPGA bit file bundle to the board's SDCard and reboot the MPS3.

Run
---

.. _running-software-stack-mps3:

Once the target is turned ON, the Secure Enclave will start to boot, wherein the relevant memory contents of the ``*.wic``
file are copied to their respective memory locations. Firewall policies are enforced
on memories and peripherals before bringing the Host Processor out of reset.

The Host Processor will boot TrustedFirmware-A, OP-TEE, U-Boot and then Linux before presenting a login prompt.

****
MPS3
****

1. Open 4 serial port comms terminals on the host machine.
   Those might be ``ttyUSB0``, ``ttyUSB1``, ``ttyUSB2``, and ``ttyUSB3`` on Linux machines.

  - ``ttyUSB0`` for MCC, OP-TEE and Secure Partition
  - ``ttyUSB1`` for Secure Enclave (Cortex-M0+)
  - ``ttyUSB2`` for Host Processor (Cortex-A35)
  - ``ttyUSB3`` for External System Processor (Cortex-M3)

    The serial ports might be different on Windows machines.

    Run the following commands in separate terminal instances on Linux:

    .. code-block:: console

        sudo picocom -b 115200 /dev/ttyUSB0

    .. code-block:: console

        sudo picocom -b 115200 /dev/ttyUSB1

    .. code-block:: console

        sudo picocom -b 115200 /dev/ttyUSB2
  
    .. code-block:: console

        sudo picocom -b 115200 /dev/ttyUSB3

    .. important::
        Plug a connected Ethernet cable to the MPS3 or it will
        wait for a network connection for a considerable amount of time, printing the following
        on the Host Processor terminal (``ttyUSB2``):

        .. code-block:: console

            Generic PHY 40100000.ethernet-ffffffff:01: attached PHY driver (mii_bus:phy_addr=40100000.ethernet-ffffffff:01, irq=POLL)
            smsc911x 40100000.ethernet eth0: SMSC911x/921x identified at 0xffffffc008e50000, IRQ: 17
            Waiting up to 100 more seconds for network.

2. Once the system boot is completed, you should see console logs on the serial port terminals.
   Once the Host Processor is booted completely, user can login to the shell using ``root`` login.

    .. important::

        The secure flash might be completely filled if the system does not boot and only the Secure Enclave logs (``ttyUSB1``) are visible.

        Clean the secure flash if that is the case following the steps `here <clean-secure-flash_>`__.

.. _running-software-stack-fvp:

***
FVP
***

A Fixed Virtual Platform (FVP) model of the Corstone-1000 platform must be available to run the
Corstone-1000 FVP software image.

A Yocto recipe is provided to download the latest supported FVP version.

The recipe is located at ``$WORKSPACE/meta-arm/meta-arm/recipes-devtools/fvp/fvp-corstone1000.bb``.

The latest FVP version is ``11.23.25`` and is automatically downloaded and installed when using the
``runfvp`` command as detailed below.

.. note::

    .. code-block:: console

        kas shell meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml \
        -c "../meta-arm/scripts/runfvp -- --version"

The FVP can also be manually downloaded from the `Arm Ecosystem FVPs`_ page by navigating
to "Corstone IoT FVPs" section to download the Corstone-1000 platform FVP installer. Follow the
instructions of the installer to setup the FVP.

#. Run the FVP

    .. code-block:: console

        kas shell meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml \
        -c "../meta-arm/scripts/runfvp --terminals=tmux"

    When the script is executed, three terminal instances will be launched:

    - one for the Secure Enclave processing element
    - two for the Host processor processing element.


    .. code-block:: console

        corstone1000-fvp login:

#. Login using the ``root`` username.


Security Issue Reporting
------------------------

To report any security issues identified with Corstone-1000, please send an email to psirt@arm.com.

#####
Tests
#####

.. important::

    All the tests below assume you have already built the software stack at least once
    following the instructions `here <building-the-software-stack_>`__.


.. _clean-secure-flash:

Clean Secure Flash
------------------

.. important::

    The MPS3 secure flash needs to be cleared before running tests.
    This is to erase the flash cleanly and prepare a clean board environment for testing.


#. Clone the `systemready-patch` repository to your $WORKSPACE.

    .. code-block:: console

        cd $WORKSPACE
        git clone https://git.gitlab.arm.com/arm-reference-solutions/systemready-patch.git -b CORSTONE1000-2024.11

#. Copy the secure flash cleaning Git patch file to your copy of `meta-arm`.

    .. code-block:: console

        cp -f systemready-patch/embedded-a/corstone1000/erase_flash/0001-embedded-a-corstone1000-clean-secure-flash.patch meta-arm

#. Apply the Git patch to `meta-arm`.

    .. code-block:: console

        cd meta-arm
        git apply 0001-embedded-a-corstone1000-clean-secure-flash.patch

#. Rebuild the software stack.

    .. code-block:: console

        cd $WORKSPACE
        kas shell meta-arm/kas/corstone1000-mps3.yml:meta-arm/ci/debug.yml
        bitbake -c cleansstate trusted-firmware-m corstone1000-flash-firmware-image
        bitbake -c build corstone1000-flash-firmware-image

#. Replace the ``bl1.bin`` file on the SD card with ``$WORKSPACE/build/tmp/deploy/images/corstone1000-mps3/bl1.bin``.

#. Reboot the board to completely erase the secure flash.

    The following message log from TrustedFirmware-M should be displayed on the Secure Enclave terminal (``ttyUSB1``):

    .. code-block:: console

        !!!SECURE FLASH HAS BEEN CLEANED!!!
        NOW YOU CAN FLASH THE ACTUAL CORSTONE1000 IMAGE
        PLEASE REMOVE THE LATEST ERASE SECURE FLASH PATCH AND BUILD THE IMAGE AGAIN

#. Whilst still in the ``kas`` shell, revert the changes the patch introduced by running the following commands:

    .. code-block:: console

        cd $WORKSPACE/meta-arm
        git reset --hard
        cd ..
        bitbake -c cleansstate trusted-firmware-m corstone1000-flash-firmware-image
        exit

#. Follow the `instructions <building-the-software-stack_>`__ to build a clean software stack and flash the MPS3 with it.

You can proceed with the test instructions in the following section after having done all the above.

SystemReady-IR
--------------

.. important::
    Running the SystemReady-IR tests described below requires USB drives.
    In our testing, not all USB drive models worked well with the MPS3.

    Here are the USB drive models that were stable in our test environment:

        - HP v165w 8 GB USB Flash Drive
        - SanDisk Ultra 32GB Dual USB Flash Drive USB M3.0
        - SanDisk Ultra 16GB Dual USB Flash Drive USB M3.0

Follow the instructions below before running the Architecture Compliance Suite (ACS) tests.


.. _build-efi-system-partition:

*****************************
Build an EFI System Partition
*****************************

A storage with EFI System Partition (ESP) must exist in the system for the UEFI-SCT related tests to pass.

#. Build an ESP partition for your target

    .. code-block:: console

        kas build meta-arm/kas/corstone1000-$TARGET.yml:meta-arm/ci/debug.yml --target corstone1000-esp-image

#. Locate the ``corstone1000-esp-image-corstone1000-$TARGET.wic`` build artefact
   in ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/`` 

****************************
Use the EFI System Partition
****************************

.. _use-efi-system-partition-mps3:

MPS3
====

#. Connect a USB drive to your development machine.

#. Run the following command on your development machine to discover which device is your USB drive:

    .. code-block:: console

        lsblk

    The remaining steps assume the USB drive is ``/dev/sdb``.

    .. warning::

        Do not mistake your development machine hard drive with the USB drive.

#. Copy the ESP to the USB drive by running the following command:

    .. code-block:: console

        sudo dd \
        if=$WORKSPACE/build/tmp/deploy/images/corstone1000-mps3/corstone1000-esp-image-corstone1000-mps3.wic \
        of=/dev/sdb \
        iflag=direct oflag=direct status=progress bs=512; sync;

#. Plug the USB drive to the MPS3.


.. _use-efi-system-partition-fvp:

FVP
===

The ESP disk image will automatically be used by the Corstone-1000 FVP as the 2nd MMC card image.
It will be used when the SystemReady-IR tests is performed on the FVP in the later section.


****************************
Run SystemReady-IR ACS Tests
****************************

ACS is used to ensure architectural compliance across different implementations of the architecture.
Arm Enterprise ACS includes a set of examples of the invariant behaviors that are provided by a
set of specifications for enterprise systems (i.e. SBSA, SBBR, etc.).
Implementers can verify if these behaviors have been interpreted correctly.

The following test suites and bootable applications are under the ``BOOT`` partition of the ACS image:

 * SCT
 * FWTS
 * BSA UEFI
 * BSA linux
 * GRUB
 * UEFI manual capsule application

See the directory structure of the ACS image ``BOOT`` partition below:

.. code-block:: console

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
    ├── ramdisk-busybox.img
    └── acs_results

The ``BOOT`` partition is also used to store test results in the ``acs_results`` folder.

.. important::

    Ensure that the ``acs_results`` folder is empty before starting the test.


This sections below describe how to build and run ACS tests on Corstone-1000.

.. _mps3-instructions-for-acs-image:


#. On your host development machine, clone the `Arm SystemReady ACS repository <https://github.com/ARM-software/arm-systemready/>`_.

    .. code-block:: console

        cd $WORKSPACE
        git clone https://github.com/ARM-software/arm-systemready.git

    This repository contains the infrastructure to build the ACS and the bootable prebuilt images to be used for the
    certifications of SystemReady-IR.

#. Find the pre-built ACS live image in ``$WORKSPACE/arm-systemready/IR/prebuilt_images/v23.09_2.1.0/ir-acs-live-image-generic-arm64.wic.xz``.

    .. note::

        This prebuilt ACS image includes v5.13 kernel, which does not provide
        USB driver support for Corstone-1000. The ACS image with a newer kernel version
        and full USB support for Corstone-1000 will be available in the repository with the next
        SystemReady release.

#. Decompress the pre-built ACS live image.

    .. code-block:: console

        cd $WORKSPACE/arm-systemready/IR/prebuilt_images/v23.09_2.1.0
        unxz ir-acs-live-image-generic-arm64.wic.xz

MPS3
====

#. Connect a USB drive (other than the one used for the ESP) to the host development machine.

#. Run the following command to discover which device is your USB drive:

    .. code-block:: console

        lsblk

    The remaining steps assume the USB drive is ``/dev/sdc``.

    .. warning::

        Do not mistake your development machine hard drive with the USB drive.

#. Copy the ACS image to the USB drive by running the following commands:

    .. code-block:: console

        cd $WORKSPACE/arm-systemready/IR/prebuilt_images/v23.09_2.1.0
        sudo dd if=ir-acs-live-image-generic-arm64.wic of=/dev/sdc iflag=direct oflag=direct bs=1M status=progress; sync

#. Plug the USB drive to the MPS3. At this point you should have both the USB drive with the ESP and the USB drive with the ACS image plugged to the MPS3.

#. Reboot the MPS3.

The MPS3 will reset multiple times during the test, and it might take approximately 24 to 36 hours to finish the test.

.. important::

    Unplug the ESP USB drive from the MPS3 if it is preventing GRUB
    from finding the bootable partition. Leave only the ACS image USB drive
    plugged in to run the ACS tests.

    The ESP USB drive can be plugged in again after
    selecting the `Linux Boot` option in the GRUB menu at the end of the ACS tests.

.. warning::

    A timeout issue has been observed while booting Linux during the ACS tests, causing the system to boot into emergency mode.
    Booting Linux is necessary to run certain tests, such as `dt-validation`.
    The following workaround is required to enable Linux to boot properly and perform all Linux-based tests:

    #. Press Enter at the Linux prompt.
    #. Open the file `/etc/systemd/system.conf` and set `DefaultDeviceTimeoutSec=infinity`.
    #. Reboot the platform using the `reboot` command.
    #. Select the `Linux Boot` option from the GRUB menu.
    #. Allow Linux to boot and run the remaining ACS tests until completion.

.. _fvp-instructions-for-acs-image:

FVP
===


Run the commands below to run the ACS test on FVP using the built firmware image and the pre-built ACS image identified above:

.. code-block:: console

    cd $WORKSPACE
    tmux
    ./meta-arm/scripts/runfvp \
    --terminals=tmux \
    ./build/tmp/deploy/images/corstone1000-fvp/corstone1000-flash-firmware-image-corstone1000-fvp.fvpconf \
    -- -C board.msd_mmc.p_mmc_file=$WORKSPACE/arm-systemready/IR/prebuilt_images/v23.09_2.1.0/ir-acs-live-image-generic-arm64.wic


.. note::
    The FVP will reset multiple times during the test.
    The ACS tests might take up to 1 day to complete when run on FVP.

The message `ACS run is completed` will be displayed on the FVP host terminal when the test runs to completion.
You will be prompted to press the Enter key to access the Linux prompt.


Test Sequence and Results
=========================

U-Boot should be able to boot the GRUB bootloader from the first partition.

If GRUB is not interrupted, the tests are executed automatically in the following order:

 - SCT
 - UEFI BSA
 - FWTS

The results can be fetched from the `acs_results` folder in the ``BOOT`` partition of the USB drive (for MPS3) or SD Card (for FVP).

.. note::

    Access the `acs_results` folder in FVP by running the following commands:

    .. code-block:: console

        sudo mkdir /mnt/test
        sudo mount -o rw,offset=1048576 \
        $WORKSPACE/arm-systemready/IR/prebuilt_images/v23.09_2.1.0/ir-acs-live-image-generic-arm64.wic \
        /mnt/test

#####################################################

Capsule Update
--------------

The following section describes the steps to update the firmware using Capsule Update
as the Corstone-1000 supports UEFI.

The firmware update process is tested with an invalid capsule (negative capsule update test)
and with a valid capsule (positive capsule update test) to validate the robustness and
error-handling capabilities of the firmware update mechanism.

During the positive capsule update test, the Corstone-1000 is given a valid capsule, which it successfully applies, boots up and then reaches the Linux command prompt.

During the negative capsule update test, the Corstone-1000 is given an outdated capsule with a lower version number,
which is expected to be rejected due to its outdated status, thereby retaining the previous firmware.

Two different capsules (one for each test) are therefore needed to perform the tests.


*****************
Generate Capsules
*****************

U-Boot's ``mkeficapsule`` tool is used to generate capsules. It is built automatically for the host machine during the firmware image building process.
The tool can be found in the ``$WORKSPACE/build/tmp/sysroots-components/x86_64/u-boot-tools-native/usr/bin/mkeficapsule`` directory.

``mkeficapsule`` uses a no-partition image which is created when performing a clean firmware build.
The no-partition image can be found in the ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/corstone1000-$TARGET_image.nopt`` directory.

The capsule's default metadata passed can be found in the ``$WORKSPACE/meta-arm/meta-arm-bsp/recipes-bsp/images/corstone1000-flash-firmware-image.bb``
and ``$WORKSPACE/meta-arm/kas/corstone1000-image-configuration.yml`` files.

Valid Capsule
=============

An automatically generated capsule can be found in ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/corstone1000-$TARGET-v6.uefi.capsule`` after running a firmware build.

The default metadata values are assumed to be correct to generate a valid capsule.

This capsule will be used for the positive capsule update test.

Invalid Capsule
===============

Generate another capsule with ``fw-version`` metadata set to a lower version than the valid capsule.
The example below assumes the valid capsule has a default firmware version of 6, and therefore creates an invalid capsule with firmware version 5.


Run the following commands to generate an invalid capsule with a ``fw-version`` of ``5``:

.. code-block:: console

   cd $WORKSPACE

   ./build/tmp/sysroots-components/x86_64/u-boot-tools-native/usr/bin/mkeficapsule \
   --monotonic-count 1 \
   --private-key build/tmp/deploy/images/corstone1000-$TARGET/corstone1000_capsule_key.key \
   --certificate build/tmp/deploy/images/corstone1000-$TARGET/corstone1000_capsule_cert.crt \
   --index 1 \
   --guid $TARGET_GUID \
   --fw-version 5 build/tmp/deploy/images/corstone1000-$TARGET/corstone1000-$TARGET_image.nopt \
   corstone1000-$TARGET-v5.uefi.capsule


.. important::

    ``$TARGET_GUID`` is different depending on whether the capsule is built for the ``fvp`` or ``mps3`` ``$TARGET``.

    - ``fvp`` ``$TARGET_GUID`` is ``989f3a4e-46e0-4cd0-9877-a25c70c01329``
    - ``mps3`` ``$TARGET_GUID`` is ``df1865d1-90fb-4d59-9c38-c9f2c1bba8cc``

The invalid capsule will be located in the ``$WORKSPACE`` directory.

***************************
Transfer Capsules to Target
***************************

The capsule delivery process described below is the direct method (usage of capsules from the ACS image)
as opposed to the on-disk method (delivery of capsules using a file on a mass storage device).

MPS3
====

#. Prepare a USB drive as explained in `this <mps3-instructions-for-acs-image_>`_ section.

#. Copy the capsule file to the root directory of the ``BOOT`` partition in the USB drive.

  .. code-block:: console

    sudo cp $CAPSULES_PATH/corstone1000-mps3-v6.uefi.capsule $ACS_IMAGE_USB_DRIVE_PATH/BOOT/
    sudo cp $CAPSULES_PATH/corstone1000-mps3-v5.uefi.capsule $ACS_IMAGE_USB_DRIVE_PATH/BOOT/
    sync

.. important::

    Since we are using the direct Capsule Update method, the capsule files should not be placed in
    the ``EFI/UpdateCapsule`` directory, as this might inadvertently trigger the on-disk update method.

FVP
===

#. Download and extract the ACS image `as described for the MPS3 <mps3-instructions-for-acs-image_>`_.
   The ACS image extraction location will be referred below as ``$ACS_IMAGE_PATH``.

    .. note::

      Creating a USB drive with the ACS image is not required as the image will be mounted with the steps below.

#. Find the first partition's offset of the ``ir-acs-live-image-generic-arm64.wic`` image using the ``fdisk`` tool.
   The partition table can be listed using:

    .. code-block:: console

        fdisk -lu $ACS_IMAGE_PATH/ir-acs-live-image-generic-arm64.wic
        Device                                                 Start     End Sectors  Size Type
        $ACS_IMAGE_PATH/ir-acs-live-image-generic-arm64.wic1    2048  309247  307200  150M Microsoft basic data
        $ACS_IMAGE_PATH/ir-acs-live-image-generic-arm64.wic2  309248 1343339 1034092  505M Linux filesystem


    Given that the first partition starts at sector 2048 and each sector is 512 bytes in size,
    the first partition is at offset 1048576 (2048 x 512).

#. Mount the ``ir-acs-live-image-generic-arm64.wic`` image using the previously calculated offset:

    .. code-block:: console

        sudo mkdir /mnt/ir-acs-live-image-generic-arm64
        sudo mount -o rw,offset=<first_partition_offset> $ACS_IMAGE_PATH/ir-acs-live-image-generic-arm64.wic  /mnt/ir-acs-live-image-generic-arm64

#. Copy the capsules:

    .. code-block:: console

        sudo cp $CAPSULES_PATH/corstone1000-fvp-v6.uefi.capsule /mnt/ir-acs-live-image-generic-arm64/
        sudo cp $CAPSULES_PATH/corstone1000-fvp-v5.uefi.capsule /mnt/ir-acs-live-image-generic-arm64/
        sync

#. Unmount the IR image:

    .. code-block:: console

        sudo umount /mnt/ir-acs-live-image-generic-arm64

************************
Run Capsule Update Tests
************************

The valid capsule (``corstone1000-$TARGET-v6.uefi.capsule``) will be used first to run the positive capsule update test.
This will be followed by using the invalid capsule (``corstone1000-$TARGET-v5.uefi.capsule``) to run the negative capsule update test.

.. important::

    This sequence order must be respected as the invalid capsule has a firmware version lower than the firmware version in the valid capsule.
    The negative capsule update test effectively tests that firmware rollback is not permitted.


.. _positive-capsule-update-test:

Positive Capsule Update Test
============================

#. Run Corstone-1000 with the ACS image containing the two capsule files:

    - MPS3:

      #. Plug the prepared USB drive which has the IR prebuilt image and two capsules to the MPS3.
      #. Power cycle the MPS3.

    - FVP:

      #. Run the FVP with the IR prebuilt image which now also contains the two capsules:

      .. code-block:: console

        kas shell meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml \
        -c "../meta-arm/scripts/runfvp --terminals=tmux \
        -- -C board.msd_mmc.p_mmc_file=$ACS_IMAGE_PATH/ir-acs-live-image-generic-arm64.wic"

      .. warning::

          ``$ACS_IMAGE_PATH`` must be an absolute path. Ensure there are no spaces before or after of ``=`` of the ``-C board.msd_mmc.p_mmc_file`` option.


#. Wait until U-Boot loads EFI from the ACS image and interrupt the EFI shell by pressing the ``Escape`` key when the following prompt is displayed on the Host Processor terminal (``ttyUSB2``).

    .. code-block:: console

        Press ESC in 4 seconds to skip startup.nsh or any other key to continue.

#. Access the content of the first file system (``File System 0``) where we copied the capsule files by running the following command:

    .. code-block:: console

        FS0:

#. Run the ``CapsuleApp`` application with the valid capsule file:

    - MPS3:

        .. code-block:: console

            EFI/BOOT/app/CapsuleApp.efi EFI/BOOT/corstone1000-mps3-v6.uefi.capsule

    - FVP:

        .. code-block:: console

            EFI/BOOT/app/CapsuleApp.efi corstone1000-fvp-v6.uefi.capsule

    The capsule update will be started.

    .. note::
        The capsule update takes about 8 minutes to complete on MPS3 and between 15-30 minutes on FVP.

        The Corstone-1000 will reset after successfully applying the capsule.

    
    The software stack copies the capsule content to the external flash, which is shared between the Secure Enclave and the Host Processor
    before rebooting the system.

    After the first reboot, TrustedFirmware-M should apply the valid capsule and display the following log on the Secure Enclave terminal (``ttyUSB1``)
    before rebooting the system a second time:

    .. code-block:: console

      ...
      SysTick_Handler: counted = 10, expiring on = 360
      SysTick_Handler: counted = 20, expiring on = 360
      SysTick_Handler: counted = 30, expiring on = 360
      ...
      metadata_write: success: active = 1, previous = 0
      flash_full_capsule: exit
      corstone1000_fwu_flash_image: exit: ret = 0
      ...

    The above log snippet indicates that the new capsule image is successfully applied, and the board is booting with the external flash's Bank-1.

    After a second reboot, the following log should be displayed on on the Secure Enclave terminal (``ttyUSB1``):

    .. code-block:: console

      ...
      fmp_set_image_info:133 Enter
      FMP image update: image id = 0
      FMP image update: status = 0version=6 last_attempt_version=6.
      fmp_set_image_info:157 Exit.
      corstone1000_fwu_host_ack: exit: ret = 0
      ...

#. Interrupt the U-Boot shell.

    .. code-block:: console

        Hit any key to stop autoboot:

#. Run the following commands in order to run the Corstone-1000 Linux kernel.

    .. note::
        Otherwise, the execution ends up in the ACS live image.

    .. code-block:: console

        $ unzip $kernel_addr 0x90000000
        $ loadm 0x90000000 $kernel_addr_r $filesize
        $ bootefi $kernel_addr_r $fdtcontroladdr


#. After the system fully boots, read the EFI System Resource Table (ESRT) to verify that the firmware version matches the version of the capsule applied.

  .. code-block:: console

    # cd /sys/firmware/efi/esrt/entries/entry0
    # cat *

    0x0                                      # capsule_flags
    989f3a4e-46e0-4cd0-9877-a25c70c01329     # fw_class
    0                                        # fw_type
    6                                        # fw_version
    0                                        # last_attempt_status
    6                                        # last_attempt_version
    0                                        # lowest_supported_fw_ver

  See the `UEFI documentation <https://uefi.org/specs/UEFI/2.10/23_Firmware_Update_and_Reporting.html#id29>`__ for more information on the significance of the table fields.

.. warning::

    Do not terminate FVP between the positive and negative capsule update tests.

Negative Capsule Update Test
============================

.. important::

  The `positive capsule update test <positive-capsule-update-test_>`__ must be run before running the negative capsule update test.

#. After running the positive capsule update test, reboot the system by typing the following command on the Host Processor terminal (``ttyUSB2``):

    .. code-block:: console

        reboot

#. Wait until U-Boot loads EFI from the ACS image and interrupt the EFI shell by pressing the ``Escape`` key when the following prompt is displayed on the Host Processor terminal (``ttyUSB2``).

    .. code-block:: console

        Press ESC in 4 seconds to skip startup.nsh or any other key to continue.

#. Access the content of the first file system (``File System 0``) where we copied the capsule files by running the following command:

    .. code-block:: console

        FS0:

#. Run the ``CapsuleApp`` application with the invalid capsule file:

    - MPS3:

        .. code-block:: console

            EFI/BOOT/app/CapsuleApp.efi EFI/BOOT/corstone1000-mps3-v5.uefi.capsule

    - FVP:

        .. code-block:: console

            EFI/BOOT/app/CapsuleApp.efi corstone1000-fvp-v5.uefi.capsule


#. TrustedFirmware-M should reject the capsule due to having a lower firmware version and display the following log on the Secure Enclave terminal (``ttyUSB1``):

    .. code-block:: console

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

    The Secure Enclave tries to load the new image a predetermined number of times
    if the capsule passes initial verification but fails verifications performed during
    boot time.

      .. code-block:: console

        ...
        metadata_write: success: active = 0, previous = 1
        fwu_select_previous: in regular state by choosing previous active bank
        ...

    The Secure Enclave eventually reverts back to the previously running image.

#. Reboot manually:

    .. code-block:: console

        Shell> reset

#. Interrupt the U-Boot shell.

    .. code-block:: console

        Hit any key to stop autoboot:

#. Run the following commands in order to run the Corstone-1000 Linux kernel.

    .. note::
        Otherwise, the execution ends up in the ACS live image.

    .. code-block:: console

        $ unzip $kernel_addr 0x90000000
        $ loadm 0x90000000 $kernel_addr_r $filesize
        $ bootefi $kernel_addr_r $fdtcontroladdr

#. After the system fully boots, read the ESRT to verify the firmware version does not match what is on the invalid capsule.

    .. code-block:: console

      # cd /sys/firmware/efi/esrt/entries/entry0
      # cat *

      0x0                                      # capsule_flags
      989f3a4e-46e0-4cd0-9877-a25c70c01329     # fw_class
      0                                        # fw_type
      6                                        # fw_version
      1                                        # last_attempt_status
      5                                        # last_attempt_version
      0                                        # lowest_supported_fw_ver



Linux Distributions
-------------------

This sections describes the steps to install major Linux distributions to the Corstone-1000 Host Processor.

The Linux distributions to be installed are:

 - `Debian <https://www.debian.org/>`__
 - `openSUSE <https://www.opensuse.org/>`__

Follow the instructions below to install the Linux distributions to the Corstone-1000 software stack.

**************************
Prepare Installation Media
**************************

The media containing the bootable files required to start the installation process needs to be prepared.

Follow the instructions below to create the installation media.

#. Using your development machine, download one of following Linux distribution images:

    - `Debian installer image <https://cdimage.debian.org/mirror/cdimage/archive/12.7.0/arm64/iso-dvd/>`__
    - `OpenSUSE Tumbleweed installer image <http://download.opensuse.org/ports/aarch64/tumbleweed/iso/>`__ 

    .. note::
        
        For openSUSE Tumbleweed, search for an ISO file with the format: ``openSUSE-Tumbleweed-DVD-aarch64-Snapshot$DATE-Media.iso``.
        
        ``openSUSE-Tumbleweed-DVD-aarch64-Snapshot20240516-Media.iso`` was used during development.

    The location of the ISO file on the development machine will be referred to as ``$DISTRO_INSTALLER_ISO_PATH``.

#. Create the installation media which will contain the necessary files to install the operation system.

    - MPS3:

        #. Plug a blank USB drive formatted with FAT32, ensuring it has a minimum capacity of 4GB, to the development machine.

        #. Run the following command to discover which device is your USB drive:

            .. code-block:: console

                lsblk

            The remaining steps assume the USB drive is ``/dev/sdb``.

            .. warning::

                Do not mistake your development machine hard drive with the USB drive.

        #. Write one of the distribution installer ISO file to the USB drive.

            .. code-block:: console

                sudo dd if=$DISTRO_INSTALLER_ISO_PATH of=/dev/sdb iflag=direct oflag=direct status=progress bs=1M; sync;

    - FVP:

        The distribution installer ISO file does not need to be burnt to a USB drive.
        It will be used as is when starting the FVP install the distribution.

********************
Prepare System Drive
********************

A system (or boot) drive, to store all the operating system files and used to boot the distribution, is required as
Corstone-1000 on-board non-volatile storage size is insufficient for installing the distributions.

    - MPS3:
        #. Find another blank USB drive formatted with FAT32 with a minimum capacity of 4GB.
        #. Do not yet connect this blank USB drive to the MPS3. It will be used as the primary drive to boot the distribution.

    - FVP:
        #. Create an 10 GB GUID Partition Table (GPT) formatted MultiMediaCard (MMC) image.

            .. code-block:: console

                dd if=/dev/zero of=$WORKSPACE/fvp_distro_system_drive.img \
                bs=1 count=0 seek=10G; sync; \
                parted -s fvp_distro_system_drive.img mklabel gpt
    
        #. This MMC image will be used as the primary drive to boot the distribution.


************
Installation
************

MPS3
====

#. Connect the installation media, which contains the installer for the desired distribution, to the MPS3.
#. Open a serial port terminal interface to ``/dev/ttyUSB0`` in one terminal window on your development machine.

    .. code-block:: console

        sudo picocom -b 115200 /dev/ttyUSB0

#. Open a serial port terminal interface to ``/dev/ttyUSB2`` in another terminal window on your development machine.

    .. code-block:: console

        sudo picocom -b 115200 /dev/ttyUSB2

#. When the installation screen is displayed on ``ttyUSB2``, plug in the (still empty) system drive to the MPS3.
#. Start the distribution installation process.

    .. note::

        Reboot the MPS3 with both USB drives (installation media and empty system drive) connected to it if the distribution installer does not start.

.. note::

    Due to the performance limitation, the distribution installation process can take up to 24 hours to complete.

FVP
===
#. Start the FVP with the system drive as the primary drive and the distro ISO file as the secondary drive.

    .. code-block:: console

        kas shell meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml \
        -c "../meta-arm/scripts/runfvp --terminals=tmux -- \
        -C board.msd_mmc.p_mmc_file=$WORKSPACE/fvp_distro_system_drive.img \
        -C board.msd_mmc_2.p_mmc_file=$DISTRO_INSTALLER_ISO_PATH"

    The Linux distribution will be installed on ``fvp_distro_system_drive.img``.


Debian Installation Extra Steps
===============================

Debian installation may need some extra steps, that are indicated below:

#. Answer ``Yes`` to the question ``Force grub installation to the EFI removable media path?``.

    If the GRUB installation fails, these are the steps to follow on the subsequent
    popups:

    #. Select ``Continue``, then ``Continue`` again on the next popup.

    #. Scroll down and select ``Execute a shell``.

    #. Select ``Continue``.

    #. Enter the following command:

        .. code-block:: console

            in-target grub-install --no-nvram --force-extra-removable

    #. Enter the following command:

        .. code-block:: console

            in-target update-grub
    
    #. Enter the following command:

        .. code-block:: console

            exit

    #. Select ``Continue without boot loader``, then select ``Continue`` on the next popup.

    #. At this stage, the installation should proceed as normal.

#. Answer ``No`` to the question ``Update NVRAM variables to automatically boot into Debian?``.


*****************
Boot Distribution
*****************

- MPS3

    #. Once the installation is complete, unplug the installation media.
    #. Perform a cold boot of the MPS3.

- FVP

    The target should automatically boot into the installed operating system image.

    Stop the FVP and run the command below to simulate a cold boot:

    .. code-block:: console

        kas shell meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml \
        -c "../meta-arm/scripts/runfvp --terminals=tmux -- \
        -C board.msd_mmc.p_mmc_file=$WORKSPACE/fvp_distro_system_drive.img"

    .. warning::

        To manually enter recovery mode, once the FVP begins booting, you can quickly
        change the boot option in GRUB, to boot into recovery mode. This option will disappear
        quickly, so it is best to preempt it.

        Select ``Advanced Options for <OS>`` and then ``<OS> (recovery mode)``.


The target will then enter recovery mode, from which the user can access a shell
after entering the password for the ``root`` user.


Timeout Optimizations
=====================

.. important::

    Operating system timeouts are inconsistent across systems.
    Skip this section if the system boots to Debian or OpenSUSE without any issue.

Make the system modification below whilst in recovery mode to increase timeouts and boot to the installed distribution.

#. Remove the timeout limit for device operations.

    - Debian
        .. code-block:: console

            vi /etc/systemd/system.conf
            DefaultDeviceTimeoutSec=infinity

    - openSUSE
        .. code-block:: console

            vi /usr/lib/systemd/system.conf
            DefaultDeviceTimeoutSec=infinity

        .. warning::

            As modifying ``system.conf`` in ``/usr/lib/systemd/`` is not working as it is getting overwritten,
            copy ``system.conf`` from ``/usr/lib/systemd/`` to ``/etc/systemd/system.conf.d/`` after the above edit.

#. Set the maximum time that the system will wait for a user to successfully log in before timing out to 180 seconds.

    - Debian
        .. code-block:: console

            vi /etc/login.defs
            LOGIN_TIMEOUT   180

    - openSUSE
        .. code-block:: console

            vi /usr/etc/login.defs
            LOGIN_TIMEOUT   180

#. Ensure the changes are applied by run the command below.

    .. code-block:: console

        systemctl daemon-reload

#. Perform a cold boot of the target.

Log into the Distribution
=========================

Login with the ``root`` username and its corresponding password (set during installation)
at the distribution login prompt after booting. See an illustration for Debian below:

.. code-block:: console

    debian login:


UEFI Secure Boot
----------------

The UEFI Secure Boot test is designed to verify the integrity and authenticity of the system’s boot process.
This test ensures that only trusted, signed images are executed, thereby preventing unauthorized or malicious code from running.
A successful test confirms that the signed image executes correctly, while any unsigned image is blocked from running.


**********************************************
Generate Keys, Signed Image and Unsigned Image
**********************************************

#. Build an EFI System Partition as described `here <build-efi-system-partition_>`__.

#. Clone the `systemready-patch` repository to your workspace.

    .. code-block:: console

        cd $WORKSPACE

        git clone https://git.gitlab.arm.com/arm-reference-solutions/systemready-patch.git \
        -b CORSTONE1000-2024.11

#. Set the current working directory to build directory's subdirectory containing the software stack build images.

    .. code-block:: console

        cd $WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/

#. Run the image signing script (without changing the current working directory).

    .. code-block:: console

        ./$WORKSPACE/systemready-patch/embedded-a/corstone1000/secureboot/create_keys_and_sign.sh \
        -d $TARGET \
        -v $CERTIFICATE_VALIDITY_DURATION_IN_DAYS

    .. important::

        The `efitools <https://github.com/vathpela/efitools/>`__  package is required to execute the script.

    .. note::

        Consult the image signing script help message (``-h``) for more information about other optional arguments.

        The script is interactive and contains commands that require ``sudo`` level permissions.


The keys, signed kernel image, and unsigned kernel image will be copied to the exisiting ESP image.
The modified ESP image can be found at ``$WORKSPACE/build/tmp/deploy/images/corstone1000-$TARGET/corstone1000-esp-image-corstone1000-$TARGET.wic``.


****************************
Run Unsigned Image Boot Test
****************************

.. _unsigned-image-boot-test-fvp:

FVP
===

#. Follow the instructions `here <use-efi-system-partition-fvp_>`__ to use the ESP.

#. Run the software stack as described `here <running-software-stack-fvp_>`__.

#. On the Host Processor terminal host side, stop the execution of U-Boot when prompted to do so with the message ``Press any key to stop``.

    .. warning::

        There is a timeout of 3 seconds to stop the execution at the U-Boot prompt.

    The U-Boot console prompt looks as follows:
   
    .. code-block:: console
   
        corstone1000#


    .. important::
    
        The rest of the instructions below will be executed on the U-Boot terminal.

#. On the U-Boot console, set the current MMC device.

    .. code-block:: console

        corstone1000# mmc dev 1

#. Enroll the four UEFI secure boot authenticated variables.

    .. code-block:: console

        corstone1000# \
        load mmc 1:1 $loadaddr corstone1000_secureboot_keys/PK.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize PK; \
        load mmc 1:1 $loadaddr corstone1000_secureboot_keys/KEK.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize KEK; \
        load mmc 1:1 $loadaddr corstone1000_secureboot_keys/db.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize db; \
        load mmc 1:1 $loadaddr corstone1000_secureboot_keys/dbx.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize dbx

#. Attempt to Load the unsigned kernel image.

    .. code-block:: console

        corstone1000# \
        load mmc 1:1 $loadaddr corstone1000_secureboot_fvp_images/Image_fvp; \
        loadm $loadaddr $kernel_addr_r $filesize; \
        bootefi $kernel_addr_r $fdtcontroladdr

        Booting /MemoryMapped(0x0,0x88200000,0x236aa00)
        Image not authenticated
        Loading image failed

The unsigned Linux kernel image should not be loaded.

.. _unsigned-image-boot-test-mps3:

MPS3
====

#. Follow the instructions `here <use-efi-system-partition-mps3_>`__ to use the ESP.

#. Perform a cold boot of the MPS3.

#. On the Host Processor terminal host side, stop the execution of U-Boot when prompted to do so with the message ``Press any key to stop``.

    .. warning::

        There is a timeout of 3 seconds to stop the execution at the U-Boot prompt.

    The U-Boot console prompt looks as follows:
   
    .. code-block:: console
   
        corstone1000#

    .. important::
    
        The rest of the instructions below will be executed on the U-Boot terminal.

#. On the U-Boot console, reset USB.

    .. code-block:: console

        corstone1000# usb reset
        resetting USB...
        Bus usb@40200000: isp1763 bus width: 16, oc: not available
        USB ISP 1763 HW rev. 32 started
        scanning bus usb@40200000 for devices... port 1 high speed
        3 USB Device(s) found
                scanning usb for storage devices... 1 Storage Device(s) found

    .. note::

        Occasionally, the USB reset may fail to detect the USB device. It is advisable to rerun the USB reset command.

#. Select the first USB device, which should be the USB drive containing the ESP.

    .. code-block:: console

        corstone1000# usb dev 0

#. Enroll the four UEFI secure boot authenticated variables.

    .. code-block:: console

        corstone1000# \
        load usb 0 $loadaddr corstone1000_secureboot_keys/PK.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize PK; \
        load usb 0 $loadaddr corstone1000_secureboot_keys/KEK.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize KEK; \
        load usb 0 $loadaddr corstone1000_secureboot_keys/db.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize db; \
        load usb 0 $loadaddr corstone1000_secureboot_keys/dbx.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize dbx

#. Attempt to Load the unsigned kernel image.

    .. code-block:: console

        corstone1000# \
        load usb 0 $loadaddr corstone1000_secureboot_mps3_images/Image_mps3
        loadm $loadaddr $kernel_addr_r $filesize
        bootefi $kernel_addr_r $fdtcontroladdr

        Booting /MemoryMapped(0x0,0x88200000,0x236aa00)
        Image not authenticated
        Loading image failed

The unsigned Linux kernel image should not be loaded.

**************************
Run Signed Image Boot Test
**************************

FVP
===

.. important::

    You must first perform the `Unsigned Image Boot Test <unsigned-image-boot-test-fvp_>`__.

Load the signed kernel image.

.. code-block:: console

    corstone1000# \
    load mmc 1:1 $loadaddr corstone1000_secureboot_fvp_images/Image_fvp.signed; \
    loadm $loadaddr $kernel_addr_r $filesize; \
    bootefi $kernel_addr_r $fdtcontroladdr

The signed Linux kernel image should be booted successfully.

MPS3
====

.. important::

    You must first perform the `Unsigned Image Boot Test <unsigned-image-boot-test-mps3_>`__.

Load the signed kernel image.

.. code-block:: console

    corstone1000# \
    load usb 0 $loadaddr corstone1000_secureboot_mps3_images/Image_mps3.signed; \
    loadm $loadaddr $kernel_addr_r $filesize; \
    bootefi $kernel_addr_r $fdtcontroladdr

The signed Linux kernel image should be booted successfully.


*******************
Disable Secure Boot
*******************

Running the UEFI Secure Boot Test steps stores UEFI authenticated variables in the secure flash.
As a result, U-Boot reads these variables and verifies the Linux kernel image before executing it at each reboot.

In a typical boot scenario, the Linux kernel image is not signed, which will prevent the system from booting due to failed image authentication.
To resolve this, the Platform Key (one of the UEFI authenticated variables for secure boot) needs to be deleted.

#. Perform a cold boot of the MPS3.

#. On the Host Processor terminal host side, stop the execution of U-Boot when prompted to do so with the message ``Press any key to stop``.

#. On the U-Boot console, delete the Platform Key (PK).

    - FVP

        .. code-block:: console

            corstone1000# \
            mmc dev 1; \
            load mmc 1:1 $loadaddr corstone1000_secureboot_keys/PK_delete.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize PK; \
            boot

    - MPS3

        .. code-block:: console

            corstone1000# \
            usb reset; \
            usb dev 0; \
            load usb 0 $loadaddr corstone1000_secureboot_keys/PK_delete.auth && setenv -e -nv -bs -rt -at -i $loadaddr:$filesize PK; \
            boot


PSA API
-------

The following tests the implementation of the Application Programming Interface (API)
of the Platform Security Architecture (PSA) certification scheme. It uses Arm Firmware Framework for Arm A-profile (FF-A)
to communicate between the normal world and the secure world to run the `Arm Platform Security Architecture Test Suite <https://github.com/ARM-software/psa-arch-tests>`__.

The tests use the `arm_tstee` driver to access Trusted Services Secure Partitions from user space. The driver is included in the Linux Kernel, starting from v6.10.

.. important::
    Ensure there are no USB drives connected to the board when running the test on the MPS3.


The steps below are applicable to both MPS3 and FVP).

#. Start the Corstone-1000 and wait until it boots to Linux on the Host Processor terminal (``ttyUSB2``).

#. Run the PSA API tests by running the commands below in the order shown:

    .. code-block:: console

        psa-iat-api-test
        psa-crypto-api-test
        psa-its-api-test
        psa-ps-api-test


External System Processor
-------------------------

.. important::

    Access to the External System Processor is disabled by default.
    Ensure you are running a software stack image with access to the External System Processor enabled following the steps `here <building-the-software-stack_>`__.

The Linux operating system running on the Host Processor starts the ``remoteproc`` framework to manage the External System Processor.


#. Stop the External System Processor with the following command:

    .. code-block:: console

        echo stop > /sys/class/remoteproc/remoteproc0/state

#. Start the External System Processor with the following command:

    .. code-block:: console

        echo start > /sys/class/remoteproc/remoteproc0/state


Symmetric Multiprocessing
-------------------------

.. warning::

    Symmetric multiprocessing (SMP) mode is only supported on FVP but is disabled by default.


#. Build the software stack with SMP mode enabled:

    .. code-block:: console

        kas build meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml:meta-arm/kas/corstone1000-fvp-multicore.yml

#. Run the Corstone-1000 FVP:

    .. code-block:: console

        kas shell meta-arm/kas/corstone1000-fvp.yml:meta-arm/ci/debug.yml:meta-arm/kas/corstone1000-fvp-multicore.yml \
        -c "../meta-arm/scripts/runfvp"


#. Verify that the FVP is running the Host Processor with more than one CPU core:

    .. code-block:: console

        nproc
        4                  # number of processing units

Secure Debug
------------

.. warning::

    Secure Debug is only supported on MPS3.

The MPS3 supports Authenticated Debug Access Control (ADAC), using the CoreSight SDC-600 IP.

For more information about this, see the following resources:

 - `CoreSight SDC-600 <https://developer.arm.com/Processors/CoreSight%20SDC-600>`__
 - `Authenticated Debug Access Control Specification <https://developer.arm.com/documentation/den0101/latest/>`__
 - `Arm Corstone-1000 for MPS3 Application Note AN550, Chapter 7 <https://developer.arm.com/documentation/dai0550/latest/>`__

The Secure Debug Manager API is implemented in the `secure-debug-manager <https://github.com/ARM-software/secure-debug-manager>`__ repository.
This repository also contains the necessary files for the Arm Development Studio support.
The build and integration instructions can be found in its `README <secure-debug-manager-repo-readme_>`__.

The `secure-debug-manager` repository also contains the private key and chain certificate to be used during the tests.
The private key's public pair is provisioned into the One-Time Programmable memory in TrustedFirmware-M. These are dummy keys that should not be used in production.

To test the Secure Debug feature, you'll need a debug probe from the DSTREAM family and Arm Development Studio versions 2022.2, 2022.c, or 2023.a.


#. Clone the `secure-debug-manager` repository to your workspace.

    .. code-block:: console

        cd $WORKSPACE
        git clone https://github.com/ARM-software/secure-debug-manager.git

#. Navigate into the repository directory and checkout the specific commit in the listing below.

    .. code-block:: console

        cd $WORKSPACE/secure-debug-manager
        git checkout b30d6496ca749123e86b39b161b9f70ef76106d6

#. Follow the steps in the `secure-debug-manager`'s `README <secure-debug-manager-repo-readme_>`__ for the development machine setup.

#. Rebuild the software stack with Secure Debug.

    .. code-block:: console

        kas build meta-arm/kas/corstone1000-mps3.yml:meta-arm/ci/debug.yml:meta-arm/ci/secure-debug.yml

#. Flash the firmware image as shown `here <flashing-firmware-images_>`__.

#. Run the software as shown `here <running-software-stack-mps3_>`__.

#. Wait until the Secure Enclave terminal (``ttyUSB1``) prints the following prompts:

    .. code-block:: console

        IComPortInit                  :  382 : warn  : init       : IComPortInit: Blocked reading of LPH2RA is active.
        IComPortInit                  :  383 : warn  : init       : IComPortInit: Blocked reading LPH2RA


#. Connect the debug probe to the MPS3 using the 20-pin 1.27mm connector with the ``CS_20W_1.27MM silkscreen`` label.

#. Create a debug configuration in Arm Development Studio as described in the `secure-debug-manager`'s `README <https://github.com/ARM-software/secure-debug-manager?tab=readme-ov-file#arm-development-studio-integration>`__.

#. Connect the debuger to the target using the debug configuration.

#. Provide the paths to the private key and trust chain certificate when asked by Arm Development Studio Console.

    .. code-block:: console

        ...

        Please provide private key file path:
        Enter file path > $WORKSPACE\secure-debug-manager\example\data\keys\EcdsaP256Key-3.pem

        Please provide trust chain file path:
        Enter file path > $WORKSPACE\secure-debug-manager\example\data\chains\chain.EcdsaP256-3

        ...

#. When successful authenticated, Arm Development Studio will connect to the running MS3 and the debug features can be used.
   The following prompt should appear in the Secure Enclave terminal (``ttyUSB1``):

    .. code-block:: console

        ...
        boot_platform_init: Corstone-1000 Secure Debug is a success.
        ...


Reports
-------
Various test reports for the `Corstone-1000 software (CORSTONE1000-2024.11) <https://git.yoctoproject.org/meta-arm/tag/?h=CORSTONE1000-2024.11>`__
release version are available for reference `here <https://gitlab.arm.com/arm-reference-solutions/arm-reference-solutions-test-report/-/tree/CORSTONE1000-2024.11/embedded-a/corstone1000/CORSTONE1000-2024.11?ref_type=tags>`__.


--------------

*Copyright (c) 2022-2024, Arm Limited. All rights reserved.*

.. _Arm Ecosystem FVPs: https://developer.arm.com/tools-and-software/open-source-software/arm-platforms-software/arm-ecosystem-fvps
.. _secure-debug-manager-repo-readme: https://github.com/ARM-software/secure-debug-manager/blob/master/README.md
