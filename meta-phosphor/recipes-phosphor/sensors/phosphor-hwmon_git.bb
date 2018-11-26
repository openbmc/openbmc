SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig
inherit obmc-phosphor-systemd

PACKAGE_BEFORE_PN = "max31785-msl"
SYSTEMD_PACKAGES = "${PN} max31785-msl"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"
SYSTEMD_SERVICE_max31785-msl = "phosphor-max31785-msl@.service"

DEPENDS += "autoconf-archive-native"
DEPENDS += " \
        sdbusplus \
        sdeventplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        gpioplus \
        "


RDEPENDS_${PN} += "\
        bash \
        "

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_phosphor-hwmon-config}"

FILES_max31785-msl = "${bindir}/max31785-msl"
RDEPENDS_max31785-msl = "${VIRTUAL-RUNTIME_base-utils} i2c-tools"

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"
SRC_URI += "file://70-hwmon.rules"
SRC_URI += "file://70-iio.rules"
SRC_URI += "file://start_hwmon.sh"

SRCREV = "5a842c3e3931993bcb184300b289c8a82ecd6322"

S = "${WORKDIR}/git"

do_install_append() {

        install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-hwmon.rules ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-iio.rules ${D}/${base_libdir}/udev/rules.d/

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/start_hwmon.sh ${D}${bindir}
}
