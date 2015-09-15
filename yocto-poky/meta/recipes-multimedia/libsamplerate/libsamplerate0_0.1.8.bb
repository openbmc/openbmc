SUMMARY = "Audio Sample Rate Conversion library"
HOMEPAGE = "http://www.mega-nerd.com/SRC/"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://src/samplerate.c;beginline=1;endline=17;md5=d0807c35fc906466d24a50463534815a"
DEPENDS = "flac libsndfile1"
PR = "r1"

SRC_URI = "http://www.mega-nerd.com/SRC/libsamplerate-${PV}.tar.gz"

SRC_URI[md5sum] = "1c7fb25191b4e6e3628d198a66a84f47"
SRC_URI[sha256sum] = "93b54bdf46d5e6d2354b7034395fe329c222a966790de34520702bb9642f1c06"
S = "${WORKDIR}/libsamplerate-${PV}"

inherit autotools pkgconfig

PACKAGECONFIG[fftw] = ",--disable-fftw,fftw"
