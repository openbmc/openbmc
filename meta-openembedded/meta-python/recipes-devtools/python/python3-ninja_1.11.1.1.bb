SUMMARY = "Ninja is a small build system with a focus on speed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_Apache_20;md5=19cbd64715b51267a47bf3750cc6a8a5"

PYPI_PACKAGE = "ninja"
PYPI_ARCHIVE_NAME_PREFIX = "pypi-"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "9d793b08dd857e38d0b6ffe9e6b7145d7c485a42dcfea04905ca0cdb6017cc3c"

SRC_URI += "file://no-scikit-build.patch \
            file://run-ninja-from-path.patch"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

do_install:append () {
	rm -rf ${D}${bindir}
}

RDEPENDS:${PN} = " \
    ninja \
    python3-io \
    python3-json \
    python3-ninja-syntax \
"

BBCLASSEXTEND = "native nativesdk"
