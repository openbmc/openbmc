SUMMARY = "Message Digest functions from BSD systems"
DESCRIPTION = "This library provides message digest functions \
found on BSD systems either on their libc (NetBSD, OpenBSD) or \
libmd (FreeBSD, DragonflyBSD, macOS, Solaris) libraries and \
lacking on others like GNU systems."
HOMEPAGE = "https://www.hadrons.org/software/libmd/"

LICENSE = "BSD-3-Clause & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=0436d4fb62a71f661d6e8b7812f9e1df"

SRC_URI = "https://archive.hadrons.org/software/libmd/libmd-${PV}.tar.xz"
SRC_URI[sha256sum] = "5a02097f95cc250a3f1001865e4dbba5f1d15554120f95693c0541923c52af4a"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
