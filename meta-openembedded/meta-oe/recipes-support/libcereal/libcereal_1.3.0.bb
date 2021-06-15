SUMMARY = "A C++11 library for serialization"
HOMEPAGE = "https://uscilab.github.io/cereal/"

SECTION = "libs"

LICENSE = "BSD-3-Clause & MIT & BSL-1.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=e612690af2f575dfd02e2e91443cea23 \
    file://include/cereal/external/rapidxml/license.txt;md5=d63ab70ba21ca0544b03284958324301 \
    file://include/cereal/external/LICENSE;md5=b07578c9df99c0b8b45eb041efd4a645 \
    file://include/cereal/external/rapidjson/LICENSE;md5=e7abb663111d4ac17cf00323698aff08 \
    file://include/cereal/external/rapidjson/msinttypes/LICENSE;md5=dffce65b98c773976de2e338bd130f46 \
"

PROVIDES += "${PN}-dev"

PV .= "+git${SRCPV}"
SRCREV = "64f50dbd5cecdaba785217e2b0aeea3a4f1cdfab"
SRC_URI = "git://github.com/USCiLab/cereal.git"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DJUST_INSTALL_CEREAL=ON"

ALLOW_EMPTY_${PN} = "1"

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
