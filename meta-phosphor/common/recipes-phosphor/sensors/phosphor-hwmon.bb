SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"

DEPENDS += "autoconf-archive-native"
DEPENDS += " \
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        "


RDEPENDS_${PN} += "\
        sdbusplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        "

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_phosphor-hwmon-config}"

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"
SRC_URI += "file://70-hwmon.rules"

SRCREV = "bf86e72ebc4b33264ac0d8d6c8f0383f76b44e0f"

S = "${WORKDIR}/git"

do_install_append() {

        install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-hwmon.rules ${D}/${base_libdir}/udev/rules.d/
}
