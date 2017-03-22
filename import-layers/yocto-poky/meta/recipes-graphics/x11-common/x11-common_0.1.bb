SUMMARY = "Common X11 scripts and configuration files"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "x11"
PR = "r47"

inherit distro_features_check
# rdepends on xdypinfo xmodmap xinit
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "file://etc \
           file://gplv2-license.patch"

S = "${WORKDIR}"

do_install() {
	cp -R ${S}/etc ${D}${sysconfdir}
	chmod -R 755 ${D}${sysconfdir}
}

RDEPENDS_${PN} = "dbus-x11 xmodmap xdpyinfo xinput-calibrator formfactor"

