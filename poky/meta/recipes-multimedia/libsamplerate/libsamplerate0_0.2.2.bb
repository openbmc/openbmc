SUMMARY = "Audio Sample Rate Conversion library"
DESCRIPTION = "Also known as Secret Rabbit Code - a library for performing sample rate conversion of audio data."
HOMEPAGE = "https://libsndfile.github.io/libsamplerate/"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=336d6faf40fb600bafb0061f4052f1f4 \
                    file://src/samplerate.c;beginline=1;endline=7;md5=7a4238289dc36bfb70968ccaa5bd0d4f"
DEPENDS = "libsndfile1"

SRC_URI = "https://github.com/libsndfile/libsamplerate/releases/download/${PV}/libsamplerate-${PV}.tar.xz \
"

SRC_URI[sha256sum] = "3258da280511d24b49d6b08615bbe824d0cacc9842b0e4caf11c52cf2b043893"

CVE_PRODUCT = "libsamplerate"

UPSTREAM_CHECK_URI = "https://github.com/libsndfile/libsamplerate/releases"

S = "${WORKDIR}/libsamplerate-${PV}"

inherit autotools pkgconfig

# FFTW and ALSA are only used in tests and examples, so they don't affect
# normal builds. It should be safe to ignore these, but explicitly disabling
# them adds some extra certainty that builds are deterministic.
EXTRA_OECONF = "--disable-fftw --disable-alsa"
