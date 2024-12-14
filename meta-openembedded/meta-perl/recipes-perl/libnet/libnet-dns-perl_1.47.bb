DESCRIPTION = "This package contains the DNS.pm module with friends."
HOMEPAGE = "https://metacpan.org/pod/Net::DNS"
SECTION = "libs"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://README;beginline=252;endline=269;md5=de95b6a896d5f861d724ea854d316a0b"

DEPENDS += "perl"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NL/NLNETLABS/Net-DNS-${PV}.tar.gz"

SRC_URI[sha256sum] = "fec3dab10ac82dbac26b1ae2e367909256f420835d96cda7dc1b508e29938f0b"

UPSTREAM_CHECK_REGEX = "Net\-DNS\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/Net-DNS-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

RDEPENDS:${PN} = " \
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

RRECOMMENDS:${PN} += " \
    libnet-dns-sec-perl \
"

RDEPENDS:${PN}-ptest += " \
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

