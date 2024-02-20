SUMMARY = "A portable audio library"
SECTION = "libs/multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=26107732c2ab637c5710446fcfaf02df"

PV .= "+git"

SRC_URI = "git://github.com/PortAudio/portaudio.git;branch=master;protocol=https"
SRCREV = "929e2e8f7af281c5eb4fa07758930d542ec43d97"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

PACKAGECONFIG ??= "alsa jack"
PACKAGECONFIG[alsa] = ",,alsa-lib"
PACKAGECONFIG[jack] = ",,jack"
PACKAGECONFIG[examples] = "-DPA_BUILD_EXAMPLES=ON,-DPA_BUILD_EXAMPLES=OFF"

do_install:append() {
    if [ -d ${B}/examples ]; then
        install -d ${D}${bindir}
        for example in ${B}/examples/pa*; do
            install -m755 $example ${D}${bindir}/
        done
    fi
}
