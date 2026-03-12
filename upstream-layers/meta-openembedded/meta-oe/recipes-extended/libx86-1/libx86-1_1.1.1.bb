SUMMARY = "x86 real-mode library"
DESCRIPTION = "A library to provide support for making real-mode calls x86 calls. On \
x86 hardware, vm86 mode is used. On other platforms, x86 emulation is \
provided."
HOMEPAGE = "http://www.codon.org.uk/~mjg59/libx86/"
LICENSE = "MIT & BSD-3-Clause"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=633af6c02e6f624d4c472d970a2aca53"

SRC_URI = "https://mirrors.slackware.com/slackware/slackware-current/source/ap/libx86/libx86-v${PV}.tar.lz \
           file://0001-assume-zero-is-valid-address.patch \
           file://0001-Define-CARD32-as-uint-as-it-is-32-bit.patch \
"
SRC_URI[sha256sum] = "0de221c8e2fcc84078155c1a82f86dcd71c2706033eb410d2090d86c99f51141"

UPSTREAM_CHECK_URI = "https://mirrors.slackware.com/slackware/slackware-current/source/ap/libx86/"

S = "${UNPACKDIR}/${BPN}-v${PV}"

BPN = "libx86"
COMPATIBLE_HOST = '(x86_64|i.86).*-linux'

export LIBDIR = "${libdir}"
export BACKEND = "x86emu"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}
