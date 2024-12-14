DESCRIPTION = "jackdmp is a C++ version of the JACK low-latency audio \
server for multi-processor machines. It is a new implementation of the \
JACK server core features that aims in removing some limitations of \
the JACK1 design. The activation system has been changed for a data \
flow model and lock-free programming techniques for graph access have \
been used to have a more dynamic and robust system."
SECTION = "libs/multimedia"

LICENSE = "GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://common/JackControlAPI.cpp;beginline=5;endline=19;md5=9d1921199e203163f160313243f853d6 \
    file://common/JackControlAPI.h;beginline=4;endline=18;md5=9d1921199e203163f160313243f853d6 \
    file://common/jack/jack.h;beginline=2;endline=17;md5=0a668d22ce661159cad28d1c3b8e66af \
    file://common/JackServer.h;beginline=2;endline=17;md5=9bf0870727804a994ee2d19fd368d940 \
"

DEPENDS = "libsamplerate0 libsndfile1"

SRC_URI = "git://github.com/jackaudio/jack2.git;branch=master;protocol=https \
    file://0001-Conceal-imp-warnings-in-Python3.patch \
    file://0002-Fix-all-DeprecationWarning-invalid-escape-sequence.patch \
"
SRCREV = "4f58969432339a250ce87fe855fb962c67d00ddb"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

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

PACKAGES =+ "libjack jack-server"

RDEPENDS:jack-dev:remove = "${PN} (= ${EXTENDPKGV})"

FILES:libjack = "${libdir}/*.so.* ${libdir}/jack/*.so"
FILES:jack-server = " \
    ${datadir}/dbus-1/services \
    ${bindir}/jackdbus \
    ${bindir}/jackd \
"

FILES:${PN}-doc += " ${datadir}/jack-audio-connection-kit/reference/html/*"

