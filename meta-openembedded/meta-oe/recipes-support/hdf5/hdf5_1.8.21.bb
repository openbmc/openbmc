SUMMARY = "Management suite for extremely large and complex data collections"
DESCRIPTION = "Unique technology suite that makes possible the management of \
extremely large and complex data collections"
HOMEPAGE = "https://www.hdfgroup.org/"
SECTION = "libs"

LICENSE = "HDF5"
LIC_FILES_CHKSUM = "file://COPYING;md5=57e5351b17591e659eedae107265c606"

inherit cmake siteinfo

SRC_URI = " \
    https://support.hdfgroup.org/ftp/HDF5/releases/hdf5-1.8/hdf5-${PV}/src/${BPN}-${PV}.tar.bz2 \
    file://H5lib_settings.c \
    file://H5Tinit-64.c \
    file://H5Tinit-32.c \
    file://0001-cross-compiling-support.patch \
    file://0002-Remove-suffix-shared-from-shared-library-name.patch \
"
SRC_URI[md5sum] = "2d2408f2a9dfb5c7b79998002e9a90e9"
SRC_URI[sha256sum] = "e5b1b1dee44a64b795a91c3321ab7196d9e0871fe50d42969761794e3899f40d"

FILES_${PN} += "${libdir}/libhdf5.settings ${datadir}/*"

EXTRA_OECMAKE = " \
    -DTEST_LFS_WORKS_RUN=0 \
    -DTEST_LFS_WORKS_RUN__TRYRUN_OUTPUT=0 \
    -DH5_PRINTF_LL_TEST_RUN=1 \
    -DH5_PRINTF_LL_TEST_RUN__TRYRUN_OUTPUT='8' \
    -DH5_LDOUBLE_TO_LONG_SPECIAL_RUN=0 \
    -DH5_LDOUBLE_TO_LONG_SPECIAL_RUN__TRYRUN_OUTPUT= \
    -DH5_LONG_TO_LDOUBLE_SPECIAL_RUN=0 \
    -DH5_LONG_TO_LDOUBLE_SPECIAL_RUN__TRYRUN_OUTPUT= \
    -DH5_LDOUBLE_TO_LLONG_ACCURATE_RUN=0 \
    -DH5_LDOUBLE_TO_LLONG_ACCURATE_RUN__TRYRUN_OUTPUT= \
    -DH5_LLONG_TO_LDOUBLE_CORRECT_RUN=0 \
    -DH5_LLONG_TO_LDOUBLE_CORRECT_RUN__TRYRUN_OUTPUT= \
    -DH5_NO_ALIGNMENT_RESTRICTIONS_RUN=0 \
    -DH5_NO_ALIGNMENT_RESTRICTIONS_RUN__TRYRUN_OUTPUT= \
    -DCMAKE_INSTALL_PREFIX='${prefix}' \
    -DHDF5_INSTALL_LIB_DIR='${baselib}' \
"

do_unpack[postfuncs] += "gen_hd5file"
gen_hd5file() {
    install -m 544 ${WORKDIR}/H5lib_settings.c ${S}
    install -m 544 ${WORKDIR}/H5Tinit-${SITEINFO_BITS}.c ${S}/H5Tinit.c
}

do_install_append() {
    # Used for generating config files on target
    install -m 755 ${B}/bin/H5detect ${D}${bindir}
    install -m 755 ${B}/bin/H5make_libsettings ${D}${bindir}
}

BBCLASSEXTEND = "native"

SRC_DISTRIBUTE_LICENSES += "HDF5"
