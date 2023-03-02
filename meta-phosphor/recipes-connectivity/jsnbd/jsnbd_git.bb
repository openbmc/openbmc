SUMMARY = "Network Block Device Proxy"
HOMEPAGE = "https://github.com/openbmc/jsnbd"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=3b83ef96387f14655fc854ddc3c6bd57"
DEPENDS += "json-c"
DEPENDS += "udev"
SRCREV = "d59ecca2963a42f2d7b29df220a392ee52401a2d"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "\ 
    git://github.com/openbmc/jsnbd;branch=master;protocol=https \
    file://state_hook \
    "

S = "${WORKDIR}/git"

inherit autotools pkgconfig

do_install:append() {
    install -d ${D}${sysconfdir}/nbd-proxy/
    install -m 0644 ${NBD_PROXY_CONFIG_JSON} ${D}${sysconfdir}/nbd-proxy/config.json
    install -m 0755 ${WORKDIR}/state_hook ${D}${sysconfdir}/nbd-proxy/state
}

FILES:${PN} += "${sysconfdir}/nbd-proxy/state"

RDEPENDS:${PN} += "nbd-client"

NBD_PROXY_CONFIG_JSON ??= "${S}/config.sample.json"
