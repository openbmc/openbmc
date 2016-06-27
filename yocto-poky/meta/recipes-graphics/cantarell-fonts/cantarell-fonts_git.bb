SUMMARY = "Cantarell, a Humanist sans-serif font family"

DESCRIPTION = "The Cantarell font typeface is designed as a \
               contemporary Humanist sans serif, and was developed for \
               on-screen reading; in particular, reading web pages on an \
               HTC Dream mobile phone."

HOMEPAGE = "https://git.gnome.org/browse/cantarell-fonts/"
SECTION = "fonts"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=df91e3ffcab8cfb972a66bf11255188d"

PV = "0.0.21+git${SRCPV}"

SRCREV = "611c169df5287c059d63b0c8f0570647c75c9189"
SRC_URI = "git://git.gnome.org/cantarell-fonts;protocol=git;branch=master"

S = "${WORKDIR}/git"

inherit autotools allarch fontcache

PACKAGECONFIG ??= ""
PACKAGECONFIG[fontforge] = "--enable-source-rebuild=yes,--enable-source-rebuild=no,fontforge-native"
FILES_${PN} = "${datadir}/fonts ${datadir}/fontconfig"
