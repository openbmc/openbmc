FILESEXTRAPATHS:prepend:sbp1 := "${THISDIR}/${PN}:"

SRC_URI:append:sbp1 = " \
    file://blacklist.json \
    "

do_install:append:sbp1 () {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${PN}/blacklist.json
}
