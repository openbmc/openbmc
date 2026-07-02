SUMMARY = "Zile is lossy Emacs"
HOMEPAGE = "http://zile.sourceforge.net/"
DEPENDS = "ncurses bdwgc glib-2.0 libgee autoconf-archive-native"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/zile/${BP}.tar.gz \
           file://remove-help2man.patch \
"

SRC_URI[sha256sum] = "d5d44b85cb490643d0707e1a2186f3a32998c2f6eabaa9481479b65caeee57c0"

inherit autotools pkgconfig vala

# zile's Vala build writes generated sources (e.g. src/dummy.vala) back into the
# source tree, so build in-tree. The shipped maintainer GNUmakefile aborts on
# "make clean" before configure, so skip the preconfigure clean.
B = "${S}"
CLEANBROKEN = "1"

# zile ships a pre-generated aclocal.m4 with the pkg-config macros but does not
# carry pkg.m4 in its m4/ macro dir. With newer autoconf/automake, autoreconf
# regenerates aclocal.m4 and can no longer find PKG_CHECK_EXISTS, failing with
# "undefined or overquoted macro: PKG_CHECK_EXISTS". Stage pkg.m4 from the
# native pkg-config so aclocal picks it up (m4/ is on its -I search path).
do_configure:prepend() {
    install -d ${S}/m4
    install -m 0644 ${STAGING_DATADIR_NATIVE}/aclocal/pkg.m4 ${S}/m4/
}

do_install:append() {
    rm -rf ${D}${libdir}/charset.alias
    rmdir --ignore-fail-on-non-empty ${D}${libdir} || true
}

# zile 2.6.x dropped the --enable-acl/--disable-acl configure switches
# (file attribute handling moved to GLib/GIO), so no acl PACKAGECONFIG.
