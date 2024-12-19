FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://blacklist.json \
           "

do_install:append() {
    install -d ${D}${datadir}/${PN}
    install -m 0444 ${UNPACKDIR}/blacklist.json ${D}${datadir}/${PN}
}
