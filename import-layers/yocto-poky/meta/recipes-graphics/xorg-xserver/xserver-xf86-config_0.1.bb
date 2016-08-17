SUMMARY = "X.Org X server configuration file"
HOMEPAGE = "http://www.x.org"
SECTION = "x11/base"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
PR = "r33"

SRC_URI = "file://xorg.conf"

SRC_URI_append_libc-musl = "\
          file://10-preload-modules.conf \
"

S = "${WORKDIR}"

CONFFILES_${PN} = "${sysconfdir}/X11/xorg.conf"
CONFFILES_${PN}_append_libc-musl = " ${sysconfdir}/X11/xorg.conf.d/10-preload-modules.conf"

PACKAGE_ARCH = "${MACHINE_ARCH}"
ALLOW_EMPTY_${PN} = "1"

do_install () {
	if test -s ${WORKDIR}/xorg.conf; then
		install -d ${D}/${sysconfdir}/X11
		install -m 0644 ${WORKDIR}/xorg.conf ${D}/${sysconfdir}/X11/
	fi
}

do_install_append_libc-musl () {
	install -Dm 0644 ${WORKDIR}/10-preload-modules.conf ${D}/${sysconfdir}/X11/xorg.conf.d/10-preload-modules.conf
}
