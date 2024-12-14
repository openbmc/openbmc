DESCRIPTION = "DNSSEC extensions to Net::DNS"
HOMEPAGE = "http://www.net-dns.org/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=165;endline=192;md5=6ef523fa09e8c272675839e21de16bde"

SRC_URI = "${CPAN_MIRROR}/authors/id/W/WI/WILLEM/Net-DNS-SEC-${PV}.tar.gz"
SRC_URI[sha256sum] = "37a47d4def72d7338f3cc7cd807ec19bd9e2ae638ae656fa536cf0314801989e"

DEPENDS += "openssl"

UPSTREAM_CHECK_REGEX = "Net\-DNS\-SEC\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/Net-DNS-SEC-${PV}"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -lssl -L${STAGING_BASELIBDIR} -lcrypto'"

inherit cpan ptest-perl

RDEPENDS:${PN} = " \
    libnet-dns-perl \
    libcrypto \
    perl-module-dynaloader \
    perl-module-file-find \
    perl-module-file-spec \
    perl-module-io-file \
    perl-module-mime-base64 \
    perl-module-test-more \
"
