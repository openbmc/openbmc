SUMMARY = "Lightweight GTK+ terminal application"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://main.c;endline=20;md5=96e39176d9e355639a0b8b1c7a840820"

DEPENDS = "gtk+3 vte"
SECTION = "x11/utils"

#SRCREV tagged 0.1
SRCREV = "3ad357db2302760b8a8817b5f4478dd87479442f"
SRC_URI = "git://git.yoctoproject.org/${BPN}"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

inherit autotools pkgconfig distro_features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
