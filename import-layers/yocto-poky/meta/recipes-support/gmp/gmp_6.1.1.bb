require gmp.inc

LICENSE = "GPLv2+ | LGPLv3+"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                   file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
                   file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

REVISION = ""
SRC_URI = "https://gmplib.org/download/${BPN}/${BP}${REVISION}.tar.bz2 \
           file://amd64.patch \
           file://use-includedir.patch \
           file://0001-Append-the-user-provided-flags-to-the-auto-detected-.patch \
           file://0001-confiure.ac-Believe-the-cflags-from-environment.patch \
           "

SRC_URI[md5sum] = "4c175f86e11eb32d8bf9872ca3a8e11d"
SRC_URI[sha256sum] = "a8109865f2893f1373b0a8ed5ff7429de8db696fc451b1036bd7bdf95bbeffd6"

acpaths = ""

EXTRA_OECONF += " --enable-cxx=detect"

PACKAGES =+ "libgmpxx"
FILES_libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

do_install_append_class-target() {
        sed -i "s|--sysroot=${STAGING_DIR_HOST}||g" ${D}${includedir}/gmp.h
}

SSTATE_SCAN_FILES += "gmp.h"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
