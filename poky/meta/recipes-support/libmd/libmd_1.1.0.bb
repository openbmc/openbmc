SUMMARY = "Message Digest functions from BSD systems"
DESCRIPTION = "This library provides message digest functions \
found on BSD systems either on their libc (NetBSD, OpenBSD) or \
libmd (FreeBSD, DragonflyBSD, macOS, Solaris) libraries and \
lacking on others like GNU systems."
HOMEPAGE = "https://www.hadrons.org/software/libmd/"

LICENSE = "BSD-3-Clause & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=0436d4fb62a71f661d6e8b7812f9e1df"

SRC_URI = "https://archive.hadrons.org/software/libmd/libmd-${PV}.tar.xz"
SRC_URI[sha256sum] = "1bd6aa42275313af3141c7cf2e5b964e8b1fd488025caf2f971f43b00776b332"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
