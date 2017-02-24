SUMMARY = "A portable audio library"
SECTION = "libs/multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26107732c2ab637c5710446fcfaf02df"

PV = "v19+svnr1919"

SRC_URI = "http://www.portaudio.com/archives/pa_stable_v19_20140130.tgz \
           file://ldflags.patch"
SRC_URI[md5sum] = "7f220406902af9dca009668e198cbd23"
SRC_URI[sha256sum] = "8fe024a5f0681e112c6979808f684c3516061cc51d3acc0b726af98fc96c8d57"

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
