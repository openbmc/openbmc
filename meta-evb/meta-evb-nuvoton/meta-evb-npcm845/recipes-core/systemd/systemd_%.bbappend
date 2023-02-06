FILESEXTRAPATHS:prepend:nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:nuvoton = " file://85-persistent-net.rules"

do_install:append:nuvoton() {
	install -m 0644 ${WORKDIR}/85-persistent-net.rules ${D}${sysconfdir}/udev/rules.d/
}
