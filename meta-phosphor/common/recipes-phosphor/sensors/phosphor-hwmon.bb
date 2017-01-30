SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"

RDEPENDS_${PN} += "\
        libsystemd \
        "

SRC_URI += "git://github.com/openbmc/phosphor-hwmon"
SRC_URI += "file://70-hwmon.rules"

SRCREV = "30dbceec70d96487016b129334ba539b9e161c08"

S = "${WORKDIR}/git"

do_install_append() {

        install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-hwmon.rules ${D}/${base_libdir}/udev/rules.d/
}
