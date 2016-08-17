DESCRIPTION = "JACK is a low-latency audio server. It can \
connect a number of different applications to an audio \
device, as well as allowing them to share audio between \
themselves."
SECTION = "libs/multimedia"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=c030468238b2cf89925a57f2780396a7 \
                    file://COPYING.GPL;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LGPL;md5=e77fe93202736b47c07035910f47974a \
"

DEPENDS = "alsa-lib libsamplerate0 readline"

SRCREV = "91a688c602b7ce1711d8ad10fb07ebcf2e530a64"
SRC_URI = "git://github.com/jackaudio/jack1.git \
           file://remove-wrong-host-test.patch \
           file://jack_fix_TWL4030_alsa_capture.patch \
"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-timestamps \
                --disable-capabilities \
                --disable-oldtrans \
                --disable-portaudio \
                --disable-coreaudio \
                --disable-oss \
                --enable-alsa"

EXTRA_OEMAKE = 'transform="s,^,,"'

PACKAGES =+ "libjack jack-server jack-utils"

FILES_libjack = "${libdir}/*.so.* ${libdir}/jack/*.so"
FILES_jack-server = "${bindir}/jackd"
FILES_jack-utils = "${bindir}/*"
FILES_${PN}-doc += " ${datadir}/jack-audio-connection-kit/reference/html/* "
