FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:sbp1 = " file://ipmb-channels.json"

do_install:append:sbp1(){
    install -m 0644 -D ${WORKDIR}/ipmb-channels.json \
                   ${D}${datadir}/ipmbbridge/
}
