SUMMARY = "Monocypher is an easy-to-use crypto library"
DESCRIPTION = "Monocypher is an easy to use, easy to deploy, \
auditable crypto library written in portable C. It approaches the size of TweetNaCl and the speed of libsodium."
HOMEPAGE = "https://monocypher.org/"
SECTION = "libs"
LICENSE = "BSD-2-Clause | CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=ff30a1c41dfd9e6fa559a9e45ee98302"

SRC_URI = "https://monocypher.org/download/${BPN}-${PV}.tar.gz"
SRC_URI[sha512sum] = "bf275d4c53ff94af6cdc723a4e002e9f080f4d1436c86c76bb37870b34807f1d7b32331d8ff8a1aeb369e946f3769021e03e63efac25b82efc5abf54dc084714"

MIRRORS = "https://.*/.* https://github.com/LoupVaillant/Monocypher/releases/download/${PV}/${BPN}-${PV}.tar.gz "

S = "${WORKDIR}/${BPN}-${PV}"

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
