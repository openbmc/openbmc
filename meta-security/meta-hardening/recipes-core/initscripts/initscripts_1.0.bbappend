FILESEXTRAPATHS:prepend_harden := "${THISDIR}/files:"

SRC_URI:append_harden = " file://mountall.sh"

do_install:append_harden() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/mountall.sh ${D}${sysconfdir}/init.d
}
