SUMMARY = "x86 real-mode library"
DESCRIPTION = "A library to provide support for making real-mode calls x86 calls. On \
x86 hardware, vm86 mode is used. On other platforms, x86 emulation is \
provided."
HOMEPAGE = "http://www.codon.org.uk/~mjg59/libx86/"
LICENSE = "MIT & BSD-3-Clause"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=633af6c02e6f624d4c472d970a2aca53"

SRC_URI = "https://mirrors.slackware.com/slackware/slackware-current/source/ap/libx86/libx86-${PV}.tar.gz \
           file://libx86-mmap-offset.patch \
           file://0001-assume-zero-is-valid-address.patch \
           file://makefile-add-ldflags.patch \
           file://0001-Fix-type-of-the-void-pointer-assignment.patch \
           file://0001-Define-CARD32-as-uint-as-it-is-32-bit.patch \
"
SRC_URI[sha256sum] = "5bf13104cb327472b5cb65643352a9138646becacc06763088d83001d832d048"

UPSTREAM_CHECK_URI = "https://mirrors.slackware.com/slackware/slackware-current/source/ap/libx86/"

BPN = "libx86"
COMPATIBLE_HOST = '(x86_64|i.86).*-linux'

export LIBDIR = "${libdir}"
export BACKEND = "x86emu"

inherit autotools-brokensep
