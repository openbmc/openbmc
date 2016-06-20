# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "keith bostic's POSIX file tree stream operations library"
HOMEPAGE = "https://sites.google.com/a/bostic.com/keithbostic"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://fts.h;beginline=1;endline=36;md5=2532eddb3d1a21905723a4011ec4e085"
SECTION = "libs"

SRC_URI = "https://sites.google.com/a/bostic.com/keithbostic/files/fts.tar.gz \
           file://fts-header-correctness.patch \
           file://fts-uclibc.patch \
           file://remove_cdefs.patch \
           file://stdint.patch \
           file://gcc5.patch \
"

SRC_URI[md5sum] = "120c14715485ec6ced14f494d059d20a"
SRC_URI[sha256sum] = "3df9b9b5a45aeaf16f33bb84e692a10dc662e22ec8a51748f98767d67fb6f342"

S = "${WORKDIR}/${BPN}"

do_configure[noexec] = "1"

HASHSTYLE_mips = "sysv"
HASHSTYLE_mipsel = "sysv"
HASHSTYLE_mips64 = "sysv"
HASHSTYLE_mips64el = "sysv"
HASHSTYLE = "gnu"

VER = "0"
do_compile () {
    ${CC} -I${S} -fPIC -shared -Wl,--hash-style=${HASHSTYLE} -o libfts.so.${VER} -Wl,-soname,libfts.so.${VER} ${S}/fts.c
}

do_install() {
    install -Dm755 ${B}/libfts.so.${VER} ${D}${libdir}/libfts.so.${VER}
    ln -sf libfts.so.${VER} ${D}${libdir}/libfts.so
    install -Dm644 ${S}/fts.h ${D}${includedir}/fts.h
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"

