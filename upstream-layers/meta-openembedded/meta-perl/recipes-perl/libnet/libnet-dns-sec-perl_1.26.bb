DESCRIPTION = "DNSSEC extensions to Net::DNS"
HOMEPAGE = "http://www.net-dns.org/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b7e4176275d57d0b036bdccbc01a0e7"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NL/NLNETLABS/Net-DNS-SEC-${PV}.tar.gz"
SRC_URI[sha256sum] = "88592c65487fb7b4d05134f2f9c48e649a9cd533a8493c50189b649b4ea711a6"

DEPENDS += "openssl"

UPSTREAM_CHECK_REGEX = "Net\-DNS\-SEC\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${UNPACKDIR}/Net-DNS-SEC-${PV}"

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

do_install_ptest_perl:append(){
   cp ${D}${PTEST_PATH}/t/TestToolkit.pm ${D}${PTEST_PATH}

   # This test reconciles the perl module's MANIFEST file with the actual files
   # in the module. This might be useful for package integrity check, but not so
   # much for runtime testing - and it also requires the whole source tree to
   # be installed. Rather just remove it.
   rm ${D}${PTEST_PATH}/t/00-install.t
}

RDEPENDS:${PN}-ptest += "\
    libnet-dns-perl \
    perl-module-extutils-mm-unix \
    perl-module-file-spec-functions \
    perl-module-perlio"
