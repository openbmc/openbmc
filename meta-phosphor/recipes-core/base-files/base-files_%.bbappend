FILESEXTRAPATHS_prepend_df-obmc-ubi-fs := "${THISDIR}/${PN}/df-ubi:"
FILESEXTRAPATHS_prepend_df-phosphor-mmc := "${THISDIR}/${PN}/df-mmc:"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN}_append_df-obmc-ubi-fs = " preinit-mounts"

SRC_URI += " \
    file://50-rp_filter.conf \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'file://nsswitch_ldap.conf', '', d)}"

do_install_append() {

    install -d ${D}/srv

    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ldap', d)}" ]; then
        install -D -m 600 ${WORKDIR}/nsswitch_ldap.conf ${D}/${sysconfdir}/nsswitch.conf
    fi

    install -d ${D}/${libdir}/sysctl.d
    install -D -m 644 ${WORKDIR}/50-rp_filter.conf ${D}/${libdir}/sysctl.d/50-rp_filter.conf
}
