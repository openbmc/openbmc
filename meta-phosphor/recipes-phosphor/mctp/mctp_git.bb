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
SRCREV = "96d5449211228c141b65dbe60a121c23f6c79cfb"
CONFFILES_${PN} = "${sysconfdir}/default/mctp"

DEPENDS += "autoconf-archive-native \
            systemd \
            "

SYSTEMD_SERVICE_${PN} = "mctp-demux.service"

do_install_append() {
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/mctp
}

S = "${WORKDIR}/git"
