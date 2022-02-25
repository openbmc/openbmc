FILESEXTRAPATHS:prepend:harden := "${THISDIR}/files:"

SRC_URI:append:harden = " file://mountall.sh"

do_install:append:harden() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/mountall.sh ${D}${sysconfdir}/init.d
}
