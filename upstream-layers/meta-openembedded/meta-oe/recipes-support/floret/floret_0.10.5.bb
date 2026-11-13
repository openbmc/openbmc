# Copyright (c) Qualcomm Technologies, Inc. and/or its subsidiaries.
# SPDX-License-Identifier: BSD-3-Clause-Clear

SUMMARY     = "fastText + Bloom embeddings for compact, full-coverage vectors"
DESCRIPTION = "\
    floret is a fastText fork with Bloom embeddings for compact, \
    full-coverage vectors compatible with spaCy. It is designed \
    specifically for use on small, low-powered, embedded devices \
    where memory efficiency and full vocabulary coverage are required. \
"
HOMEPAGE   = "https://github.com/explosion/floret"

LICENSE          = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ce16d1d2b4c556959d654417007b713"

DEPENDS = "zlib"

SRC_URI = "git://github.com/explosion/floret.git;protocol=https;branch=main;tag=v${PV} \
           file://0001-PATCH-floret-Adjust-CMake-flags-for-cross-compilation.patch \
           file://0002-args.h-add-missing-cstdint-include-for-int64_t.patch \
           "

SRCREV = "775638223bee4fabc420cc8234632557452558e0"

inherit cmake

# Remove -march=native: invalid for cross-compilation
CXXFLAGS:remove = "-march=native"
CXXFLAGS       += "-std=c++17"

EXTRA_OECMAKE = "\
    -DCMAKE_CXX_STANDARD=17 \
    -DBUILD_SHARED_LIBS=ON \
"

do_install:append() {
    # cmake_do_install follows the libfloret.so build symlink and installs
    # the ELF directly as ${libdir}/libfloret.so.  Re-install under the real
    # SONAME and recreate the unversioned dev symlink so debian.bbclass sees
    # a proper shared-library layout and does not rename both ${PN} and
    # ${PN}-dev to the same package name, which would cause
    # do_create_package_spdx to fail.
    if [ -e ${B}/libfloret.so ]; then
        soname=$(${OBJDUMP} -p ${B}/libfloret.so | awk '/SONAME/ { print $2 }')
        rm -f ${D}${libdir}/libfloret.so
        install -m 0755 ${B}/libfloret.so ${D}${libdir}/$soname
        ln -sf $soname ${D}${libdir}/libfloret.so
    fi
}

PACKAGES =+ "${PN}-cli"

FILES:${PN}-cli       = "${bindir}/floret"
FILES:${PN}-dev      += "${libdir}/libfloret.so"
FILES:${PN}-staticdev = "${libdir}/libfloret.a ${libdir}/libfloret_pic.a"
FILES:${PN}          += "${libdir}/libfloret.so.*"

SUMMARY:${PN}           = "floret runtime library"
SUMMARY:${PN}-cli       = "floret command-line tool for training Bloom embeddings"
SUMMARY:${PN}-dev       = "floret development headers and shared library"
SUMMARY:${PN}-staticdev = "floret static library"
