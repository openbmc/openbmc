inherit obmc-phosphor-utils

FILESEXTRAPATHS_prepend := "${@mf_enabled(d, 'obmc-ubi-fs', '${THISDIR}/${PN}:')}"

RDEPENDS_${PN}_append += "${@mf_enabled(d, 'obmc-ubi-fs', 'mount-overlay-help')}"

do_install_append() {
    install -d ${D}/srv
}
