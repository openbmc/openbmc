SUMMARY = "Matchbox Window Manager Desktop"
DESCRIPTION = "A lightweight windows manager for embedded systems. It uses the desktop background to provide an application launcher and allows modules to be loaded for additional functionality."
HOMEPAGE = "http://matchbox-project.org/"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/desktop.c;endline=20;md5=2e488557570c7dee53bfd0567e4273a9 \
                    file://src/main.c;endline=19;md5=5d2234b35efa927ab3ae36ebac52ba59"

DEPENDS = "gtk+3 startup-notification dbus"
SECTION = "x11/wm"

SRCREV = "0fd6a0c3f3b7bbf4f4b46190d71f7aef35d6bbfd"
SRC_URI = "git://git.yoctoproject.org/${BPN}-2;branch=master;protocol=https \
           file://vfolders/ \
           "

EXTRA_OECONF = "--enable-startup-notification --with-dbus"

S = "${WORKDIR}/git"

inherit autotools pkgconfig features_check

# The startup-notification requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

do_install:append() {
    install -d ${D}${datadir}/matchbox/vfolders/
    install -m 0644 ${UNPACKDIR}/vfolders/* ${D}${datadir}/matchbox/vfolders/
}

FILES:${PN} += "${datadir}/matchbox/vfolders/"
