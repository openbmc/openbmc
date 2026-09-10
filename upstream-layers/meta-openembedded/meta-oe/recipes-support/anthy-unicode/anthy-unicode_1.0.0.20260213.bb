DESCRIPTION = "Anthy Unicode is a maintained fork of the Anthy Japanese input \
method. It converts Hiragana text to Kana Kanji mixed text and works in UTF-8."
HOMEPAGE = "https://github.com/fujiwarat/anthy-unicode"

LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f37c94ff7ea73f8ed6ce61216611051 \
    file://alt-cannadic/COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b \
"

SRC_URI = "https://github.com/fujiwarat/anthy-unicode/releases/download/${PV}/anthy-unicode-${PV}.tar.gz \
    file://not_build_elc.patch \
"

SRC_URI:append:class-target = " file://target-helpers.patch"
SRC_URI:append:class-native = " file://native-helpers.patch"

SRC_URI[sha256sum] = "666e91b92c76eb5bee7ee88dca1478eb55122c2af2641f055d4e51cf9d904860"

UPSTREAM_CHECK_URI = "https://github.com/fujiwarat/anthy-unicode/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/(?P<pver>\d+(\.\d+)+)"

DEPENDS:class-target = "anthy-unicode-native"
RDEPENDS:${PN}:class-target = "libanthy-unicode0"

inherit autotools pkgconfig

PACKAGES += "${PN}-el libanthy-unicode0"

FILES:${PN}-dbg += "${libdir}/.debug"
FILES:libanthy-unicode0 = "${libdir}/libanthy-unicode.so.* \
    ${libdir}/libanthydic-unicode.so.* \
    ${libdir}/libanthyinput-unicode.so.* \
"

FILES:${PN}-el = "${datadir}/emacs/*"
FILES:${PN} = "${datadir}/* \
    ${bindir}/* \
    ${sysconfdir}/anthy-unicode.conf \
"

BBCLASSEXTEND = "native"
