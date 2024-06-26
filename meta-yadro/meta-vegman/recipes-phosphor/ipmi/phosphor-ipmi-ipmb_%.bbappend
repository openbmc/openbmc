FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://ipmb-channels.json"

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/ipmb-channels.json \
                   ${D}${datadir}/ipmbbridge/
}
