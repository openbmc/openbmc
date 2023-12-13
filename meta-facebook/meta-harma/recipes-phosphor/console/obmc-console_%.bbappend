FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd

SRC_URI:append = " \
    file://setup-uart-routing \
    file://setup-uart-routing.conf \
    "

do_install:append() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/setup-uart-routing ${D}${libexecdir}/${PN}
}

SYSTEMD_OVERRIDE:${PN}:append = " setup-uart-routing.conf:obmc-console@ttyS2.service.d/setup-uart-routing.conf"
RDEPENDS:${PN}:append = " bash"
