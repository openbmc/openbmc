DESCRIPTION = "FFTW"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

PROVIDES = "virtual/fftw"

RPROVIDES_${PN} += "libfftw3"

SRC_URI = "http://www.fftw.org/fftw-${PV}.tar.gz"

S = "${WORKDIR}/fftw-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-fortran --enable-shared --enable-threads"

do_configure_prepend() {
    echo 'AM_PROG_AS' >> ${S}/configure.ac 
}

PACKAGES =+ "lib${BPN}"
FILES_lib${BPN} = "${libdir}/lib*.so.*"

# rename binary in /bin and use u-a tpo choose between different precision implementations?
