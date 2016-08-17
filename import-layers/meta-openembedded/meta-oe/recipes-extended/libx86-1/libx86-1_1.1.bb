SUMMARY = "x86 real-mode library"
DESCRIPTION = "A library to provide support for making real-mode calls x86 calls. On \
x86 hardware, vm86 mode is used. On other platforms, x86 emulation is \
provided."
HOMEPAGE = "http://www.codon.org.uk/~mjg59/libx86/"
LICENSE = "MIT & BSD-3-Clause"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=633af6c02e6f624d4c472d970a2aca53"

SRC_URI = "http://www.codon.org.uk/~mjg59/libx86/downloads/${BPN}-${PV}.tar.gz \
           file://libx86-mmap-offset.patch \
           file://0001-assume-zero-is-valid-address.patch \
"

SRC_URI[md5sum] = "41bee1f8e22b82d82b5f7d7ba51abc2a"
SRC_URI[sha256sum] = "5bf13104cb327472b5cb65643352a9138646becacc06763088d83001d832d048"

BPN = "libx86"
COMPATIBLE_HOST = '(x86_64|i.86).*-linux'

export LIBDIR = "${libdir}"
export BACKEND = "x86emu"

inherit autotools-brokensep
