FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append_harden = " file://mountall.sh"

do_install_append_harden() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/mountall.sh ${D}${sysconfdir}/init.d
}
