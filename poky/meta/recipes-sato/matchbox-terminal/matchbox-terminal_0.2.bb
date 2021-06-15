SUMMARY = "Lightweight GTK+ terminal application"
HOMEPAGE = "http://www.matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://main.c;endline=20;md5=96e39176d9e355639a0b8b1c7a840820"

DEPENDS = "gtk+3 vte"
SECTION = "x11/utils"

#SRCREV tagged 0.2
SRCREV = "161276d0f5d1be8187010fd0d9581a6feca70ea5"
SRC_URI = "git://git.yoctoproject.org/${BPN}"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
