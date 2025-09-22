SUMMARY = "MCTP stack"
DESCRIPTION = "MCTP library implementing the MCTP base specification"
HOMEPAGE = "https://github.com/openbmc/libmctp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0d30807bb7a4f16d36e96b78f9ed8fae"
DEPENDS += "autoconf-archive-native \
            systemd \
           "
SRCREV = "b04447c2d8c7d4659350a808ade9b1345794e3fc"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} pcap"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir,systemd"
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
        install -m 0644 ${UNPACKDIR}/default ${D}${sysconfdir}/default/mctp
}

CONFFILES:${PN} = "${sysconfdir}/default/mctp"
