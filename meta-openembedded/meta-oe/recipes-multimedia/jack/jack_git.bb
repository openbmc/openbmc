DESCRIPTION = "jackdmp is a C++ version of the JACK low-latency audio \
server for multi-processor machines. It is a new implementation of the \
JACK server core features that aims in removing some limitations of \
the JACK1 design. The activation system has been changed for a data \
flow model and lock-free programming techniques for graph access have \
been used to have a more dynamic and robust system."
SECTION = "libs/multimedia"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://common/jack/control.h;beginline=2;endline=21;md5=e6df0bf30cde8b3b825451459488195d \
    file://common/jack/jack.h;beginline=1;endline=19;md5=6b736ed6b810592b135480a5e853392e \
"

DEPENDS = "libsamplerate0 libsndfile1 readline"

SRC_URI = "git://github.com/jackaudio/jack2.git \
           file://0001-Python-3.7-fixes-2126.patch \
           "
SRCREV = "c1647819eed6d11f94b21981d9c869629299f357"
PV = "1.9.12"
S = "${WORKDIR}/git"

inherit waf pkgconfig

PACKAGECONFIG ??= "alsa"
PACKAGECONFIG[alsa] = "--alsa=yes,--alsa=no,alsa-lib"
PACKAGECONFIG[opus] = "--opus=yes,--opus=no,libopus"

# portaudio is for windows builds only
EXTRA_OECONF = "--portaudio=no"

PACKAGES =+ "libjack jack-server jack-utils"

RDEPENDS_jack-dev_remove = "${PN} (= ${EXTENDPKGV})"

FILES_libjack = "${libdir}/*.so.* ${libdir}/jack/*.so"
FILES_jack-server = "${bindir}/jackd"
FILES_jack-utils = "${bindir}/*"
FILES_${PN}-doc += " ${datadir}/jack-audio-connection-kit/reference/html/* "
