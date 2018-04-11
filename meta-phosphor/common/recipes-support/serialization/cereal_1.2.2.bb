SUMMARY = "Cereal - A C++11 library for serialization"
DESCRIPTION = "Cereal is a header-only C++11 serialization library."
HOMEPAGE = "https://github.com/USCiLab/cereal"
PR = "r1"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e612690af2f575dfd02e2e91443cea23"

SRC_URI += "git://github.com/USCiLab/cereal"
SRCREV = "51cbda5f30e56c801c07fe3d3aba5d7fb9e6cca4"
PV = "1.2.2+git${SRCPV}"

S = "${WORKDIR}/git"

ALLOW_EMPTY_${PN} = "1"

do_install () {
    install -d ${D}${includedir}/cereal
    cp -r ${S}/include/cereal/* ${D}${includedir}/cereal/
}
