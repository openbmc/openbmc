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
    ${PN}-sync \
"
PACKAGE_BEFORE_PN += "${SOFTWARE_MGR_PACKAGES}"
ALLOW_EMPTY_${PN} = "1"

DBUS_PACKAGES = "${SOFTWARE_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

PACKAGECONFIG[verify_signature] = "--enable-verify_signature,--disable-verify_signature"
PACKAGECONFIG[sync_bmc_files] = "--enable-sync_bmc_files,--disable-sync_bmc_files"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service
inherit pythonnative

DEPENDS += " \
    autoconf-archive-native \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    sdbus++-native \
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
    virtual-obmc-image-manager \
    bash \
"
RDEPENDS_${PN}-updater_append_df-obmc-ubi-fs = " \
    mtd-utils-ubifs \
"

RPROVIDES_${PN}-version += " \
    virtual-obmc-image-manager \
"

FILES_${PN}-version += "${sbindir}/phosphor-version-software-manager"
FILES_${PN}-download-mgr += "${sbindir}/phosphor-download-manager"
FILES_${PN}-updater += " \
    ${sbindir}/phosphor-image-updater \
    ${sbindir}/obmc-flash-bmc \
    /usr/local \
    "
FILES_${PN}-sync += " \
    ${sbindir}/phosphor-sync-software-manager \
    ${sysconfdir}/synclist \
    "
DBUS_SERVICE_${PN}-version += "xyz.openbmc_project.Software.Version.service"
DBUS_SERVICE_${PN}-download-mgr += "xyz.openbmc_project.Software.Download.service"
DBUS_SERVICE_${PN}-updater += "xyz.openbmc_project.Software.BMC.Updater.service"
DBUS_SERVICE_${PN}-sync += "xyz.openbmc_project.Software.Sync.service"

SYSTEMD_SERVICE_${PN}-updater += " \
    obmc-flash-bmc-ubirw.service \
    obmc-flash-bmc-ubiro@.service \
    obmc-flash-bmc-setenv@.service \
    obmc-flash-bmc-ubirw-remove.service \
    obmc-flash-bmc-ubiro-remove@.service \
    usr-local.mount \
    obmc-flash-bmc-ubiremount.service \
    obmc-flash-bmc-updateubootvars@.service \
    reboot-guard-enable.service \
    reboot-guard-disable.service \
    obmc-flash-bmc-cleanup.service \
    obmc-flash-bmc-mirroruboot.service \
    "

# Name of the mtd device where the ubi volumes should be created
BMC_RW_MTD ??= "bmc"
BMC_RO_MTD ??= "bmc"
BMC_KERNEL_MTD ??= "bmc"
BMC_RW_SIZE ??= "0x600000"
SYSTEMD_SUBSTITUTIONS += "RW_MTD:${BMC_RW_MTD}:obmc-flash-bmc-ubirw.service"
SYSTEMD_SUBSTITUTIONS += "RO_MTD:${BMC_RO_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "KERNEL_MTD:${BMC_KERNEL_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "RW_SIZE:${BMC_RW_SIZE}:obmc-flash-bmc-ubirw.service"

SRC_URI += "file://obmc-flash-bmc"
SRC_URI += "file://synclist"
do_install_append() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/obmc-flash-bmc ${D}${sbindir}/obmc-flash-bmc
    install -d ${D}/usr/local

    if [ -f ${WORKDIR}/build/phosphor-sync-software-manager ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/synclist ${D}${sysconfdir}/synclist
    fi
}

SRC_URI += "git://github.com/openbmc/phosphor-bmc-code-mgmt"
SRCREV = "4b35dd31529e489b5f2f74c1d34900713c8032fc"

S = "${WORKDIR}/git"
