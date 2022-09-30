SUMMARY = "High-level language, primarily intended for numerical computations"
HOMEPAGE = "http://www.gnu.org/software/octave/"
SECTION = "math"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

# To build, add 'FORTRAN:forcevariable = ",fortran"' to your local.conf (or use
# meta-mortsgna as distro :)

DEPENDS = " \
    gperf-native \
    libglu \
    lapack \
    fftw \
    curl \
    freetype \
    fontconfig \
    texinfo \
    pcre \
    readline \
    fltk \
    hdf5 \
    libsndfile1 \
"

inherit autotools pkgconfig texinfo gettext gtk-icon-cache mime-xdg features_check

REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = " \
    ${GNU_MIRROR}/octave/${BPN}-${PV}.tar.gz \
    file://fix-blas-library-integer-size.patch \
"
SRC_URI[sha256sum] = "b12cb652587d31c5c382b39ed73463c22a5259ecb2fa6b323a27da409222dacc"

# Note: Qt5Help is required for gui -> qttools(-native) must be build with
# clang in PACKAGECONFIG
PACKAGECONFIG[gui] = "--with-qt=yes,--with-qt=no,qttools-native qttools qtbase"

EXTRA_OECONF = " \
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
