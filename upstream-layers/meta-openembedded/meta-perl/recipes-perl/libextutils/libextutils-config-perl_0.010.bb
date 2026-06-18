SUMMARY = "ExtUtils::Config - A wrapper for perl's configuration"
DESCRIPTION = "ExtUtils::Config is an abstraction around the %Config hash."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/dist/ExtUtils-Config"

LICENSE = "Artistic-1.0-Perl | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ddb4d526cd95b017e23486893490883e"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/ExtUtils-Config-${PV}.tar.gz"
SRC_URI[sha256sum] = "82e7e4e90cbe380e152f5de6e3e403746982d502dd30197a123652e46610c66d"

S = "${UNPACKDIR}/ExtUtils-Config-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} = " perl-module-extutils-makemaker \
                   perl-module-data-dumper \
		   perl-module-ipc-open3 \
                   perl-module-test-more \
                   perl-module-file-temp \
"

BBCLASSEXTEND = "native"
