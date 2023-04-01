SUMMARY = "MCTP stack"
DESCRIPTION = "MCTP library implementing the MCTP base specification"
HOMEPAGE = "https://github.com/openbmc/libmctp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0d30807bb7a4f16d36e96b78f9ed8fae"
DEPENDS += "autoconf-archive-native \
            systemd \
           "
SRCREV = "2608b2943b7751b6de3abf83cc16d83f913c1651"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} pcap"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir,systemd"
PACKAGECONFIG[astlpc-raw-kcs] = "--enable-astlpc-raw-kcs,--disable-astlpc-raw-kcs,udev,udev"
PACKAGECONFIG[pcap] = "--enable-capture,--disable-capture,libpcap,"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/libmctp;branch=master;protocol=https \
           file://default"

SYSTEMD_SERVICE:${PN} = "mctp-demux.service \
                         mctp-demux.socket \
                        "
S = "${WORKDIR}/git"

inherit systemd
inherit autotools pkgconfig

do_install:append() {
        install -d ${D}${sysconfdir}/default
        install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/mctp
}

CONFFILES:${PN} = "${sysconfdir}/default/mctp"
