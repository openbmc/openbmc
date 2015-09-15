SUMMARY = "Stub implementation of gtk-doc"
DESCRIPTION = "Stub implementation of gtk-doc, as we don't want to build the API documentation"
SECTION = "x11/base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

PROVIDES = "gtk-doc gobject-introspection-stub"

SRCREV = "1dea266593edb766d6d898c79451ef193eb17cfa"
PV = "1.1+git${SRCPV}"

SRC_URI = "git://git.gnome.org/${BPN}"

S = "${WORKDIR}/git"

inherit autotools

FILES_${PN} += "${datadir}"

BBCLASSEXTEND = "native"
