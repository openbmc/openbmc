SUMMARY = "A Perl DBI driver for SQLite"
DESCRIPTION = "DBD::SQLite is a Perl DBI driver for SQLite, that includes the entire \
thing in the distribution. So in order to get a fast transaction capable \
RDBMS working for your perl project you simply have to install this \
module, and nothing else. \
"
HOMEPAGE = "http://search.cpan.org/~ishigaki/DBD-SQLite/"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
DEPENDS += "libdbi-perl-native"
RDEPENDS_${PN} += "libdbi-perl \
                   sqlite3 \
                   perl-module-constant \
                   perl-module-locale \
                   perl-module-tie-hash \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1726e2117494ba3e13e1c3d93f795360"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/I/IS/ISHIGAKI/DBD-SQLite-${PV}.tar.gz \
           file://sqlite-perl-test.pl \
"

SRC_URI[md5sum] = "8f835ddacb9a4a92a52bbe2d24d18a8e"
SRC_URI[sha256sum] = "3929a6dbd8d71630f0cb57f85dcef9588cd7ac4c9fa12db79df77b9d3a4d7269"

UPSTREAM_CHECK_REGEX = "DBD\-SQLite\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/DBD-SQLite-${PV}"

inherit cpan

BBCLASSEXTEND = "native"

do_install_append() {
    if [ ${PERL_DBM_TEST} = "1" ]; then
        install -m 755 -D ${WORKDIR}/sqlite-perl-test.pl ${D}/${bindir}/sqlite-perl-test.pl
    fi
}

