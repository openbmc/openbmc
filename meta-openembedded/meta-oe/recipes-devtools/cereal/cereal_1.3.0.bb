SUMMARY = "cereal is a header-only C++11 serialization library"
HOMEPAGE = "https://uscilab.github.io/cereal"
LICENSE = "BSD & MIT & BSL-1.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=e612690af2f575dfd02e2e91443cea23 \
    file://include/cereal/external/rapidxml/license.txt;md5=d63ab70ba21ca0544b03284958324301 \
    file://include/cereal/external/LICENSE;md5=b07578c9df99c0b8b45eb041efd4a645 \
    file://include/cereal/external/rapidjson/LICENSE;md5=e7abb663111d4ac17cf00323698aff08 \
    file://include/cereal/external/rapidjson/msinttypes/LICENSE;md5=dffce65b98c773976de2e338bd130f46 \
"

inherit cmake pkgconfig

RDEPENDS_${PN}-dev = ""

SRC_URI = " \
    git://github.com/USCiLab/cereal.git;protocol=https \
    file://0001-add-license-files-for-components-of-cereal.patch \
"

SRCREV = "02eace19a99ce3cd564ca4e379753d69af08c2c8"

EXTRA_OECMAKE += "\
    -DCEREAL_INSTALL=TRUE \
    -DJUST_INSTALL_CEREAL=TRUE \
"

S = "${WORKDIR}/git"

PROVIDES += "${PN}-dev"

FILES_${PN}-dev = " \
    ${includedir} \
    ${libdir} \
    ${datadir}/cmake \
"
