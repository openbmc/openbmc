include conf/machine/include/rpi-default-settings.inc
include conf/machine/include/rpi-default-versions.inc
include conf/machine/include/rpi-default-providers.inc

SOC_FAMILY = "rpi"
include conf/machine/include/soc-family.inc

IMAGE_FSTYPES ?= "tar.bz2 ext3 wic.bz2 wic.bmap"
WKS_FILE ?= "sdimage-raspberrypi.wks"

XSERVER = " \
    xserver-xorg \
    ${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "xserver-xorg-extension-glx", "", d)} \
    ${@bb.utils.contains("MACHINE_FEATURES", "vc4graphics", "xf86-video-modesetting", "xf86-video-fbdev", d)} \
    "

RPI_KERNEL_DEVICETREE_OVERLAYS ?= " \
    overlays/overlay_map.dtb \
    overlays/at86rf233.dtbo \
    overlays/disable-bt.dtbo \
    overlays/disable-wifi.dtbo \
    overlays/dwc2.dtbo \
    overlays/gpio-ir.dtbo \
    overlays/gpio-ir-tx.dtbo \
    overlays/gpio-key.dtbo \
    overlays/gpio-poweroff.dtbo \
    overlays/gpio-shutdown.dtbo \
    overlays/hifiberry-amp.dtbo \
    overlays/hifiberry-amp100.dtbo \
    overlays/hifiberry-amp3.dtbo \
    overlays/hifiberry-amp4pro.dtbo \
    overlays/hifiberry-dac.dtbo \
    overlays/hifiberry-dacplus.dtbo \
    overlays/hifiberry-dacplusadc.dtbo \
    overlays/hifiberry-dacplusadcpro.dtbo \
    overlays/hifiberry-dacplusdsp.dtbo \
    overlays/hifiberry-dacplushd.dtbo \
    overlays/hifiberry-digi-pro.dtbo \
    overlays/hifiberry-digi.dtbo \
    overlays/justboom-both.dtbo \
    overlays/justboom-dac.dtbo \
    overlays/justboom-digi.dtbo \
    overlays/i2c-gpio.dtbo \
    overlays/i2c-rtc.dtbo \
    overlays/imx219.dtbo \
    overlays/imx477.dtbo \
    overlays/imx708.dtbo \
    overlays/iqaudio-dac.dtbo \
    overlays/iqaudio-dacplus.dtbo \
    overlays/mcp2515-can0.dtbo \
    overlays/mcp2515-can1.dtbo \
    overlays/mcp3008.dtbo \
    overlays/miniuart-bt.dtbo \
    overlays/pitft22.dtbo \
    overlays/pitft28-capacitive.dtbo \
    overlays/pitft28-resistive.dtbo \
    overlays/pitft35-resistive.dtbo \
    overlays/pps-gpio.dtbo \
    overlays/rpi-ft5406.dtbo \
    overlays/rpi-poe.dtbo \
    overlays/uart0.dtbo \
    overlays/uart0-pi5.dtbo \
    overlays/uart1.dtbo \
    overlays/uart1-pi5.dtbo \
    overlays/uart2.dtbo \
    overlays/uart2-pi5.dtbo \
    overlays/uart3.dtbo \
    overlays/uart3-pi5.dtbo \
    overlays/uart4.dtbo \
    overlays/uart4-pi5.dtbo \
    overlays/uart5.dtbo \
    overlays/vc4-fkms-v3d.dtbo \
    overlays/vc4-fkms-v3d-pi4.dtbo \
    overlays/vc4-kms-v3d.dtbo \
    overlays/vc4-kms-v3d-pi4.dtbo \
    overlays/vc4-kms-v3d-pi5.dtbo \
    overlays/vc4-kms-dsi-7inch.dtbo \
    overlays/vc4-kms-dsi-ili9881-7inch.dtbo \
    overlays/rpi-backlight.dtbo \
    overlays/w1-gpio.dtbo \
    overlays/w1-gpio-pullup.dtbo \
    overlays/w1-gpio-pi5.dtbo \
    overlays/wm8960-soundcard.dtbo \
    overlays/bcm2712d0.dtbo \
    "

RPI_KERNEL_DEVICETREE ?= " \
    broadcom/bcm2708-rpi-zero.dtb \
    broadcom/bcm2708-rpi-zero-w.dtb \
    broadcom/bcm2708-rpi-b.dtb \
    broadcom/bcm2708-rpi-b-rev1.dtb \
    broadcom/bcm2708-rpi-b-plus.dtb \
    broadcom/bcm2709-rpi-2-b.dtb \
    broadcom/bcm2710-rpi-2-b.dtb \
    broadcom/bcm2710-rpi-3-b.dtb \
    broadcom/bcm2710-rpi-3-b-plus.dtb \
    broadcom/bcm2710-rpi-zero-2.dtb \
    broadcom/bcm2711-rpi-4-b.dtb \
    broadcom/bcm2711-rpi-400.dtb \
    broadcom/bcm2708-rpi-cm.dtb \
    broadcom/bcm2710-rpi-cm3.dtb \
    broadcom/bcm2711-rpi-cm4.dtb \
    broadcom/bcm2711-rpi-cm4s.dtb \
    "

