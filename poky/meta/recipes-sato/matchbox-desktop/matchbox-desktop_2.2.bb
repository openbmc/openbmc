SUMMARY = "Matchbox Window Manager Desktop"
DESCRIPTION = "A lightweight windows manager for embedded systems. It uses the desktop background to provide an application launcher and allows modules to be loaded for additional functionality."
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/desktop.c;endline=20;md5=36c9bf295e6007f3423095f405af5a2d \
                    file://src/main.c;endline=19;md5=2044244f97a195c25b7dc602ac7e9a00"

DEPENDS = "gtk+3 startup-notification dbus"
SECTION = "x11/wm"

# SRCREV tagged 2.2
SRCREV = "6bc67d09da4147e5552fe30011a05a2c59d2f777"
SRC_URI = "git://git.yoctoproject.org/${BPN}-2;branch=master \
           file://vfolders/ \
           "

EXTRA_OECONF = "--enable-startup-notification --with-dbus"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

do_install:append() {
    install -d ${D}${datadir}/matchbox/vfolders/
    install -m 0644 ${WORKDIR}/vfolders/* ${D}${datadir}/matchbox/vfolders/
}

FILES:${PN} += "${datadir}/matchbox/vfolders/"
