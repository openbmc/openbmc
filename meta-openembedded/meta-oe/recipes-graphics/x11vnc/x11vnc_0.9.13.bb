SUMMARY = "Exports your X session on-the-fly via VNC"
HOMEPAGE = "http://www.karlrunge.com/x11vnc/"

SECTION = "x11/utils"
AUTHOR = "Karl Runge"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f \
                    file://x11vnc/x11vnc.h;endline=33;md5=6f95dc6535467d7ee1563fd434fb372e"

SRC_URI = "${SOURCEFORGE_MIRROR}/libvncserver/x11vnc/${PV}/x11vnc-${PV}.tar.gz\
           file://starting-fix.patch \
           file://endian-fix.patch \
           file://remove-redundant-RPATH.patch \
"

SRC_URI[md5sum] = "a372ec4fe8211221547b1c108cf56e4c"
SRC_URI[sha256sum] = "f6829f2e629667a5284de62b080b13126a0736499fe47cdb447aedb07a59f13b"

DEPENDS = "openssl virtual/libx11 libxext jpeg zlib libxfixes libxrandr libxdamage libxtst libtasn1 p11-kit"

inherit autotools-brokensep distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} libvncserver"
PACKAGECONFIG[avahi] = "--with-avahi,--without-avahi,avahi"
PACKAGECONFIG[xinerama] = "--with-xinerama,--without-xinerama,libxinerama"
PACKAGECONFIG[libvncserver] = "--with-system-libvncserver,--without-system-libvncserver,libvncserver"

do_prepare_sources () {
    # Remove old libtool macros from acinclude.m4
    sed -i -e '/^# libtool.m4/q' ${S}/acinclude.m4
}
do_patch[postfuncs] += "do_prepare_sources"
