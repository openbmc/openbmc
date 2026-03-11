DESPCRIPTION = "DBD::MariaDB is the Perl5 Database Interface driver for MariaDB and MySQL databases."
HOMEPAGE = "https://metacpan.org/dist/DBD-MariaDB"
LICENSE = "GPL-1.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d0a06964340e5c0cde88b7af611f755c"

DEPENDS = "libdbi-perl-native libdev-checklib-perl-native libmysqlclient"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PA/PALI/DBD-MariaDB-${PV}.tar.gz"
SRC_URI[sha256sum] = "f977a25b4116a0a95a7c8a894fd37097abe19af9a6a9ed4d800604ec17873fe4"

RDEPENDS:${PN} = "libdbi-perl"

S = "${UNPACKDIR}/DBD-MariaDB-${PV}"

inherit cpan

RDEPENDS:${PN}-ptest += " \
	libtest-warnings-perl \
	perl-module-test-more \
"
