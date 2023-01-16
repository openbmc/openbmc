LICENSE = "GPL-2.0-only & GPL-3.0-only & BSD-3-Clause & LGPL-2.0-only & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5fa987762101f748a6cdd951b64ffc6b"
SRC_URI = "git://github.com/DrTimothyAldenDavis/SuiteSparse;protocol=https;branch=stable \
           file://0001-Preserve-CXXFLAGS-from-environment-in-Mongoose.patch \
           file://0002-Preserve-links-when-installing-libmetis.patch \
           file://0003-Add-version-information-to-libmetis.patch \
           "
SRCREV = "538273cfd53720a10e34a3d80d3779b607e1ac26"

S = "${WORKDIR}/git"

DEPENDS = "cmake-native lapack gmp mpfr chrpath-native"

PROVIDES = "mongoose graphblas"
RPROVIDES:${PN} = "mongoose graphblas"

# The values of $CC, $CXX, and $LD that Bitbake uses have spaces in them which
# causes problems when the SuiteSparse Makefiles try to pass these values on
# the command line. To get around this problem, set these variables to only the
# program name and prepend the rest of the value onto the corresponding FLAGS
# variable.
CFLAGS:prepend := "${@" ".join(d.getVar('CC', True).split()[1:])} "
export CC := "${@d.getVar('CC', True).split()[0]}"

CXXFLAGS:prepend := "${@" ".join(d.getVar('CXX', True).split()[1:])} "
export CXX := "${@d.getVar('CXX', True).split()[0]}"

LDFLAGS:prepend := "${@" ".join(d.getVar('LD', True).split()[1:])} "
export LD := "${@d.getVar('LD', True).split()[0]}"

export CMAKE_OPTIONS = " \
    -DCMAKE_INSTALL_PREFIX=${D}${prefix} \
    -DCMAKE_INSTALL_LIBDIR=${baselib} \
"

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
