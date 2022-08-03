# Armv7-A Base Platform FVP Support in meta-arm-bsp

## How to build and run

### Configuration:
In the local.conf file, MACHINE should be set as follows:
MACHINE ?= "fvp-base-arm32"

### Build:
```bash$ bitbake core-image-minimal```

### Run:
To Run the Fixed Virtual Platform simulation tool you must download "Armv8-A
Base Platform FVP" from Arm developer (This might require the user to
register) from this address:
https://developer.arm.com/tools-and-software/simulation-models/fixed-virtual-platforms
and install it on your host PC.

Fast Models Fixed Virtual Platforms (FVP) Reference Guide:
https://developer.arm.com/docs/100966/latest

Armv8‑A Foundation Platform User Guide:
https://developer.arm.com/docs/100961/latest/


Once done, do the following to build and run an image:
```bash$ bitbake core-image-minimal```
```bash$ export YOCTO_DEPLOY_IMGS_DIR="<yocto-build-dir/tmp/deploy/images/fvp-base-arm32>"```
```bash$ cd <path-to-Base_RevC_AEMv8A_pkg-dir/models/Linux64_GCC-X.X/>```
```
bash$ ./FVP_Base_RevC-2xAEMv8A -C bp.virtio_net.enabled=1 \
         -C cache_state_modelled=0 \
         -C bp.secureflashloader.fname=${YOCTO_DEPLOY_IMGS_DIR}/bl1-fvp.bin \
         -C bp.flashloader0.fname=${YOCTO_DEPLOY_IMGS_DIR}/fip-fvp.bin \
         --data cluster0.cpu0=${YOCTO_DEPLOY_IMGS_DIR}/Image@0x80080000 \
         -C bp.virtioblockdevice.image_path=${YOCTO_DEPLOY_IMGS_DIR}/core-image-minimal-fvp-base-arm32.wic \
         -C cluster0.cpu0.CONFIG64=0 \
         -C cluster0.cpu1.CONFIG64=0 \
         -C cluster0.cpu2.CONFIG64=0 \
         -C cluster0.cpu3.CONFIG64=0 \
         -C cluster1.cpu0.CONFIG64=0 \
         -C cluster1.cpu1.CONFIG64=0 \
         -C cluster1.cpu2.CONFIG64=0 \
         -C cluster1.cpu3.CONFIG64=0 \

```


If you have built a configuration without a ramdisk, you can use the following
command in U-boot to start Linux:
```fvp32# bootz 0x80080000 - 0x82000000```

## Devices supported in the kernel
- serial
- virtio disk
- network
- watchdog
- rtc

## Devices not supported or not functional
None
