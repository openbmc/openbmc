# N1SDP Development Platform Support in meta-arm-bsp

## Overview
The N1SDP provides access to the Arm Neoverse N1 SoC. The N1SDP enables software development for key enterprise technology
and general Arm software development. The N1SDP consists of the N1 board containing the N1 SoC.
The N1 SoC contains two dual-core Arm Neoverse N1 processor clusters.

The system demonstrates Arm technology in the context of Cache-Coherent Interconnect for Accelerators (CCIX) protocol by:

- Running coherent traffic between the N1 SoC and an accelerator card.
- Coherent communication between two N1 SoCs.
- Enabling development of CCIX-enabled FPGA accelerators.

Further information on N1SDP can be found at
https://community.arm.com/developer/tools-software/oss-platforms/w/docs/458/neoverse-n1-sdp

## Configuration:
In the local.conf file, MACHINE should be set as follow:
MACHINE ?= "n1sdp"

## Building
```bash$ bitbake core-image-minimal```

## Running

# Update Firmware on SD card:

(*) To use n1sdp board in single chip mode, flash:
    n1sdp-board-firmware_primary.tar.gz firmware.

(*) To use n1sdp board in multi chip mode, flash:
    n1sdp-board-firmware_primary.tar.gz firmware to primary board,
    n1sdp-board-firmware_secondary.tar.gz firmware to secondary board.

The SD card content is generated during the build here:
  tmp/deploy/images/n1sdp/n1sdp-board-firmware_primary.tar.gz
  tmp/deploy/images/n1sdp/n1sdp-board-firmware_secondary.tar.gz


Its content must be written on the N1SDP firmware SD card.
To do this:
- insert the sdcard of the N1SDP in an SD card reader and mount it:
```bash$ sudo mount /dev/sdx1 /mnt```
(replace sdx by the device of the SD card)

- erase its content and put the new one:
```bash$ sudo rm -rf /mnt/*```
```bash$ sudo tar --no-same-owner -xzf tmp/deploy/images/n1sdp/n1sdp-board-firmware_primary.tar.gz -C /mnt/```
```bash$ sudo umount /mnt```

- reinsert the SD card in the N1SDP board

Firmware tarball contains iofpga configuration files, scp and uefi binaries.

**NOTE**:
If the N1SDP board was manufactured after November 2019 (Serial Number greater
than 36253xxx), a different PMIC firmware image must be used to prevent
potential damage to the board. More details can be found in [1].
The `MB/HBI0316A/io_v123f.txt` file located in the microSD needs to be updated.
To update it, set the PMIC image (300k_8c2.bin) to be used in the newer models
by running the following commands on your host PC:

    $ sudo umount /dev/sdx1
    $ sudo mount /dev/sdx1 /mnt
    $ sudo sed -i '/^MBPMIC: pms_0V85.bin/s/^/;/g' /mnt/MB/HBI0316A/io_v123f.txt
    $ sudo sed -i '/^;MBPMIC: 300k_8c2.bin/s/^;//g' /mnt/MB/HBI0316A/io_v123f.txt
    $ sudo umount /mnt

# Prepare an USB hard drive:

Grub boot partition is placed on first partition of the *.wic image,
Linux root file system is placed on the second partition of the *.wic image:
  tmp/deploy/images/n1sdp/core-image-minimal-n1sdp.wic

This *.wic image should be copied to USB stick with simple dd call.


[1]: https://community.arm.com/developer/tools-software/oss-platforms/w/docs/604/notice-potential-damage-to-n1sdp-boards-if-using-latest-firmware-release
