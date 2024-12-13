DESCRIPTION="Anthy is a system for Japanese input method. It converts Hiragana text to Kana Kanji mixed text."
HOMEPAGE = "http://anthy.sourceforge.jp"

LICENSE = "LGPL-2.1-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=11f384074d8e93e263b5664ef08a411a \
    file://alt-cannadic/COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b \
"

SRC_URI = "http://osdn.dl.sourceforge.jp/anthy/37536/anthy-9100h.tar.gz \
    file://not_build_elc.patch \
    file://2ch_t.patch \
"

SRC_URI:append:class-target = " file://target-helpers.patch"
SRC_URI:append:class-native = " file://native-helpers.patch"

SRC_URI[md5sum] = "1f558ff7ed296787b55bb1c6cf131108"
SRC_URI[sha256sum] = "d256f075f018b4a3cb0d165ed6151fda4ba7db1621727e0eb54569b6e2275547"

UPSTREAM_CHECK_URI = "https://osdn.net/projects/anthy/releases/"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>(\d+)+(\w*))"

DEPENDS:class-target = "anthy-native"
RDEPENDS:${PN}:class-target = "libanthy0"

inherit autotools pkgconfig

PACKAGES += "${PN}-el libanthy0 libanthy-dev"

FILES:${PN}-dbg += "${libdir}/.debug"
FILES:libanthy0 = "${libdir}/libanthy.so.*  \
    ${libdir}/libanthydic.so.* \
    ${libdir}/libanthyinput.so.* \
"

FILES:libanthy-dev = "${libdir}/libanthy*.la \
    ${libdir}/libanthy*.a \
    ${libdir}/libanthy*.so \
    ${includedir}/anthy \
    ${libdir}/pkgconfig/anthy.pc \
"

FILES:${PN}-el = "${datadir}/emacs/*"
FILES:${PN} = "${datadir}/* \
    ${bindir}/* \
    ${sysconfdir}/anthy-conf \
"

BBCLASSEXTEND = "native"
