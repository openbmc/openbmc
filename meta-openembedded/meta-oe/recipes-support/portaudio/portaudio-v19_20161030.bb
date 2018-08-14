SUMMARY = "A portable audio library"
SECTION = "libs/multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26107732c2ab637c5710446fcfaf02df"

PV = "v190600"

SRC_URI = "http://www.portaudio.com/archives/pa_stable_v190600_20161030.tgz \
           file://ldflags.patch"
SRC_URI[md5sum] = "4df8224e047529ca9ad42f0521bf81a8"
SRC_URI[sha256sum] = "f5a21d7dcd6ee84397446fa1fa1a0675bb2e8a4a6dceb4305a8404698d8d1513"

S = "${WORKDIR}/portaudio"

inherit autotools pkgconfig

PACKAGECONFIG ??= "alsa jack"
PACKAGECONFIG[alsa] = "--with-alsa, --without-alsa, alsa-lib,"
PACKAGECONFIG[jack] = "--with-jack, --without-jack, jack,"

EXTRA_OECONF = "--without-oss --without-asihpi"

do_install_append() {
    mkdir --parents ${D}${bindir}
    for b in ${B}/bin/pa*; do
        # Bit nasty, should always work
        ${B}/*-libtool --mode install install $b ${D}${bindir}
    done
}

PACKAGES += "portaudio-examples"
FILES_portaudio-examples = "${bindir}"
