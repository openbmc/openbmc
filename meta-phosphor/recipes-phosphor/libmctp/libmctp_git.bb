SUMMARY = "MCTP stack"
DESCRIPTION = "MCTP library implementing the MCTP base specification"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit systemd
inherit autotools pkgconfig

HOMEPAGE = "https://github.com/openbmc/libmctp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0d30807bb7a4f16d36e96b78f9ed8fae"
SRC_URI = "git://github.com/openbmc/libmctp;branch=master;protocol=https \
	   file://default"
SRCREV = "f39c38575828a53d59883ae999f03e6b48ee7c24"

DEPENDS += "autoconf-archive-native \
            systemd \
           "

SYSTEMD_SERVICE:${PN} = "mctp-demux.service \
                         mctp-demux.socket \
                        "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} pcap"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir,systemd"

PACKAGECONFIG[astlpc-raw-kcs] = "--enable-astlpc-raw-kcs,--disable-astlpc-raw-kcs,udev,udev"
PACKAGECONFIG[pcap] = "--enable-capture,--disable-capture,libpcap,"

CONFFILES:${PN} = "${sysconfdir}/default/mctp"

do_install:append() {
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/mctp
}

S = "${WORKDIR}/git"
