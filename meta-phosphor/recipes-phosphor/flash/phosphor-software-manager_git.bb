SUMMARY = "Phosphor Software Management"
DESCRIPTION = "Phosphor Software Manager provides a set of system software \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
PR = "r1"
PV = "1.0+git${SRCPV}"

require ${PN}.inc

SOFTWARE_MGR_PACKAGES = " \
    ${PN}-version \
    ${PN}-download-mgr \
    ${PN}-updater \
    ${PN}-updater-ubi \
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
PACKAGECONFIG[ubifs_layout] = "--enable-ubifs_layout"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service
inherit pythonnative
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'obmc-ubi-fs', 'phosphor-software-manager-ubi-fs', '', d)}

DEPENDS += " \
    autoconf-archive-native \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    sdbus++-native \
"

RDEPENDS_${PN}-updater += " \
    bash \
    virtual-obmc-image-manager \
"
EXTRA_OECONF += " \
    ACTIVE_BMC_MAX_ALLOWED=1 \
    MEDIA_DIR=/run/media \
"

RPROVIDES_${PN}-version += " \
    virtual-obmc-image-manager \
"

FILES_${PN}-version += "${bindir}/phosphor-version-software-manager ${exec_prefix}/lib/tmpfiles.d/software.conf"
FILES_${PN}-download-mgr += "${bindir}/phosphor-download-manager"
FILES_${PN}-updater += " \
    ${bindir}/phosphor-image-updater \
    ${bindir}/obmc-flash-bmc \
    /usr/local \
    "
FILES_${PN}-sync += " \
    ${bindir}/phosphor-sync-software-manager \
    ${sysconfdir}/synclist \
    "
DBUS_SERVICE_${PN}-version += "xyz.openbmc_project.Software.Version.service"
DBUS_SERVICE_${PN}-download-mgr += "xyz.openbmc_project.Software.Download.service"
DBUS_SERVICE_${PN}-updater += "xyz.openbmc_project.Software.BMC.Updater.service"
DBUS_SERVICE_${PN}-sync += "xyz.openbmc_project.Software.Sync.service"

SYSTEMD_SERVICE_${PN}-updater += " \
    obmc-flash-bmc-setenv@.service \
    usr-local.mount \
"

S = "${WORKDIR}/git"

do_install_append() {
    install -d ${D}/usr/local
}
