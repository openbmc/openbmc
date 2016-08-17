SUMMARY = "Full-featured IRC chat client with scripting support"
LICENSE = "GPLv2+"
HOMEPAGE = "http://www.xchat.org"
SECTION = "x11/network"

DEPENDS = "libgcrypt zlib gtk+ libsexy"
DEPENDS += "gdk-pixbuf-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

PR = "r2"

SRC_URI = "http://xchat.org/files/source/2.8/xchat-${PV}.tar.bz2 \
    file://glib-2.32.patch \
    file://automake-foreign.patch \
    file://fix-includes-for-separate-build.patch \
"

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= "dbus"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus-glib"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[python] = "--enable-python,--disable-python,python"

EXTRA_OECONF = "\
    --enable-maintainer-mode \
    --disable-perl \
    --disable-tcl \
"

RDEPENDS_${PN} += "dbus"
FILES_${PN} += "${datadir}/dbus-1"
FILES_${PN}-dbg += "${libdir}/xchat/plugins/.debug"

SRC_URI[md5sum] = "6775c44f38e84d06c06c336b32c4a452"
SRC_URI[sha256sum] = "0d6d69437b5e1e45f3e66270fe369344943de8a1190e498fafa5296315a27db0"
