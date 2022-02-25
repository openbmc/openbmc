SUMMARY = "Skip tests when modules not available"
DESCRIPTION = "Skip test scripts if modules are not available. \
The requested modules will be loaded, and optionally have their versions \
checked. If the module is missing, the test script will be skipped. Modules \
that are found but fail to compile will exit with an error rather than skip."

HOMEPAGE = "https://metacpan.org/release/Test-Needs"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

CPAN_NAME = "Test-Needs"
CPAN_AUTHOR = "HAARG"

LIC_FILES_CHKSUM = "file://README;md5=3f3ccd21a0a48aa313db212cc3b1bc09;beginline=88;endline=89"

DEPENDS += "perl"

SRC_URI = "https://cpan.metacpan.org/authors/id/H/HA/${CPAN_AUTHOR}/${CPAN_NAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "5643cd323afb77d20363acbaf9b12bcc"
SRC_URI[sha256sum] = "571c21193ad16195df58b06b268798796a391b398c443271721d2cc0fb7c4ac3"

S = "${WORKDIR}/${CPAN_NAME}-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += "perl-module-test-more perl-module-ipc-open3 perl-module-lib perl-module-version"

BBCLASSEXTEND = "native"
