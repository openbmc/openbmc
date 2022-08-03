# Armv8-A Base Platform FVP Support in meta-arm-bsp

## Howto Build and Run

### Configuration:
In the local.conf file, MACHINE should be set as follow:
MACHINE ?= "fvp-base"

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
```bash$ export YOCTO_DEPLOY_IMGS_DIR="<yocto-build-dir/tmp/deploy/images/fvp-base>"```
```bash$ cd <path-to-Base_RevC_AEMv8A_pkg-dir/models/Linux64_GCC-X.X/>```
```
bash$ ./FVP_Base_RevC-2xAEMv8A -C bp.virtio_net.enabled=1 \
         -C cache_state_modelled=0 \
         -C bp.secureflashloader.fname=${YOCTO_DEPLOY_IMGS_DIR}/bl1-fvp.bin \
         -C bp.flashloader0.fname=${YOCTO_DEPLOY_IMGS_DIR}/fip-fvp.bin \
         --data cluster0.cpu0=${YOCTO_DEPLOY_IMGS_DIR}/Image@0x80080000 \
         --data cluster0.cpu0=${YOCTO_DEPLOY_IMGS_DIR}/fvp-base-gicv3-psci-custom.dtb@0x83000000 \
         -C bp.virtioblockdevice.image_path=${YOCTO_DEPLOY_IMGS_DIR}/core-image-minimal-fvp-base.wic
```


If you have built a configuration without a ramdisk, you can use the following
command in U-boot to start Linux:
```VExpress64# booti 0x80080000 - 0x83000000```

## Devices supported in the kernel
- serial
- virtio disk
- network
- watchdog
- rtc

## Devices not supported or not functional
None
