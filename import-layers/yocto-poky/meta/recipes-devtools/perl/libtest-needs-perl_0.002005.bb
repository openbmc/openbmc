SUMMARY = "Skip tests when modules not available"
DESCRIPTION = "Skip test scripts if modules are not available. \
The requested modules will be loaded, and optionally have their versions \
checked. If the module is missing, the test script will be skipped. Modules \
that are found but fail to compile will exit with an error rather than skip."

HOMEPAGE = "https://metacpan.org/release/Test-Needs"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

CPAN_NAME = "Test-Needs"
CPAN_AUTHOR = "HAARG"

LIC_FILES_CHKSUM = "file://README;md5=3f3ccd21a0a48aa313db212cc3b1bc09;beginline=81;endline=82"

DEPENDS += "perl"

SRC_URI = "http://www.cpan.org/authors/id/H/HA/${CPAN_AUTHOR}/${CPAN_NAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "356634a56c99282e8059f290f5d534c8"
SRC_URI[sha256sum] = "5a4f33983586edacdbe00a3b429a9834190140190dab28d0f873c394eb7df399"

S = "${WORKDIR}/${CPAN_NAME}-${PV}"

inherit cpan ptest-perl

RDEPENDS_${PN}-ptest += "perl-module-test-more"

BBCLASSEXTEND = "native"
