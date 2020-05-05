SUMMARY = "Javascript bindings for GNOME"
LICENSE = "MIT & LGPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=beb29cf17fabe736f0639b09ee6e76fa \
    file://COPYING.LGPL;md5=3bf50002aefd002f49e7bb854063f7e7 \
"

GNOMEBASEBUILDCLASS = "autotools"

DEPENDS = "mozjs gtk+3"

inherit gnomebase gsettings gobject-introspection vala gettext features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "c8c530d4593133f496ae69098273a675"
SRC_URI[archive.sha256sum] = "2014509019ea4e7a8ab8e0dd0c472bbcf0ad3067928af88c29f5e2879c58c7a9"
SRC_URI += "file://0001-Disable-tests-on-host.patch"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " --enable-introspection --disable-introspection"

EXTRA_OECONF = " \
    --without-dbus-tests \
    --disable-installed-tests \
"

do_configure_prepend() {
    # make configure find gobject-introspection test code. Although we set
    # --disable-installed-tests gjs builds them
    sed -i 's|:$GI_DATADIR|:${STAGING_DIR_NATIVE}$GI_DATADIR|g' ${S}/configure.ac
}

FILES_${PN} += "${datadir}/gjs-1.0/lsan"

PACKAGES =+ "${PN}-valgrind"
FILES_${PN}-valgrind = "${datadir}/gjs-1.0/valgrind"
RSEPENDS_${PN}-valgrind += "valgrind"
