LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
DESCRIPTION = "Runtime libraries for parsing and creating MIME mail"
SECTION = "libs"
DEPENDS = "glib-2.0 zlib"

inherit gnomebase autotools gobject-introspection

SRC_URI += "file://iconv-detect.h \
            file://nodolt.patch"

SRC_URI[archive.md5sum] = "69ae21a0b1df966a7d39a9431856ac81"
SRC_URI[archive.sha256sum] = "6a0875eeb552ab447dd54853a68ced62217d863631048737dd97eaa2713e7311"

EXTRA_OECONF_remove = "--disable-schemas-install"

export ac_cv_have_iconv_detect_h="yes"
do_configure_append = "cp ${WORKDIR}/iconv-detect.h ${S}"

# we do not need GNOME 1 gnome-config support
do_install_append () {
    rm -f ${D}${libdir}/gmimeConf.sh
}
