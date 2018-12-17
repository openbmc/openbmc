SUMMARY = "Guess OpenSSL include path"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea914cc2718e8d53bd7744d96e66c03c"

SRC_URI = "http://www.cpan.org/modules/by-module/Crypt/Crypt-OpenSSL-Guess-${PV}.tar.gz "

SRC_URI[md5sum] = "e768fe2c07826b0ac9ea604c79f93032"
SRC_URI[sha256sum] = "aa6b18e38cb852cbad80a58cd90c395b40819d4d01e0ab37e7703149094d7167"

DEPENDS += "openssl"

RDEPENDS_${PN}="perl-module-config perl-module-exporter perl-module-symbol perl-module-file-spec"

EXTRA_CPANFLAGS = "INC='-I${STAGING_INCDIR}' LIBS='-L${STAGING_LIBDIR} -lssl -L${STAGING_DIR_TARGET}${base_libdir} -lcrypto'"

S = "${WORKDIR}/Crypt-OpenSSL-Guess-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
