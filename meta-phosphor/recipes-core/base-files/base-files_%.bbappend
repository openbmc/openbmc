FILESEXTRAPATHS_prepend_df-obmc-ubi-fs := "${THISDIR}/${PN}/df-ubi:"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN}_append_df-obmc-ubi-fs = " preinit-mounts"

SRC_URI += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ldap', 'file://nsswitch_ldap.conf', '', d)}"

do_install_append() {

    install -d ${D}/srv

    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ldap', d)}" ]; then
        install -D -m 600 ${WORKDIR}/nsswitch_ldap.conf ${D}/${sysconfdir}/
    fi
}
