SUMMARY = "Matchbox GTK+ theme configuration application"
HOMEPAGE = "http://matchbox-project.org"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://appearance/mb-appearance.c;endline=25;md5=ea92333cf8a6802639d62d874c114a28"

DEPENDS = "gconf gtk+3"
RDEPENDS_${PN} = "settings-daemon"

# SRCREV tagged 0.2
SRCREV = "ef2192ce98d9374ffdad5f78544c3f8f353c16aa"
SRC_URI = "git://git.yoctoproject.org/${BPN} \
           file://no-handed.patch"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

# The settings-daemon requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"
