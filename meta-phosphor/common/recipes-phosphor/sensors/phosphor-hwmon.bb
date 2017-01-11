SUMMARY = "OpenBMC hwmon poller"
DESCRIPTION = "OpenBMC hwmon poller."
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} = "xyz.openbmc_project.Hwmon@.service"

PROVIDES += "virtual/obmc-sensors-hwmon"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"

RPROVIDES_${PN} += "virtual-obmc-sensors-hwmon"

RDEPENDS_${PN} += "\
        libsystemd \
        "

SRC_URI += "git://github.com/bradbishop/phosphor-hwmon;branch=snap-edf908b"
SRC_URI += "file://hwmon.rules"

SRCREV = "edf908bdbc5b9ac69048ab3fdb4df1e367f0a230"

S = "${WORKDIR}/git"

do_install_append() {

	install -d ${D}/${sysconfdir}/udev/rules.d/
	install ${WORKDIR}/hwmon.rules ${D}/${sysconfdir}/udev/rules.d/
}
