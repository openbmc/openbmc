FILESEXTRAPATHS:prepend:harma := "${THISDIR}/${PN}:"

SRC_URI:append:harma = " \
    file://blacklist.json \
    "

do_install:append:harma () {
    install -m 0644 -D ${WORKDIR}/blacklist.json ${D}${datadir}/${PN}/blacklist.json
}
