FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

SRC_URI:append:s6q = " file://s6q-ipmb-channels.json"

do_install:append:s6q(){
    install -m 0644 -D ${UNPACKDIR}/s6q-ipmb-channels.json \
                   ${D}/${datadir}/ipmbbridge/ipmb-channels.json
}
