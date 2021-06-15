SUMMARY = "Big Buck Bunny movie - 720P"
LICENSE = "CC-BY-3.0"
# http://www.bigbuckbunny.org/index.php/about/
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/CC-BY-3.0;md5=dfa02b5755629022e267f10b9c0a2ab7"

SRC_URI = "https://archive.org/download/BigBuckBunny/big_buck_bunny_720p_surround.avi"
SRC_URI[md5sum] = "0da8fe124595f5b206d64cb1400bbefc"
SRC_URI[sha256sum] = "b957d6e6212638441b52d3b620af157cc8d40c2a0342669294854a06edcd528c"

inherit allarch

do_install() {
    install -d ${D}${datadir}/movies
    install -m 0644 ${WORKDIR}/big_buck_bunny_720p_surround.avi ${D}${datadir}/movies/
}

FILES_${PN} += "${datadir}/movies"
