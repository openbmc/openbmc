FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}/${MACHINE}:"

SRC_URI_append = " file://ipmb-channels.json"

do_install_append() {
    install -m 0644 -D ${WORKDIR}/ipmb-channels.json \
                   ${D}/usr/share/ipmbbridge
}
