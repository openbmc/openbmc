SUMMARY = "Transparent X11 cursor theme for touchscreens"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/base"
PR = "r4"

SRC_URI = "http://downloads.yoctoproject.org/releases/matchbox/utils/xcursor-transparent-theme-${PV}.tar.gz \
	   file://use-relative-symlinks.patch \
	   file://fix_watch_cursor.patch"

SRC_URI[md5sum] = "7b0c623049d4aab20600d6473f8aab23"
SRC_URI[sha256sum] = "b26adf2d503d01299718390ae39dab4691a67220de09423be0364e9a060bf7e4"
FILES_${PN} = "${datadir}/icons/xcursor-transparent/cursors/*"

inherit autotools allarch
