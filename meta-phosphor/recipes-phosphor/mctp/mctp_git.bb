SUMMARY = "MCTP stack"
DESCRIPTION = "MCTP library implementing the MCTP base specification"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit systemd
inherit autotools pkgconfig

HOMEPAGE = "https://github.com/openbmc/libmctp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0d30807bb7a4f16d36e96b78f9ed8fae"
SRC_URI = "git://github.com/openbmc/libmctp \
	   file://default"
SRCREV = "34d4c96fcf9691e060d8a7f56633bf196c82a52f"

DEPENDS += "autoconf-archive-native \
            systemd \
            "

SYSTEMD_SERVICE_${PN} = "mctp-demux.service"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir,systemd"

PACKAGECONFIG[astlpc-raw-kcs] = "--enable-astlpc-raw-kcs,--disable-astlpc-raw-kcs,udev,udev"

CONFFILES_${PN} = "${sysconfdir}/default/mctp"

do_install_append() {
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/mctp
}

S = "${WORKDIR}/git"
