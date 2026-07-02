SUMMARY = "High-level language, primarily intended for numerical computations"
DESCRIPTION = "\
    GNU Octave is a scientific programming language with powerful \
    mathematics-oriented syntax with built-in 2D/3D plotting and visualization \
    tools, which is drop-in compatible with many Matlab scripts. \
"
HOMEPAGE = "https://octave.org/"
BUGTRACKER = "https://bugs.octave.org/"
SECTION = "math"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

# To build, add 'FORTRAN:forcevariable = ",fortran"' to your local.conf (or use
# meta-mortsgna as distro :)

DEPENDS = "\
    curl \
    fftw \
    fltk \
    fontconfig \
    freetype \
    gperf-native \
    hdf5 \
    lapack \
    libglu \
    libsndfile1 \
    pcre2 \
    readline \
    texinfo \
"

inherit autotools pkgconfig texinfo gettext gtk-icon-cache mime-xdg features_check

# File /usr/lib/octave/11.3.0/liboctinterp.so.15.0.1 in package octave contains reference to TMPDIR [buildpaths]
#  contains the whole compiler invokation
# File /usr/lib/octave/11.3.0/.debug/liboctave.so.13.0.2 in package octave-dbg contains reference to TMPDIR [buildpaths]
#  contains all fortran compiler options
# File /usr/src/debug/octave/11.3.0/libinterp/build-env.cc in package octave-src contains reference to TMPDIR [buildpaths]
#  contains multiple variables from the build environment
# File /usr/src/debug/octave/11.3.0/src/mkoctfile.cc in package octave-src contains reference to TMPDIR [buildpaths]
#  contains the variables CC CFLAGS CXX and CXXFLAGS from the build environment
INSANE_SKIP += "buildpaths"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "2b80f3149b2de6d1f4f2fcb4fe6515a17eb363b52111bf57b90f37bf6f5e12e1"

# Note: Qt5Help is required for gui -> qttools(-native) must be build with
# clang in PACKAGECONFIG
PACKAGECONFIG[gui] = "--with-qt,--without-qt,qttools-native qttools qtbase"

EXTRA_OECONF = "\
    --disable-java \
    --disable-docs \
"

do_compile:prepend() {
    for folder in "liboctave/operators liboctave/numeric liboctave/array liboctave/util"; do
        mkdir -p ${B}/${folder}
    done
}

FILES:${PN} += "${datadir}/metainfo"
FILES:${PN}-dev += "${libdir}/${BPN}/${PV}/lib*${SOLIBSDEV}"

# fortran is not enabled by default
EXCLUDE_FROM_WORLD = "1"
