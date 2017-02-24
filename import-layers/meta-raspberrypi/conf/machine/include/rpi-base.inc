include conf/machine/include/rpi-default-settings.inc
include conf/machine/include/rpi-default-versions.inc
include conf/machine/include/rpi-default-providers.inc

SOC_FAMILY = "rpi"
include conf/machine/include/soc-family.inc

IMAGE_FSTYPES ?= "tar.bz2 ext3 rpi-sdimg"

XSERVER = " \
    xserver-xorg \
    ${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "xserver-xorg-extension-glx", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "xf86-video-modesetting", "xf86-video-fbdev", d)} \
    "

KERNEL_DEVICETREE ?= " \
    bcm2708-rpi-b.dtb \
    bcm2708-rpi-b-plus.dtb \
    bcm2709-rpi-2-b.dtb \
    bcm2710-rpi-3-b.dtb \
    bcm2708-rpi-cm.dtb \
    bcm2710-rpi-cm3.dtb \
    \
    overlays/hifiberry-amp.dtbo \
    overlays/hifiberry-dac.dtbo \
    overlays/hifiberry-dacplus.dtbo \
    overlays/hifiberry-digi.dtbo \
    overlays/i2c-rtc.dtbo \
    overlays/iqaudio-dac.dtbo \
    overlays/iqaudio-dacplus.dtbo \
    overlays/lirc-rpi.dtbo \
    overlays/pitft22.dtbo \
    overlays/pitft28-resistive.dtbo \
    overlays/pps-gpio.dtbo \
    overlays/rpi-ft5406.dtbo \
    overlays/w1-gpio.dtbo \
    overlays/w1-gpio-pullup.dtbo \
    overlays/pi3-disable-bt.dtbo \
    overlays/pi3-miniuart-bt.dtbo \
    overlays/vc4-kms-v3d.dtbo \
    "
KERNEL_IMAGETYPE ?= "Image"

MACHINE_FEATURES += "apm usbhost keyboard vfat ext2 screen touchscreen alsa bluetooth wifi sdio"

# Raspberry Pi has no hardware clock
MACHINE_FEATURES_BACKFILL_CONSIDERED = "rtc"

MACHINE_EXTRA_RRECOMMENDS += " kernel-modules"

# Set Raspberrypi splash image
SPLASH = "psplash-raspberrypi"

IMAGE_BOOT_FILES ?= "bcm2835-bootfiles/* ${KERNEL_IMAGETYPE};${SDIMG_KERNELIMAGE}"

# The kernel image is installed into the FAT32 boot partition and does not need
# to also be installed into the rootfs.
RDEPENDS_kernel-base = ""
