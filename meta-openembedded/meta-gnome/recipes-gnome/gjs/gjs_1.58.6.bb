SUMMARY = "Javascript bindings for GNOME"
LICENSE = "MIT & LGPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=beb29cf17fabe736f0639b09ee6e76fa \
    file://COPYING.LGPL;md5=3bf50002aefd002f49e7bb854063f7e7 \
"

GNOMEBASEBUILDCLASS = "autotools"

DEPENDS = "mozjs gtk+3"

inherit gnomebase gsettings gobject-introspection vala gettext features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "7f671b3fc3b149adce263495540e7f50"
SRC_URI[archive.sha256sum] = "1864bf22f4f5e4107832b4c54127cce8509d37ccc14abd25a6878c7ffed96640"
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
