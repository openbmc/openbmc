DESCRIPTION = "Library to implement a well-behaved Unix daemon process"
HOMEPAGE = "https://pagure.io/python-daemon/"
SECTION = "devel/python"

DEPENDS += "python3-docutils-native python3-changelog-chug-native"
RDEPENDS:${PN} = "python3-docutils \
                  python3-lockfile (>= 0.10) \
                  python3-resource \
"

LICENSE = "Apache-2.0 & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=55f76b1b31719284caf4bc3ecbb70d6f"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f7b04335adc473de877f5117e26d5f1142f4c9f7cd765408f0877757be5afbf4"

PYPI_PACKAGE = "python_daemon"
