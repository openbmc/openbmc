SUMMARY = "Plugins for the GStreamer multimedia framework"
HOMEPAGE = "http://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"
DEPENDS = "gstreamer"

# gobject-introspection to get --disable-introspection into UNKNOWN_CONFIGURE_WHITELIST
inherit autotools pkgconfig gobject-introspection

SRC_URI = "http://gstreamer.freedesktop.org/src/${BPN}/${BPN}-${PV}.tar.bz2"

GSTREAMER_DEBUG ?= "--disable-debug"
EXTRA_OECONF = "--disable-valgrind ${GSTREAMER_DEBUG} --disable-examples --disable-introspection"

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

LIBV = "0.10"
require recipes-multimedia/gstreamer/gst-plugins-package.inc

PACKAGES_DYNAMIC += "^${PN}-.*"

# apply gstreamer hack after Makefile.in.in in source is replaced by our version from
# ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in, but before configure is executed
# http://lists.linuxtogo.org/pipermail/openembedded-core/2012-November/032233.html
oe_runconf_prepend() {
    if [ -e ${S}/po/Makefile.in.in ]; then
        sed -i -e "1a\\" -e 'GETTEXT_PACKAGE = @GETTEXT_PACKAGE@' ${S}/po/Makefile.in.in
    fi
}
