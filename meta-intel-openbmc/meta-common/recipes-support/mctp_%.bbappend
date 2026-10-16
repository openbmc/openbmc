FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = "file://mctpd.conf"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/mctpd.conf ${D}${sysconfdir}/mctpd.conf
}
