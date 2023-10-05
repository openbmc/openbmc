FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://mctp"

do_install:append() {
	install -m 0644 ${WORKDIR}/mctp ${D}${sysconfdir}/default/mctp
}
