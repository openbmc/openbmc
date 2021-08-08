SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=59e4271a933d33edfe60237db377a14b"

SRC_URI[sha256sum] = "3642d483b558eec80d3c831e23953582c34d7e4540db86d9e5ed9dad238dabc6"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-logging \
	python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
