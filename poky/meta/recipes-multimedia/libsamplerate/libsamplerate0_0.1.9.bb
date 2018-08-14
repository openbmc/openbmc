SUMMARY = "Audio Sample Rate Conversion library"
HOMEPAGE = "http://www.mega-nerd.com/SRC/"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=336d6faf40fb600bafb0061f4052f1f4 \
                    file://src/samplerate.c;beginline=1;endline=7;md5=5b6982a8c2811c7312c13cccbf55f55e"
DEPENDS = "libsndfile1"
PR = "r1"

SRC_URI = "http://www.mega-nerd.com/SRC/libsamplerate-${PV}.tar.gz \
           file://0001-configure.ac-improve-alsa-handling.patch \
"

SRC_URI[md5sum] = "2b78ae9fe63b36b9fbb6267fad93f259"
SRC_URI[sha256sum] = "0a7eb168e2f21353fb6d84da152e4512126f7dc48ccb0be80578c565413444c1"

CVE_PRODUCT = "libsamplerate"

UPSTREAM_CHECK_URI = "http://www.mega-nerd.com/SRC/download.html"

S = "${WORKDIR}/libsamplerate-${PV}"

inherit autotools pkgconfig

# FFTW and ALSA are only used in tests and examples, so they don't affect
# normal builds. It should be safe to ignore these, but explicitly disabling
# them adds some extra certainty that builds are deterministic.
EXTRA_OECONF = "--disable-fftw --disable-alsa"
