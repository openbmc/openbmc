DESCRIPTION = "C utility functions for OpenWrt"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "\
    file://avl.c;endline=39;md5=00810155fed3d604816ec5814523d60a \
    file://avl-cmp.c;endline=15;md5=1603e6094b432a5f3f320877a06f41b5 \
    file://base64.c;endline=61;md5=51fdff010d45b0086ac0a6e035693dc0 \
    file://blobmsg.c;endline=15;md5=7ed64c1570e8c9b46c4fc6fbd16c489e \
    file://list.h;endline=28;md5=2d5f5475fbd0f08741354c5a99c2e983 \
    file://md5.h;endline=39;md5=048bf9f68963c207a0c2b3a94c9d2aaa \
    file://md5.c;endline=51;md5=0a448eea0bcbc89e3c7e6608f2d119a0 \
    file://usock.h;endline=18;md5=f0dfdc8de858e66d66d74036611bba14 \
"

SRC_URI = "\
    git://git.openwrt.org/project/libubox.git \
    file://0001-version-libraries.patch \
    file://fix-libdir.patch \
"

SRCREV = "155bf39896f126b1ba121b816922a88dc34c31e3"
PV = "1.0.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS = "json-c"

EXTRA_OECMAKE = "-DBUILD_EXAMPLES=OFF -DBUILD_LUA=OFF"
