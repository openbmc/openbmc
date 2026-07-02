SUMMARY = "Monocypher is an easy-to-use crypto library"
DESCRIPTION = "Monocypher is an easy to use, easy to deploy, \
auditable crypto library written in portable C. It approaches the size of TweetNaCl and the speed of libsodium."
HOMEPAGE = "https://monocypher.org/"
SECTION = "libs"
LICENSE = "BSD-2-Clause | CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=ff30a1c41dfd9e6fa559a9e45ee98302"

SRC_URI = "https://monocypher.org/download/${BPN}-${PV}.tar.gz"
SRC_URI[sha512sum] = "40904ada5c7ee4f7741733e38b69a30a4b0561cbffba5ffe7c2dce16136d540251ec0d9056ff606510d3b5b708fb8a40db7e0870d4a0b2dc17ba2bfb880f8965"

MIRRORS =+ "https://.*/.* https://github.com/LoupVaillant/Monocypher/releases/download/${PV}/${BPN}-${PV}.tar.gz"

S = "${UNPACKDIR}/${BPN}-${PV}"

CFLAGS += "-pedantic -Wall -Wextra -O3"
EXTRA_OEMAKE = "'PREFIX=${prefix}' 'DESTDIR=${D}' 'CFLAGS=${CFLAGS}' 'LIBDIR=${libdir}'"

do_compile() {
    oe_runmake library
}

do_install() {
    oe_runmake install-lib
    oe_runmake install-pc
}

BBCLASSEXTEND = "native nativesdk"
