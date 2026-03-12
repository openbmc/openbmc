# Common configuration for Arm firmware components

# Firmware packages are always machine-specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Allow all firmware to be debugged together
FIRMWARE_DEBUG_BUILD ?= "${@oe.utils.vartrue('DEBUG_BUILD', '1', '0', d)}"

# Use ${MACHINE} as the default platform name for firmware
FIRMWARE_PLATFORM ?= "${MACHINE}"

# Provide a standard folder layout for firmware packages
FIRMWARE_BASE_DIR ?= "/firmware"
FIRMWARE_DIR ?= "${FIRMWARE_BASE_DIR}/${PN}"
FILES:${PN} = "${FIRMWARE_DIR}/*.bin"
FILES:${PN}-dbg = "${FIRMWARE_DIR}/*.elf"
SYSROOT_DIRS += "${FIRMWARE_DIR}"

# Provide a default deploy implementation, which deploys to a subdirectory
# of ${DEPLOY_DIR_IMAGE}
inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}/${PN}
    cp -rf ${D}${FIRMWARE_DIR}/* ${DEPLOYDIR}/${PN}
}
addtask deploy after do_install
