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

SRC_URI = "git://github.com/jackaudio/jack2.git;branch=master;protocol=https \
           file://0001-example-clients-Use-c-compiler-for-jack_simdtests.patch \
          "
SRCREV = "3e6ca6b734b69e2f5dfb85935068e15006fb5acf"

S = "${WORKDIR}/git"

inherit waf pkgconfig

PACKAGECONFIG ??= "alsa"
PACKAGECONFIG[alsa] = "--alsa=yes,--alsa=no,alsa-lib"
# --dbus only stops building jackd -> add --classic
PACKAGECONFIG[dbus] = "--dbus --classic,,dbus"
PACKAGECONFIG[opus] = "--opus=yes,--opus=no,libopus"

# portaudio is for windows builds only
EXTRA_OECONF = "--portaudio=no"

do_install:append() {
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'dbus', True, False, d)}; then
		rm -f ${D}${bindir}/jack_control
	fi
}

PACKAGES =+ "libjack jack-server jack-utils"

RDEPENDS:jack-dev:remove = "${PN} (= ${EXTENDPKGV})"

FILES:libjack = "${libdir}/*.so.* ${libdir}/jack/*.so"
FILES:jack-server = " \
    ${datadir}/dbus-1/services \
    ${bindir}/jackdbus \
    ${bindir}/jackd \
"
FILES:jack-utils = "${bindir}/*"

FILES:${PN}-doc += " ${datadir}/jack-audio-connection-kit/reference/html/*"
