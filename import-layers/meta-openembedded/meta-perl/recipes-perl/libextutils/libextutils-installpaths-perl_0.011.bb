SUMMARY = "ExtUtils::InstallPaths - Build.PL install path logic made easy"
DESCRIPTION = "This module tries to make install path resolution as easy \
as possible."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~leont/ExtUtils-InstallPaths/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b6fa54d873ce6bcf4809ea88bdf97769"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/ExtUtils-InstallPaths-${PV}.tar.gz"
SRC_URI[md5sum] = "9c75894c3c8c899ab6bfafc5eaa97999"
SRC_URI[sha256sum] = "7609fa048cdcf1451cad5b1d7d494f30e3d5bad0672d15404f1ea60e1df0067c"

S = "${WORKDIR}/ExtUtils-InstallPaths-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-extutils-makemaker \
                   perl-module-data-dumper \
                   perl-module-test-more \
                   perl-module-file-temp \
"

BBCLASSEXTEND = "native"
