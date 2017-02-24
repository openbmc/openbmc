SUMMARY = "Convert LinuxDoc SGML source into other formats"
HOMEPAGE = "http://packages.debian.org/linuxdoc-tools"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=077ef64ec3ac257fb0d786531cf26931"

DEPENDS = "groff-native openjade-native"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/l/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://disable_sgml2rtf.patch \
           file://disable_txt_doc.patch \
           file://disable_tex_doc.patch \
           file://disable_dvips_doc.patch"

SRC_URI[md5sum] = "1d13d500918a7a145b0edc2f16f61dd1"
SRC_URI[sha256sum] = "7103facee18a2ea97186ca459d743d22f7f89ad4b5cd1dfd1c34f83d6bfd4101"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/l/linuxdoc-tools/"
inherit autotools-brokensep native

do_configure () {
	oe_runconf
}

do_install() {
	oe_runmake 'DESTDIR=${D}' 'TMPDIR=${T}' install
}
