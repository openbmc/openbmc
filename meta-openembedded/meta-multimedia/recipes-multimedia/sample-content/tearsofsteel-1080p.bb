SUMMARY = "Tears of Steel movie - 1080P"
LICENSE = "CC-BY-3.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/CC-BY-3.0;md5=dfa02b5755629022e267f10b9c0a2ab7"

SRC_URI = "http://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"
SRC_URI[md5sum] = "e3fee55b1779c553e37b1d3988e6fad6"
SRC_URI[sha256sum] = "bd2b5bc6c16d4085034f47ef7e4b3938afe86b4eec4ac3cf2685367d3b0b23b0"

inherit allarch

do_install() {
    install -d ${D}${datadir}/movies
    install -m 0644 ${WORKDIR}/ToS-4k-1920.mov ${D}${datadir}/movies/
}

FILES_${PN} += "${datadir}/movies"
