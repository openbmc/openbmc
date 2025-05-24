SUMMARY = "Matchbox window manager core library"
DESCRIPTION = "Matchbox is an Open Source base environment for the X Window \
System running on non-desktop embedded platforms such as handhelds, set-top \
boxes, kiosks and anything else for which screen space, input mechanisms or \
system resources are limited."
SECTION = "x11/libs"
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.com/"

LICENSE = "LGPL-2.0-or-later & HPND"
LIC_FILES_CHKSUM = "file://COPYING;md5=87712c91ca9a2c2d475a0604c00f8f54 \
                    file://COPYING.HPND;md5=508defeea3622831e69c4827a31d6db0"

DEPENDS = "virtual/libx11 libxext"

SRCREV = "35cd78efa3120efc46497f55c04382be960d1864"
SRC_URI = "git://git.yoctoproject.org/${BPN};branch=master;protocol=https;tag=${PV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "jpeg png xft"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"
PACKAGECONFIG[xft] = "--enable-xft,--disable-xft,libxft"
