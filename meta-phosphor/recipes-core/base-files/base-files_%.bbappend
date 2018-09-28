FILESEXTRAPATHS_prepend_df-obmc-ubi-fs := "${THISDIR}/${PN}/df-ubi:"

RDEPENDS_${PN}_append_df-obmc-ubi-fs = " preinit-mounts"

do_install_append() {
    install -d ${D}/srv
}
