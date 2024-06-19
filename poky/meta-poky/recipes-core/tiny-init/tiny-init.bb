SUMMARY = "Poky-tiny init"
DESCRIPTION = "Basic init system for poky-tiny"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r2"

RDEPENDS:${PN} = "busybox"

SRC_URI = "file://init \
	   file://rc.local.sample \
	  "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_configure() {
	:
}

do_compile() {
	:
}

do_install() {
	install -d ${D}${sysconfdir}
	install -m 0755 ${S}/init ${D}
	install -m 0755 ${S}/rc.local.sample ${D}${sysconfdir}
}

FILES:${PN} = "/init ${sysconfdir}/rc.local.sample"
RCONFLICTS:${PN} = "systemd"
