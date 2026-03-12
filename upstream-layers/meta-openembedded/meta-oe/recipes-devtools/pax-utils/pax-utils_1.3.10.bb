SUMMARY = "Security-focused ELF files checking tool"
DESCRIPTION = "This is a small set of various PaX aware and related \
utilities for ELF binaries. It can check ELF binary files and running \
processes for issues that might be relevant when using ELF binaries \
along with PaX, such as non-PIC code or executable stack and heap."
HOMEPAGE = "https://wiki.gentoo.org/wiki/Hardened/PaX_Utilities"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "git://github.com/gentoo/pax-utils;protocol=https;branch=master;tag=v${PV}"
SRCREV = "d279ca563775105859f1f8c8467b8244d758cc62"

RDEPENDS:${PN} += "bash"

BBCLASSEXTEND = "native"

inherit meson pkgconfig

PACKAGECONFIG ??= ""

PACKAGECONFIG[libcap] = "-Duse_libcap=enabled, -Duse_libcap=disabled, libcap"
PACKAGECONFIG[libseccomp] = "-Duse_seccomp=true, -Duse_seccomp=false, libseccomp"
PACKAGECONFIG[pyelftools] = "-Dlddtree_implementation=python, -Dlddtree_implementation=sh,, python3-pyelftools"

do_install:append(){
    if ${@bb.utils.contains('PACKAGECONFIG', 'pyelftools', 'true', 'false', d)}; then
        sed -i 's,#!/usr/bin/env python,#!/usr/bin/env python3,' ${D}${bindir}/lddtree
    fi
}

