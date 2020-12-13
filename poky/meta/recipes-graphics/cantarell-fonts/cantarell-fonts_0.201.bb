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
SRC_URI[archive.sha256sum] = "b61f64e5f6a48aa0abc7a53cdcbce60de81908ca36048a64730978fcd9ac9863"

EXTRA_OEMESON += "-Duseprebuilt=true -Dbuildappstream=false"

FILES_${PN} = "${datadir}/fonts ${datadir}/fontconfig"
