SUMMARY = "X.Org X server configuration file"
HOMEPAGE = "http://www.x.org"
SECTION = "x11/base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://xorg.conf"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

CONFFILES:${PN} = "${sysconfdir}/X11/xorg.conf"

PACKAGE_ARCH = "${MACHINE_ARCH}"
ALLOW_EMPTY:${PN} = "1"

do_install () {
	if test -s ${S}/xorg.conf; then
		install -d ${D}/${sysconfdir}/X11
		install -m 0644 ${S}/xorg.conf ${D}/${sysconfdir}/X11/
	fi
}
