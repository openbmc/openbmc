SUMMARY = "Phosphor Software Management"
DESCRIPTION = "Phosphor Software Manager provides a set of system software \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-bmc-code-mgmt"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SOFTWARE_MGR_PACKAGES = " \
    ${PN}-version \
    ${PN}-download-mgr \
    ${PN}-updater \
"
PACKAGES =+ "${SOFTWARE_MGR_PACKAGES}"
PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = "${SOFTWARE_MGR_PACKAGES}"
RDEPENDS_${PN}-staticdev = "${SOFTWARE_MGR_PACKAGES}"

DBUS_PACKAGES = "${SOFTWARE_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service
inherit pythonnative

DEPENDS += " \
    autoconf-archive-native \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
"

RDEPENDS_${PN}-version += " \
    phosphor-logging \
    phosphor-dbus-interfaces \
    sdbusplus \
"
RDEPENDS_${PN}-download-mgr += " \
    phosphor-logging \
    phosphor-dbus-interfaces \
    sdbusplus \
"
RDEPENDS_${PN}-updater += " \
    phosphor-logging \
    phosphor-dbus-interfaces \
    sdbusplus \
"

FILES_${PN}-version += "${sbindir}/phosphor-version-software-manager"
FILES_${PN}-download-mgr += "${sbindir}/phosphor-download-manager"
FILES_${PN}-updater += " \
    ${sbindir}/phosphor-image-updater \
    ${sbindir}/obmc-flash-bmc \
    "
DBUS_SERVICE_${PN}-version += "xyz.openbmc_project.Software.Version.service"
DBUS_SERVICE_${PN}-download-mgr += "xyz.openbmc_project.Software.Download.service"
DBUS_SERVICE_${PN}-updater += "xyz.openbmc_project.Software.BMC.Updater.service"

SYSTEMD_SERVICE_${PN}-updater += " \
    obmc-flash-bmc-ubirw.service \
    obmc-flash-bmc-ubiro@.service \
    obmc-flash-bmc-setenv@.service \
    obmc-flash-bmc-ubirw-remove.service \
    obmc-flash-bmc-ubiro-remove@.service \
    "

# Name of the mtd device where the ubi volumes should be created
BMC_RW_MTD ??= "pnor"
BMC_RO_MTD ??= "pnor"
# TODO Change kernel location to primary BMC chip once the rofs/rwfs mtd devices
# are merged into a single ubi one openbmc/openbmc#1942
BMC_KERNEL_MTD ??= "pnor"
SYSTEMD_SUBSTITUTIONS += "RW_MTD:${BMC_RW_MTD}:obmc-flash-bmc-ubirw.service"
SYSTEMD_SUBSTITUTIONS += "RO_MTD:${BMC_RO_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "KERNEL_MTD:${BMC_KERNEL_MTD}:obmc-flash-bmc-ubiro@.service"

SRC_URI += "file://obmc-flash-bmc"
do_install_append() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/obmc-flash-bmc ${D}${sbindir}/obmc-flash-bmc
}

SRC_URI += "git://github.com/openbmc/phosphor-bmc-code-mgmt"
SRCREV = "bed88af902121a0fe359f780fbf61b0f9612b941"

S = "${WORKDIR}/git"
