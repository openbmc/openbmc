SUMMARY = "Phosphor Software Management"
DESCRIPTION = "Phosphor Software Manager provides a set of system software \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
PR = "r1"
PV = "1.0+git${SRCPV}"

require ${BPN}.inc

SOFTWARE_MGR_PACKAGES = " \
    ${PN}-version \
    ${PN}-download-mgr \
    ${PN}-updater \
    ${PN}-updater-ubi \
    ${PN}-updater-mmc \
    ${PN}-sync \
"
PACKAGE_BEFORE_PN += "${SOFTWARE_MGR_PACKAGES}"
ALLOW_EMPTY_${PN} = "1"

DBUS_PACKAGES = "${SOFTWARE_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

PACKAGECONFIG[verify_signature] = " \
    -Dverify-full-signature=enabled, \
    -Dverify-full-signature=disabled"
PACKAGECONFIG[sync_bmc_files] = "-Dsync-bmc-files=enabled, -Dsync-bmc-files=disabled"
PACKAGECONFIG[ubifs_layout] = "-Dbmc-layout=ubi"
PACKAGECONFIG[mmc_layout] = "-Dbmc-layout=mmc"
PACKAGECONFIG[flash_bios] = "-Dhost-bios-upgrade=enabled, -Dhost-bios-upgrade=disabled"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit python3native
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'obmc-ubi-fs', 'phosphor-software-manager-ubi-fs', '', d)}
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-mmc', 'phosphor-software-manager-mmc', '', d)}

DEPENDS += " \
    openssl \
    phosphor-dbus-interfaces \
    phosphor-logging \
    ${PYTHON_PN}-sdbus++-native \
    sdbusplus \
"

RDEPENDS_${PN}-updater += " \
    bash \
    virtual-obmc-image-manager \
    ${@bb.utils.contains('PACKAGECONFIG', 'verify_signature', 'phosphor-image-signing', '', d)} \
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
    force-reboot.service \
    obmc-flash-bmc-setenv@.service \
    reboot-guard-disable.service \
    reboot-guard-enable.service \
    usr-local.mount \
"

SYSTEMD_SERVICE_${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'flash_bios', 'obmc-flash-host-bios@.service', '', d)}"

S = "${WORKDIR}/git"

do_install_append() {
    install -d ${D}/usr/local
}
