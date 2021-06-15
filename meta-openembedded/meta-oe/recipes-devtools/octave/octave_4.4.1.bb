DESCRIPTION = "High-level language, primarily intended for numerical computations"
HOMEPAGE = "http://www.gnu.org/software/octave/"
SECTION = "math"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = "gperf-native texinfo lapack pcre readline"

inherit autotools pkgconfig texinfo gettext mime-xdg

EXTRA_OECONF = "--disable-java --disable-docs"

SRC_URI = "${GNU_MIRROR}/octave/${BPN}-${PV}.tar.gz \
           file://fix-blas-library-integer-size.patch \
"

SRC_URI[md5sum] = "b43bd5f4309a0c048c91af10cf8e8674"
SRC_URI[sha256sum] = "09fbd0f212f4ef21e53f1d9c41cf30ce3d7f9450fb44911601e21ed64c67ae97"

do_compile_prepend() {
	for folder in "liboctave/operators liboctave/numeric liboctave/array liboctave/util"; do
		mkdir -p ${B}/${folder}
	done
}

PACKAGES =+ " octave-common liboctave liboctave-dev liboctave-dbg"

FILES_${PN} = "${bindir}/* ${sbindir}/* ${libexecdir}/* ${datadir}/${PN} \
            ${libdir}/${PN}/${PV}/oct ${libdir}/${PN}/${PV}/site  \
            ${libdir}/${PN}/site ${datadir}/applications ${datadir}/metainfo"
FILES_${PN}-common = "${datadir}/icons"

FILES_liboctave = "${libdir}/${PN}/${PV}/lib*${SOLIBS}"
FILES_liboctave-dev = "${libdir}/${PN}/${PV}/lib*${SOLIBSDEV}"
FILES_liboctave-dbg = "${libdir}/${PN}/${PV}/.debug"

FILES_${PN}-dbg = "${bindir}/.debug ${libdir}/${PN}/${PV}/oct/${TARGET_SYS}/.debug"

EXCLUDE_FROM_WORLD = "1"
