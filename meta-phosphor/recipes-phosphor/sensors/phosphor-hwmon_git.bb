SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit meson pkgconfig
inherit obmc-phosphor-systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[max31785-msl] = "-Denable-max31785-msl=true, -Denable-max31785-msl=false"

PACKAGE_BEFORE_PN = "max31785-msl"
SYSTEMD_PACKAGES = "${PN} max31785-msl"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"
SYSTEMD_SERVICE_max31785-msl = "${@bb.utils.contains('PACKAGECONFIG', 'max31785-msl', 'phosphor-max31785-msl@.service', '', d)}"

DEPENDS += "autoconf-archive-native"
DEPENDS += " \
        sdbusplus \
        sdeventplus \
        stdplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        gpioplus \
        cli11 \
        "


RDEPENDS_${PN} += "\
        bash \
        "

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_phosphor-hwmon-config}"

FILES_max31785-msl = "${bindir}/max31785-msl"
RDEPENDS_max31785-msl = "${VIRTUAL-RUNTIME_base-utils} i2c-tools bash"

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"
SRC_URI += "file://70-hwmon.rules"
SRC_URI += "file://70-iio.rules"
SRC_URI += "file://start_hwmon.sh"

SRCREV = "f4414cba09577e906b7b022a96b41e6a859da149"

S = "${WORKDIR}/git"

do_install_append() {

        install -d ${D}/${base_libdir}/udev/rules.d/
        install -m 0644 ${WORKDIR}/70-hwmon.rules ${D}/${base_libdir}/udev/rules.d/
        install -m 0644 ${WORKDIR}/70-iio.rules ${D}/${base_libdir}/udev/rules.d/

        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/start_hwmon.sh ${D}${bindir}
}
