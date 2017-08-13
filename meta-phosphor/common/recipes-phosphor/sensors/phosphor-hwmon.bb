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
SRC_URI += "file://70-iio.rules"

SRCREV = "f9c83c488d9ce45ef57e809fb17bc56763c515af"

S = "${WORKDIR}/git"

do_install_append() {

        install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-hwmon.rules ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-iio.rules ${D}/${base_libdir}/udev/rules.d/
}
