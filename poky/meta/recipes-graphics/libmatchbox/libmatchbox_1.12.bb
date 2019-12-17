SUMMARY = "Matchbox window manager core library"
DESCRIPTION = "Matchbox is an Open Source base environment for the X Window \
System running on non-desktop embedded platforms such as handhelds, set-top \
boxes, kiosks and anything else for which screen space, input mechanisms or \
system resources are limited."
SECTION = "x11/libs"
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.com/"

LICENSE = "LGPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 \
                    file://COPYING.MIT;md5=f45ed9332b4f50a35adf2065adde4ca7 \
                    file://libmb/mbexp.c;endline=20;md5=28c0aef3b23e308464f5dae6a11b0d2f \
                    file://libmb/xsettings-client.c;endline=20;md5=4b106a387602db8d91a50d5cdfd65031"

DEPENDS = "virtual/libx11 libxext"

#SRCREV for 1.12
SRCREV = "e846ee434f8e23d9db38af13c523f791495e0e87"
SRC_URI = "git://git.yoctoproject.org/${BPN}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "jpeg png xft"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"
PACKAGECONFIG[xft] = "--enable-xft,--disable-xft,libxft"
