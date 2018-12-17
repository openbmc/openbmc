SUMMARY = "Invoke py.test as distutils command with dependency resolution"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a33f38bbf47d48c70fe0d40e5f77498e"

SRC_URI[md5sum] = "4b696b0f35767f0694f183d0a1855300"
SRC_URI[sha256sum] = "d23f117be39919f00dd91bffeb4f15e031ec797501b717a245e377aee0f577be"

inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
