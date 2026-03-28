SUMMARY = "Delivery framework for general Input Method configuration"
DESCRIPTION = "IMSettings is a framework that delivers Input Method \
settings and applies the changes so they take effect \
immediately without any need to restart applications \
or the desktop. \
This package contains the core DBus services and some utilities."
HOMEPAGE = "http://code.google.com/p/imsettings/"
SECTION = "Applications/System"

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools gettext gtk-doc gobject-introspection features_check

DEPENDS = "autoconf-archive-native gtk+3 libnotify"

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "git://gitlab.com/tagoh/imsettings.git;protocol=https;branch=main;tag=${PV} \
           file://imsettings-gcc10.patch \
           file://0001-remove-man-page.patch \
          "
SRCREV = "7fadd84fcbfe6c321dbe1c97b50cbb70efe20155"


do_configure:prepend() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/ABOUT-NLS ${AUTOTOOLS_AUXDIR}/
}

EXTRA_OECONF = "--with-xinputsh=50-xinput.sh \
                --disable-static \
               "

CFLAGS:append:toolchain-clang = " -Wno-error=unused-function -Wno-error=single-bit-bitfield-constant-conversion"
PACKAGECONFIG ??= "xfce"
PACKAGECONFIG[xfce] = ",,xfconf"
PACKAGECONFIG[xim] = ",,libgxim"

RDEPENDS:${PN} += "bash"

FILES:${PN} += "${datadir}/dbus-1/*"
