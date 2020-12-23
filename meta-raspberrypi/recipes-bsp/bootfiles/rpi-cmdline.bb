SUMMARY = "cmdline.txt file used to boot the kernel on a Raspberry Pi device"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "^rpi$"
INHIBIT_DEFAULT_DEPS = "1"
inherit deploy nopackages

CMDLINE_DWC_OTG ?= "dwc_otg.lpm_enable=0"
CMDLINE_ROOTFS ?= "root=/dev/mmcblk0p2 rootfstype=ext4 rootwait"

CMDLINE_SERIAL ?= "${@oe.utils.conditional("ENABLE_UART", "1", "console=serial0,115200", "", d)}"

CMDLINE_CMA ?= "${@oe.utils.conditional("RASPBERRYPI_CAMERA_V2", "1", "cma=64M", "", d)}"

CMDLINE_PITFT ?= "${@bb.utils.contains("MACHINE_FEATURES", "pitft", "fbcon=map:10 fbcon=font:VGA8x8", "", d)}"

# Add the kernel debugger over console kernel command line option if enabled
CMDLINE_KGDB ?= '${@oe.utils.conditional("ENABLE_KGDB", "1", "kgdboc=serial0,115200", "", d)}'

# Disable rpi logo on boot
CMDLINE_LOGO ?= '${@oe.utils.conditional("DISABLE_RPI_BOOT_LOGO", "1", "logo.nologo", "", d)}'

# You can define CMDLINE_DEBUG as "debug" in your local.conf or distro.conf
# to enable kernel debugging.
CMDLINE_DEBUG ?= ""

CMDLINE = " \
    ${CMDLINE_DWC_OTG} \
    ${CMDLINE_SERIAL} \
    ${CMDLINE_ROOTFS} \
    ${CMDLINE_CMA} \
    ${CMDLINE_KGDB} \
    ${CMDLINE_LOGO} \
    ${CMDLINE_PITFT} \
    ${CMDLINE_DEBUG} \
    "

do_compile() {
    echo "${CMDLINE}" > "${WORKDIR}/cmdline.txt"
}

do_deploy() {
    install -d "${DEPLOYDIR}/${BOOTFILES_DIR_NAME}"
    install -m 0644 "${WORKDIR}/cmdline.txt" "${DEPLOYDIR}/${BOOTFILES_DIR_NAME}"
}

addtask deploy before do_build after do_install
do_deploy[dirs] += "${DEPLOYDIR}/${BOOTFILES_DIR_NAME}"
