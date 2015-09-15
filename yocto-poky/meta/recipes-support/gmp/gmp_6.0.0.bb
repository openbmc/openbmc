require gmp.inc

LICENSE="GPLv2+ | LGPLv3+"

REVISION="a"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                   file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
                   file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

SRC_URI += "file://configure.patch \
            file://amd64.patch \
            file://use-includedir.patch \
            file://append_user_provided_flags.patch \
            file://gmp-6.0.0-ppc64.patch \
            "
SRC_URI[md5sum] = "b7ff2d88cae7f8085bd5006096eed470"
SRC_URI[sha256sum] = "7f8e9a804b9c6d07164cf754207be838ece1219425d64e28cfa3e70d5c759aaf"

acpaths = ""

EXTRA_OECONF += " --enable-cxx=detect"

PACKAGES =+ "libgmpxx"
FILES_libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

do_install_append_class-target() {
        sed -i "s|--sysroot=${STAGING_DIR_HOST}||g" ${D}${includedir}/gmp.h
}

SSTATE_SCAN_FILES += "gmp.h"

