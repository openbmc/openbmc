FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "\
    file://blacklist.json \
    "

do_install:append() {
    install -m 0644 -D ${UNPACKDIR}/blacklist.json ${D}${datadir}/${PN}/blacklist.json
}
