SUMMARY = "Platform detection for use by libraries like Adafruit-Blinka."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Python_PlatformDetect"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fccd531dce4b989c05173925f0bbb76c"

SRC_URI[sha256sum] = "74552d1afcf779a84ca63527a1eaee9995c429ff422db15492bad0c3599cab4b"

PYPI_PACKAGE = "adafruit_platformdetect"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "python3-core"
