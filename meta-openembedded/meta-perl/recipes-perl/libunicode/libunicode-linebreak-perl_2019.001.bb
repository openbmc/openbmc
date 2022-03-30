SUMMARY = "Unicode::LineBreak - UAX #14 Unicode Line Breaking Algorithm."
DESCRIPTION = "Unicode::LineBreak performs Line Breaking Algorithm described in Unicode \
Standard Annex #14 [UAX #14]. East_Asian_Width informative property \
defined by Annex #11 [UAX #11] will be concerned to determine breaking \
positions."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/Unicode-LineBreak"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=5;endline=9;md5=b5e8b1099b86b86fbc315b50484231ab"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEZUMI/Unicode-LineBreak-${PV}.tar.gz"

SRC_URI[md5sum] = "003d6da7a13700e069afed9238c864b9"
SRC_URI[sha256sum] = "486762e4cacddcc77b13989f979a029f84630b8175e7fef17989e157d4b6318a"

S = "${WORKDIR}/Unicode-LineBreak-${PV}"

DEPENDS = "libsombok3 libmime-charset-perl"

inherit cpan ptest-perl

do_install_ptest() {
    cp -r ${B}/test-data ${D}${PTEST_PATH}
    chown -R root:root ${D}${PTEST_PATH}
}

RDEPENDS:${PN} += " \
    libmime-charset-perl \
    libsombok3 \
    perl-module-carp \
    perl-module-constant \
    perl-module-encode \
    perl-module-exporter \
    perl-module-overload \
    perl-module-strict \
    perl-module-vars \
    perl-module-warnings \
    perl-module-xsloader \
"

RDEPENDS:${PN}-ptest += " \
    perl-module-findbin \
    perl-module-lib \
    perl-module-strict \
    perl-module-test-more \
"

RPROVIDES:${PN} += " \
    libtext-linefold-perl \
    libunicode-gcstring-perl \
"

BBCLASSEXTEND = "native"
