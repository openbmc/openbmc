# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "An implementation of the standard library for Linux-based systems"
DESCRIPTION = "A new standard library to power a new generation of Linux-based devices. \
musl is lightweight, fast, simple, free, and strives to be correct in the sense of \
standards-conformance and safety."

HOMEPAGE = "http://www.musl-libc.org/"
LICENSE = "MIT"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=8a4bb28f771c817fe57d3a7c4240e3aa"

PATH_prepend = "${STAGING_BINDIR_TOOLCHAIN}.${STAGINGCC}:"

INHIBIT_DEFAULT_DEPS = "1"

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${SDK_SYS}"


FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/lib*${SOLIBSDEV}"
INSANE_SKIP_${PN} = "dev-so"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

# thumb1 is unsupported
ARM_INSTRUCTION_SET_armv5 = "arm"
ARM_INSTRUCTION_SET_armv4 = "arm"

