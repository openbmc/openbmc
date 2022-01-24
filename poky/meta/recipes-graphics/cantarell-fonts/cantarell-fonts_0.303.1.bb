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
SRC_URI[archive.sha256sum] = "f9463a0659c63e57e381fdd753cf1929225395c5b49135989424761830530411"

EXTRA_OEMESON += "-Duseprebuilt=true -Dbuildappstream=false"

FILES:${PN} = "${datadir}/fonts ${datadir}/fontconfig"
