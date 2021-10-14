FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://mctp"

PACKAGECONFIG:append:fp5280g2 = " astlpc-raw-kcs"

do_install:append() {
	install -m 0644 ${WORKDIR}/mctp ${D}${sysconfdir}/default/mctp
}
