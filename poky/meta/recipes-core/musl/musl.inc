# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "An implementation of the standard library for Linux-based systems"
DESCRIPTION = "A new standard library to power a new generation of Linux-based devices. \
musl is lightweight, fast, simple, free, and strives to be correct in the sense of \
standards-conformance and safety."

HOMEPAGE = "http://www.musl-libc.org/"
LICENSE = "MIT"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=0c2904cdc34777fb4067732bae145506"

INHIBIT_DEFAULT_DEPS = "1"

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/lib*${SOLIBSDEV}"
INSANE_SKIP:${PN} = "dev-so"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

# thumb1 is unsupported
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv4 = "arm"

# 1.2.4 doesn't support riscv32
COMPATIBLE_HOST:riscv32 = "null"

# Enable out of tree build
B = "${WORKDIR}/build"

do_configure[cleandirs] = "${B}"

python () {
    multilibs = d.getVar("MULTILIBS")
    if multilibs:
        raise bb.parse.SkipRecipe("musl is not compatible with multilibs")
}
