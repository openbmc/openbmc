SUMMARY = "GNU poke is an extensible editor for structured binary data"
HOMEPAGE = "https://pokology.org"
DESCRIPTION = "GNU poke is an interactive, extensible editor for binary data. Not limited to editing basic entities such as bits and bytes, it provides a full-fledged procedural, interactive programming language designed to describe data structures and to operate on them."
SECTION = "console/utils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/poke/poke-${PV}.tar.gz \
          file://0001-build-do-not-check-for-Tcl-Tk-if-disable-gui-is-spec.patch \
          file://0002-jitter-jitter-config.in-avoit-host-poisoning-while-c.patch \
          file://0003-configure.ac-HELP2MAN-replace-by-true-when-cross-com.patch \
          "

DEPENDS = "flex-native bison-native bdwgc readline"

SRC_URI[sha256sum] = "f61cf8da5b64c01a1359373725aad1ca257f35c1c9269e4d50dd0664183ddf62"

# poke does not support using out-of-tree builds
inherit autotools-brokensep gettext pkgconfig

# The automatic m4 path detection gets confused, so force the right value from
# the poke bootstrap script.
acpaths = "-I ./m4"

EXTRA_OECONF = "--disable-gui \
                --disable-libnbd \
                --with-libreadline-prefix=${STAGING_INCDIR} \
                "

PACKAGECONFIG[mi] = "--enable-mi,--disable-mi,json-c"

FILES:${PN} += "${datadir}/emacs/site-lisp"
