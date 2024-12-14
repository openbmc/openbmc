SUMMARY = "Text::WrapI18N - Line wrapping module with support for multibyte, \
fullwidth, and combining characters and languages without whitespaces \
between words."
DESCRIPTION = "This module intends to be a better Text::Wrap module. This module is \
needed to support multibyte character encodings such as UTF-8, EUC-JP, \
EUC-KR, GB2312, and Big5. This module also supports characters with \
irregular widths, such as combining characters (which occupy zero columns \
on terminal, like diacritical marks in UTF-8) and fullwidth characters \
(which occupy two columns on terminal, like most of east Asian \
characters). Also, minimal handling of languages which doesn't use \
whitespaces between words (like Chinese and Japanese) is supported."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/KUBOTA/Text-WrapI18N-0.06"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;md5=080862e1e40cdcddef4393e137285858"

SRC_URI = "${CPAN_MIRROR}/authors/id/K/KU/KUBOTA/Text-WrapI18N-${PV}.tar.gz"

SRC_URI[sha256sum] = "4bd29a17f0c2c792d12c1005b3c276f2ab0fae39c00859ae1741d7941846a488"

S = "${WORKDIR}/Text-WrapI18N-${PV}"

DEPENDS = "libtext-charwidth-perl"

RDEPENDS:${PN} = "libtext-charwidth-perl"

inherit cpan

BBCLASSEXTEND = "native"
