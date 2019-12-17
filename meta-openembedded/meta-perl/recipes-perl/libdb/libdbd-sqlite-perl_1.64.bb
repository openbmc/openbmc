SUMMARY = "A Perl DBI driver for SQLite"
DESCRIPTION = "DBD::SQLite is a Perl DBI driver for SQLite, that includes the entire \
thing in the distribution. So in order to get a fast transaction capable \
RDBMS working for your perl project you simply have to install this \
module, and nothing else. \
"
HOMEPAGE = "https://metacpan.org/release/DBD-SQLite"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1726e2117494ba3e13e1c3d93f795360"

SRC_URI = "${CPAN_MIRROR}/authors/id/I/IS/ISHIGAKI/DBD-SQLite-${PV}.tar.gz \
           file://sqlite-perl-test.pl \
"

SRC_URI[md5sum] = "10796495b52927eb2e1df34c86924027"
SRC_URI[sha256sum] = "f4ae8f7b50842305566aadd90f7bfd12a9e32b6c603a9b1c1529e73eb82aff01"

UPSTREAM_CHECK_REGEX = "DBD\-SQLite\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/DBD-SQLite-${PV}"

DEPENDS += "libdbi-perl-native"

inherit cpan ptest-perl

RDEPENDS_${PN} += "libdbi-perl \
		   sqlite3 \
		   perl-module-constant \
		   perl-module-locale \
		   perl-module-tie-hash \
"

do_install_append() {
    if [ ${PERL_DBM_TEST} = "1" ]; then
        install -m 755 -D ${WORKDIR}/sqlite-perl-test.pl ${D}/${bindir}/sqlite-perl-test.pl
    fi
}

do_install_ptest() {
	cp -r ${B}/MANIFEST ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
}

RDEPENDS_${PN}-ptest += " \
    libtest-nowarnings-perl \
    perl-module-lib \
    perl-module-encode \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-findbin \
    perl-module-test-more \
    "

BBCLASSEXTEND = "native"
