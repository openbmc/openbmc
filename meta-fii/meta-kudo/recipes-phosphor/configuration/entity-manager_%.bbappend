FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

SRC_URI:append:kudo = " \
    file://blacklist.json \
    "

do_install:append:kudo () {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${PN}/blacklist.json
}
