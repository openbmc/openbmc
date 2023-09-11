SUMMARY = "Lightweight GTK+ terminal application"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://main.c;endline=20;md5=7d9d802a36298b5c74440a880e2f4817"

DEPENDS = "gtk+3 vte"
SECTION = "x11/utils"

SRCREV = "99e6eb7db1b5fef110973d96194eec992a2515a2"
SRC_URI = "git://git.yoctoproject.org/${BPN};branch=master;protocol=https"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"
PV = "0.2+git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
