LICENSE = "GPL-2.0-only & GPL-3.0-only & BSD-3-Clause & LGPL-2.0-only & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5fa987762101f748a6cdd951b64ffc6b"
SRC_URI = "git://github.com/DrTimothyAldenDavis/SuiteSparse;protocol=https;branch=stable \
           file://0001-Preserve-CXXFLAGS-from-environment-in-Mongoose.patch \
           file://0002-Preserve-links-when-installing-libmetis.patch \
           file://0003-Add-version-information-to-libmetis.patch \
           file://makefile-quoting.patch \
           "
SRCREV = "538273cfd53720a10e34a3d80d3779b607e1ac26"


DEPENDS = "cmake-native lapack gmp mpfr chrpath-native"

PROVIDES = "mongoose graphblas"
RPROVIDES:${PN} = "mongoose graphblas"

inherit cmake

B = "${S}"

export CMAKE_OPTIONS = " \
    -DCMAKE_INSTALL_PREFIX=${D}${prefix} \
    -DCMAKE_INSTALL_LIBDIR=${baselib} \
"

OECMAKE_SOURCEPATH = "${S}/Mongoose ${S}/metis-5.1.0 ${S}/GraphBLAS"

do_compile () {
	oe_runmake library
}

do_install () {
	oe_runmake prefix=${D}${prefix} INSTALL=${D}${prefix} install

        # Remove runtime paths from shared libraries
        for file in ${D}${libdir}/*.so.*; do
            if [ ! -L "$file" ]; then
                chrpath -d "$file"
            fi
        done
}

FILES:${PN} += " \
    ${libdir}/libmongoose.so.* \
    ${libdir}/libgraphblas.so.* \
    ${libdir}/libmetis.so.* \
    ${libdir}/libsuitesparseconfig.so.* \
    ${libdir}/libamd.so.* \
    ${libdir}/libbtf.so.* \
    ${libdir}/libcamd.so.* \
    ${libdir}/libccolamd.so.* \
    ${libdir}/libcolamd.so.* \
    ${libdir}/libcholmod.so.* \
    ${libdir}/libcxsparse.so.* \
    ${libdir}/libldl.so.* \
    ${libdir}/libklu.so.* \
    ${libdir}/libumfpack.so.* \
    ${libdir}/librbio.so.* \
    ${libdir}/libspqr.so.* \
    ${libdir}/libsliplu.so.* \
    ${bindir}/mongoose \
"

FILES:${PN}-staticdev += "${libdir}/libmongoose.a"
FILES:${PN}-dev += "${includedir} ${libdir}/*.so"

EXCLUDE_FROM_WORLD = "1"
