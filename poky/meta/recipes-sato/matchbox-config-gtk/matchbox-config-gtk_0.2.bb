SUMMARY = "Matchbox GTK+ theme configuration application"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://appearance/mb-appearance.c;endline=25;md5=f49d7ae8b8634a94315410cd2e055bdf"

DEPENDS = "gconf gtk+3"
RDEPENDS:${PN} = "settings-daemon"

SRCREV = "7182e603357250952aa24d90f6d89345f93da7ce"
SRC_URI = "git://git.yoctoproject.org/${BPN};branch=master;protocol=https \
           file://no-handed.patch"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"
PV = "0.2+git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check

# The settings-daemon requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
