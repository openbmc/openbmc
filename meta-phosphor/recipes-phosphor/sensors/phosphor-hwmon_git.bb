SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit meson
inherit obmc-phosphor-systemd

PACKAGECONFIG ??= ""
# Meson configure option to enable/disable max31785-msl
PACKAGECONFIG[max31785-msl] = "-Denable-max31785-msl=true, -Denable-max31785-msl=false"

PACKAGE_BEFORE_PN = "max31785-msl"
SYSTEMD_PACKAGES = "${PN} max31785-msl"

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"
SYSTEMD_SERVICE_max31785-msl = "${@bb.utils.contains('PACKAGECONFIG', 'max31785-msl', 'phosphor-max31785-msl@.service', '', d)}"

DEPENDS += " \
        sdbusplus \
        sdeventplus \
        stdplus \
        phosphor-dbus-interfaces \
        phosphor-logging \
        gpioplus \
        cli11 \
        "

FILES_${PN} += "${base_libdir}/systemd/system/xyz.openbmc_project.Hwmon@.service"
RDEPENDS_${PN} += "\
        bash \
        "

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_phosphor-hwmon-config}"

FILES_max31785-msl = "\
        ${base_libdir}/systemd/system/phosphor-max31785-msl@.service \
        ${bindir}/max31785-msl \
        "
RDEPENDS_max31785-msl = "${VIRTUAL-RUNTIME_base-utils} i2c-tools bash"

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"

SRCREV = "5b520cf494ad65be2d336f60ee622efc456c2e3f"

S = "${WORKDIR}/git"
