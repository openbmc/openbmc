LICENSE = "GPLv2 & GPLv3 & BSD-3-Clause & LGPL-2.0 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f9f2b9d61cb769a67c4cd079e1166de7"
SRC_URI = "git://github.com/DrTimothyAldenDavis/SuiteSparse;protocol=https \
           file://0001-Preserve-CXXFLAGS-from-environment-in-Mongoose.patch \
           file://0002-Preserve-links-when-installing-libmetis.patch \
           file://0003-Add-version-information-to-libmetis.patch \
           "

SRC_URI[md5sum] = "c414679bbc9432a3def01b31ad921140"
SRC_URI[sha256sum] = "06726e471fbaa55f792578f9b4ab282ea9d008cf39ddcc3b42b73400acddef40"

SRCREV = "v${PV}"

S = "${WORKDIR}/git"

DEPENDS = "cmake-native lapack gmp mpfr chrpath-native"

PROVIDES = "mongoose graphblas"
RPROVIDES_${PN} = "mongoose graphblas"

# The values of $CC, $CXX, and $LD that Bitbake uses have spaces in them which
# causes problems when the SuiteSparse Makefiles try to pass these values on
# the command line. To get around this problem, set these variables to only the
# program name and prepend the rest of the value onto the corresponding FLAGS
# variable.
CFLAGS_prepend := "${@" ".join(d.getVar('CC', True).split()[1:])} "
export CC := "${@d.getVar('CC', True).split()[0]}"

CXXFLAGS_prepend := "${@" ".join(d.getVar('CXX', True).split()[1:])} "
export CXX := "${@d.getVar('CXX', True).split()[0]}"

LDFLAGS_prepend := "${@" ".join(d.getVar('LD', True).split()[1:])} "
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

FILES_${PN} += " \
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

FILES_${PN}-staticdev += "${libdir}/libmongoose.a"
FILES_${PN}-dev += "${includedir} ${libdir}/*.so"

EXCLUDE_FROM_WORLD = "1"

