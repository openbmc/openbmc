DESCRIPTION="Anthy is a system for Japanese input method. It converts Hiragana text to Kana Kanji mixed text."
AUTHOR = "Anthy Developers <anthy-dev@lists.sourceforge.jp>"
HOMEPAGE = "http://anthy.sourceforge.jp"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=11f384074d8e93e263b5664ef08a411a"

SRC_URI = "http://osdn.dl.sourceforge.jp/anthy/37536/anthy-9100h.tar.gz \
           file://not_build_elc.patch \
           file://2ch_t.patch \
          "

SRC_URI_append_class-target = "file://target-helpers.patch"
SRC_URI_append_class-native = "file://native-helpers.patch"

SRC_URI[md5sum] = "1f558ff7ed296787b55bb1c6cf131108"
SRC_URI[sha256sum] = "d256f075f018b4a3cb0d165ed6151fda4ba7db1621727e0eb54569b6e2275547"

DEPENDS_class-target = "anthy-native"
RDEPENDS_${PN}_class-target = "libanthy0"

inherit autotools pkgconfig

PACKAGES += "${PN}-el libanthy0 libanthy-dev"

FILES_${PN}-dbg += "${libdir}/.debug"
FILES_libanthy0 = "${libdir}/libanthy.so.*  \
		   ${libdir}/libanthydic.so.*   \
		   ${libdir}/libanthyinput.so.*"

FILES_libanthy-dev = "${libdir}/libanthy*.la \
		      ${libdir}/libanthy*.a \
		      ${libdir}/libanthy*.so \
		      ${includedir}/anthy   \
		      ${libdir}/pkgconfig/anthy.pc"

FILES_${PN}-el = "${datadir}/emacs/*"
FILES_${PN} = "${datadir}/* \
	       ${bindir}/* \
	       ${sysconfdir}/anthy-conf"

BBCLASSEXTEND = "native"
