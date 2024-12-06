FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:system1 = " file://ipmb-channels.json"

do_install:append:system1(){
    install -m 0644 -D ${UNPACKDIR}/ipmb-channels.json \
                   ${D}${datadir}/ipmbbridge/
}
