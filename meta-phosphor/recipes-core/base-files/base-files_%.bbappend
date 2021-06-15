FILESEXTRAPATHS_prepend_df-obmc-ubi-fs := "${THISDIR}/${PN}/df-ubi:"
FILESEXTRAPATHS_prepend_df-phosphor-mmc := "${THISDIR}/${PN}/df-mmc:"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN}_append_df-obmc-ubi-fs = " preinit-mounts"

SRC_URI += " \
    file://50-rp_filter.conf \
"

do_install_append() {
    sed -i 's/\(\(passwd\|group\):\s*\).*/\1files systemd/' \
        "${D}${sysconfdir}/nsswitch.conf"

    install -d ${D}/srv

    install -d ${D}/${libdir}/sysctl.d
    install -D -m 644 ${WORKDIR}/50-rp_filter.conf ${D}/${libdir}/sysctl.d/50-rp_filter.conf
}
