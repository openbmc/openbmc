SUMMARY = "Big Buck Bunny movie - 1080P"
LICENSE = "CC-BY-3.0"
# http://www.bigbuckbunny.org/index.php/about/
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/CC-BY-3.0;md5=dfa02b5755629022e267f10b9c0a2ab7"

SRC_URI = "http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_surround.avi"
SRC_URI[md5sum] = "223991c8b33564eb77988a4c13c1c76a"
SRC_URI[sha256sum] = "69fe2cfe7154a6e752688e3a0d7d6b07b1605bbaf75b56f6470dc7b4c20c06ea"

inherit allarch

do_install() {
    install -d ${D}${datadir}/movies
    install -m 0644 ${WORKDIR}/big_buck_bunny_1080p_surround.avi ${D}${datadir}/movies/
}

FILES_${PN} += "${datadir}/movies"
