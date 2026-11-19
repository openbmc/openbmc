FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${BPN}:"

SRC_URI:append:kudo = " \
    file://blacklist.json \
    "

do_install:append:kudo () {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${BPN}/blacklist.json
}