KERNEL_DEVICETREE ??= " \
    ${RPI_KERNEL_DEVICETREE} \
    ${RPI_KERNEL_DEVICETREE_OVERLAYS} \
    "

# By default:
#
# * When u-boot is disabled use the "Image" format which can be directly loaded
#   by the rpi firmware.
#
# * When u-boot is enabled use the "uImage" format and the "bootm" command
#   within u-boot to load the kernel.
KERNEL_BOOTCMD ??= "bootm"
KERNEL_IMAGETYPE_UBOOT ??= "uImage"
KERNEL_IMAGETYPE_DIRECT ??= "zImage"
KERNEL_IMAGETYPE ?= "${@bb.utils.contains('RPI_USE_U_BOOT', '1', \
        '${KERNEL_IMAGETYPE_UBOOT}', '${KERNEL_IMAGETYPE_DIRECT}', d)}"

MACHINE_FEATURES += "apm usbhost keyboard vfat ext2 screen touchscreen alsa bluetooth wifi sdio ${@bb.utils.contains('DISABLE_VC4GRAPHICS', '1', '', 'vc4graphics', d)}"

# Raspberry Pi has no hardware clock
MACHINE_FEATURES_BACKFILL_CONSIDERED = "rtc"

MACHINE_EXTRA_RRECOMMENDS += "kernel-modules udev-rules-rpi"
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "${@oe.utils.conditional('ENABLE_I2C', '1', 'kernel-module-i2c-dev kernel-module-i2c-bcm2708', '', d)}"
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "${@oe.utils.conditional('ENABLE_IR', '1', 'kernel-module-gpio-ir kernel-module-gpio-ir-tx', '', d)}"
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS += "${@oe.utils.conditional('ENABLE_GPIO_SHUTDOWN', '1', 'gpio-shutdown kernel-module-gpio-keys', '', d)}"

SERIAL_CONSOLES_CHECK ??= "${SERIAL_CONSOLES}"

# The name of the deploy directory for raspberry pi boot files.
# This variable is referred to by recipes fetching / generating the files.
BOOTFILES_DIR_NAME ?= "bootfiles"

def make_dtb_boot_files(d):
    # Generate IMAGE_BOOT_FILES entries for device tree files listed in
    # KERNEL_DEVICETREE.
    alldtbs = d.getVar('KERNEL_DEVICETREE')

    # DTBs may be built out of kernel with devicetree.bbclass
    if not alldtbs:
        return ''

    def transform(dtb):
        base = os.path.basename(dtb)
        if dtb.endswith('dtbo') or base == 'overlay_map.dtb':
            # overlay dtb:
            # eg: overlays/hifiberry-amp.dtbo has:
            #     DEPLOYDIR file: hifiberry-amp.dtbo
            #     destination: overlays/hifiberry-amp.dtbo
            return '{};{}'.format(base, dtb)
        elif dtb.endswith('dtb'):
            # eg: whatever/bcm2708-rpi-b.dtb has:
            #     DEPLOYDIR file: bcm2708-rpi-b.dtb
            #     destination: bcm2708-rpi-b.dtb
            return base

    return ' '.join([transform(dtb) for dtb in alldtbs.split(' ') if dtb])

RPI_EXTRA_IMAGE_BOOT_FILES ?= " \
	${@bb.utils.contains('RPI_USE_U_BOOT', '1', \
		'${KERNEL_IMAGETYPE} u-boot.bin;${SDIMG_KERNELIMAGE} boot.scr', \
		'${KERNEL_IMAGETYPE};${SDIMG_KERNELIMAGE}', d)} \
	"

IMAGE_BOOT_FILES ?= "${BOOTFILES_DIR_NAME}/* \
                 ${@make_dtb_boot_files(d)} \
                 ${RPI_EXTRA_IMAGE_BOOT_FILES} \
                 "

EXTRA_IMAGEDEPENDS += "rpi-bootfiles"

do_image_wic[depends] += " \
    virtual/kernel:do_deploy \
    rpi-bootfiles:do_deploy \
    ${@bb.utils.contains('RPI_USE_U_BOOT', '1', 'u-boot:do_deploy', '',d)} \
    "

do_image_wic[recrdeps] = "do_build"

# The kernel image is installed into the FAT32 boot partition and does not need
# to also be installed into the rootfs.
RDEPENDS:${KERNEL_PACKAGE_NAME}-base = ""
