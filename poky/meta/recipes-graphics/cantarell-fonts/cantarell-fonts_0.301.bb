SUMMARY = "Cantarell, a Humanist sans-serif font family"

DESCRIPTION = "The Cantarell font typeface is designed as a \
               contemporary Humanist sans serif, and was developed for \
               on-screen reading; in particular, reading web pages on an \
               HTC Dream mobile phone."

HOMEPAGE = "https://gitlab.gnome.org/GNOME/cantarell-fonts/"
SECTION = "fonts"
LICENSE = "OFL-1.1 & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb1ef92b6909969a472a6ea3c4e99cb7"

inherit gnomebase meson allarch fontcache pkgconfig
SRC_URI[archive.sha256sum] = "3d35db0ac03f9e6b0d5a53577591b714238985f4cfc31a0aa17f26cd74675e83"

EXTRA_OEMESON += "-Duseprebuilt=true -Dbuildappstream=false"

FILES_${PN} = "${datadir}/fonts ${datadir}/fontconfig"
