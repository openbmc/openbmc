SUMMARY = "ExtUtils::InstallPaths - Build.PL install path logic made easy"
DESCRIPTION = "This module tries to make install path resolution as easy \
as possible."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~leont/ExtUtils-InstallPaths/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b6fa54d873ce6bcf4809ea88bdf97769"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/ExtUtils-InstallPaths-${PV}.tar.gz"
SRC_URI[md5sum] = "9a8d66aab1ffec98ea260faf03ac612b"
SRC_URI[sha256sum] = "84735e3037bab1fdffa3c2508567ad412a785c91599db3c12593a50a1dd434ed"

S = "${WORKDIR}/ExtUtils-InstallPaths-${PV}"

inherit cpan ptest-perl

RDEPENDS_${PN} = " \
    libextutils-config-perl \
    perl-module-bytes \
    perl-module-data-dumper \
    perl-module-extutils-makemaker \
    perl-module-file-temp \
    perl-module-test-more \
"

RDEPENDS_${PN}-ptest = " \
    ${PN} \
    perl-module-file-spec-functions \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
