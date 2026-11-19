FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI += " \
            file://blacklist.json \
           "

do_install:append() {
     install -d ${D}${datadir}/${BPN}
     install -m 0444 ${UNPACKDIR}/blacklist.json ${D}${datadir}/${BPN}
}
