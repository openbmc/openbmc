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

SRC_URI[sha256sum] = "923ffdc78fcba96609753e4bae26b0ba0186893de4a63cd5236e012c7c90e208"

S = "${WORKDIR}/${CPAN_NAME}-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += "perl-module-test-more perl-module-ipc-open3 perl-module-lib perl-module-version"

BBCLASSEXTEND = "native"
