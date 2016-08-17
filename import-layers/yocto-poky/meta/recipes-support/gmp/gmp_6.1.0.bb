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
           "

SRC_URI[md5sum] = "86ee6e54ebfc4a90b643a65e402c4048"
SRC_URI[sha256sum] = "498449a994efeba527885c10405993427995d3f86b8768d8cdf8d9dd7c6b73e8"

acpaths = ""

EXTRA_OECONF += " --enable-cxx=detect"

PACKAGES =+ "libgmpxx"
FILES_libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

do_install_append_class-target() {
        sed -i "s|--sysroot=${STAGING_DIR_HOST}||g" ${D}${includedir}/gmp.h
}

SSTATE_SCAN_FILES += "gmp.h"

BBCLASSEXTEND = "native nativesdk"
