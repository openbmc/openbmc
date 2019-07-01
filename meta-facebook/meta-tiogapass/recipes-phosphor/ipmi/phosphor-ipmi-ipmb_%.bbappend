FILESEXTRAPATHS_prepend_tiogapass := "${THISDIR}/${PN}:"

SRC_URI_append_tiogapass = " file://ipmb-channels.json"

do_install_append_tiogapass(){
    install -m 0644 -D ${WORKDIR}/ipmb-channels.json \
                   ${D}/usr/share/ipmbbridge
}
