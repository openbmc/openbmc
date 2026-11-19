FILESEXTRAPATHS:prepend:sbp1 := "${THISDIR}/${BPN}:"

SRC_URI:append:sbp1 = " \
    file://blacklist.json \
    "

do_install:append:sbp1 () {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${BPN}/blacklist.json
}
