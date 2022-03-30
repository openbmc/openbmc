SUMMARY = "Net::LibIDN - Perl bindings for GNU Libidn"
DESCRIPTION = "\
Provides bindings for GNU Libidn, a C library for handling Internationalized \
Domain Names according to IDNA (RFC 3490), in a way very much inspired by \
Turbo Fredriksson's PHP-IDN. \
"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
HOMEPAGE = "http://search.cpan.org/dist/Net-LibIDN/"
DEPENDS += "libidn"
# We must need glibc-gconvs to enable charset related functions,
# such as Net::LibIDN::idn_to_ascii().
RDEPENDS:${PN}:append:libc-glibc = " glibc-gconvs"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/T/TH/THOR/Net-LibIDN-${PV}.tar.gz"
SRC_URI[md5sum] = "c3e4de2065009d67bcb1df0afb473e12"
SRC_URI[sha256sum] = "2f8acc9442b3866ec7dc63cd449fc693ae3e930d5d3e5e9430fbb6f393bdbb17"

SRC_URI += "file://libidn-wr-cross-compile.patch"

LIC_FILES_CHKSUM = "file://README;beginline=42;endline=92;md5=3374ea0369ca3ead6047520477a43147"

S = "${WORKDIR}/Net-LibIDN-${PV}"

EXTRA_CPANFLAGS = "--with-libidn=${STAGING_LIBDIR} --with-libidn-inc=${STAGING_INCDIR} --compiler='${CC}'"
EXTRA_CPANFLAGS += "--disable-tld"

inherit cpan

FILES:${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/Net/LibIDN/.debug/"

do_configure:prepend() {
    rm -rf ${S}/.pc/
}
