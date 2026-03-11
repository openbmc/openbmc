DESCRIPTION = "C utility functions for OpenWrt"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
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
    git://git.openwrt.org/project/libubox.git;branch=master \
    file://0001-cmake-Set-library-version.patch \
    file://0002-cmake-fix-the-CMAKE_INSTALL_LIBDIR.patch \
    file://0003-cmake-Don-t-include-lua-and-examples-directories-if-.patch \
"

SRCREV = "b7acc8e6fd5e13611ad90a593e98f9589af4009a"
PV = "1.0.1+git"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"


inherit cmake pkgconfig

DEPENDS = "json-c"

EXTRA_OECMAKE = "-DBUILD_EXAMPLES=OFF -DBUILD_LUA=OFF"
