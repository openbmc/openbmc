SUMMARY = "ExtUtils::InstallPaths - Build.PL install path logic made easy"
DESCRIPTION = "This module tries to make install path resolution as easy \
as possible."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/ExtUtils::InstallPaths"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4109085ee02c4453f25d919938e72e96"

SRC_URI = "${CPAN_MIRROR}/authors/id/L/LE/LEONT/ExtUtils-InstallPaths-${PV}.tar.gz"
SRC_URI[sha256sum] = "7d64eb2dfa87ead010cdf55c8a1bdfde50b7b5852d7cb8cf2304f55bea2eb007"

S = "${UNPACKDIR}/ExtUtils-InstallPaths-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} = " \
    libextutils-config-perl \
    perl-module-bytes \
    perl-module-data-dumper \
    perl-module-extutils-makemaker \
    perl-module-file-temp \
    perl-module-test-more \
"

RDEPENDS:${PN}-ptest = " \
    ${PN} \
    perl-module-file-spec-functions \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
