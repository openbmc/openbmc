SUMMARY = "Subsurface is an open source dive log program"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libdivecomputer gtk+ libxml2 glib-2.0 gconf"

PNBLACKLIST[subsurface] ?= "Needs to be upgraded for compatibility with new libdivecomputer"

inherit gtk-icon-cache cmake

inherit gitpkgv
PKGV = "${GITPKGVTAG}"
PV = "4.2"

SRCREV = "f61ee20ba356ecfc4c5b247f548f52d588179c94"
SRC_URI = "git://subsurface.hohndel.org/subsurface.git"
S = "${WORKDIR}/git"

#FILES_${PN} += "${datadir}/icons/hicolor/scalable/apps/subsurface.svg"
RRECOMMENDS_${PN}_append_libc-glibc = " glibc-gconv-iso8859-15"

