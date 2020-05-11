require gmp.inc

LICENSE = "GPLv2+ | LGPLv3+"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                   file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
                   file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

REVISION = ""
SRC_URI = "https://gmplib.org/download/${BPN}/${BP}${REVISION}.tar.bz2 \
           file://use-includedir.patch \
           file://0001-Append-the-user-provided-flags-to-the-auto-detected-.patch \
           file://0001-confiure.ac-Believe-the-cflags-from-environment.patch \
           "
SRC_URI[md5sum] = "c24161e0dd44cae78cd5f67193492a21"
SRC_URI[sha256sum] = "f51c99cb114deb21a60075ffb494c1a210eb9d7cb729ed042ddb7de9534451ea"

acpaths = ""

EXTRA_OECONF += " --enable-cxx=detect"
EXTRA_OECONF_mipsarchr6_append = " --disable-assembly"

PACKAGES =+ "libgmpxx"
FILES_libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

do_install_append() {
	oe_multilib_header gmp.h
}

do_install_prepend_class-target() {
        sed -i \
        -e "s|--sysroot=${STAGING_DIR_HOST}||g" \
        -e "s|${DEBUG_PREFIX_MAP}||g" \
         ${B}/gmp.h
}

SSTATE_SCAN_FILES += "gmp.h"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
