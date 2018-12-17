SUMMARY = "Weston, a Wayland compositor, configuration files"
HOMEPAGE = "http://wayland.freedesktop.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"

CONFFILES_${PN} = "${sysconfdir}/xdg/weston/weston.ini"

FILES_${PN} = "${sysconfdir}/xdg/weston/weston.ini"

PACKAGES = "${PN}"

do_configure[noexec] = '1'
do_compile[noexec] = '1'

do_install() {
	:
}

do_install_qemux86() {
	mkdir -p ${D}/${sysconfdir}/xdg/weston
	cat << EOF > ${D}/${sysconfdir}/xdg/weston/weston.ini
[core]
backend=fbdev-backend.so
EOF
}

do_install_qemux86-64() {
	mkdir -p ${D}/${sysconfdir}/xdg/weston
	cat << EOF > ${D}/${sysconfdir}/xdg/weston/weston.ini
[core]
backend=fbdev-backend.so
EOF
}
