SUMMARY = "Invoke py.test as distutils command with dependency resolution"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a33f38bbf47d48c70fe0d40e5f77498e"

SRC_URI[md5sum] = "a52d65bd3dbc88bed751cf934dc41db3"
SRC_URI[sha256sum] = "25a013c8d84f0ca60bb01bd11913a3bcab420f601f0f236de4423074af656e7a"

inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
