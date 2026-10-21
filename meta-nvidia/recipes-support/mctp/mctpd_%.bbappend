FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://mctpd.conf.in \
"

MCTPD_POLL_INTERVAL_MS ?= ""
MCTPD_POLL_INTERVAL_MS:nvl32-obmc = "2500"
MCTPD_POLL_INTERVAL_MS:vr-nvl-obmc = "8000"
MCTPD_POLL_INTERVAL_MS:vr-c2-obmc = "8000"

PACKAGE_ARCH:nvl32-obmc = "${MACHINE_ARCH}"
PACKAGE_ARCH:vr-nvl-obmc = "${MACHINE_ARCH}"
PACKAGE_ARCH:vr-c2-obmc = "${MACHINE_ARCH}"

do_install:append() {
    if [ -n "${MCTPD_POLL_INTERVAL_MS}" ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${UNPACKDIR}/mctpd.conf.in ${D}${sysconfdir}/mctpd.conf
        sed -i "s/@MCTPD_POLL_INTERVAL_MS@/${MCTPD_POLL_INTERVAL_MS}/g" \
            ${D}${sysconfdir}/mctpd.conf
    fi
}

FILES:${PN}:append = " \
    ${sysconfdir}/mctpd.conf \
"
