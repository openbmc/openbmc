DESCRIPTION = "This package contains the DNS.pm module with friends."
HOMEPAGE = "http://www.net-dns.org/"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://README;beginline=252;endline=269;md5=27db37b42cd1a5173a53922d67072bcb"

DEPENDS += "perl"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/N/NL/NLNETLABS/Net-DNS-${PV}.tar.gz"

SRC_URI[md5sum] = "91e8593eb6eed41995e6edc567fb6fec"
SRC_URI[sha256sum] = "ddefe13b28084ffcc8f10a96b3c13c59449dbf6fc371c006d129630ea0ce767a"

UPSTREAM_CHECK_REGEX = "Net\-DNS\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/Net-DNS-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

RDEPENDS_${PN} = " \
    libdigest-hmac-perl \
    perl-module-base \
    perl-module-constant \
    perl-module-digest-md5 \
    perl-module-digest-sha \
    perl-module-file-spec \
    perl-module-integer \
    perl-module-io-file \
    perl-module-io-select \
    perl-module-io-socket \
    perl-module-io-socket-ip \
    perl-module-mime-base64 \
    perl-module-scalar-util \
    perl-module-test-more \
    perl-module-time-local \
"

RRECOMMENDS_${PN} += " \
    libnet-dns-sec-perl \
"

RDEPENDS_${PN}-ptest += " \
    perl-module-encode \
    perl-module-encode-byte \
    perl-module-extutils-mm \
    perl-module-extutils-mm-unix \
    perl-module-overload \
"

python __anonymous () {
    # rather than use "find" to determine libc-*.so,
    # statically export the known paths for glibc and musl
    import os
    if d.getVar('TCLIBC') == "glibc":
        os.environ["LIBC"] = "${STAGING_BASELIBDIR}/libc.so.6"
    elif d.getVar('TCLIBC') == "musl":
        os.environ["LIBC"] = "${STAGING_LIBDIR}/libc.so"
    else:
       raise bb.parse.SkipRecipe("incompatible with %s C library" %
                                   d.getVar('TCLIBC'))
}

BBCLASSEXTEND = "native"
