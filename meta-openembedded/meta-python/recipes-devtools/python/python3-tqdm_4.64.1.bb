SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=1672e2674934fd93a31c09cf17f34100"

SRC_URI[sha256sum] = "5f4f682a004951c1b450bc753c710e9280c5746ce6ffedee253ddbcbf54cf1e4"

inherit pypi python_setuptools_build_meta

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-logging \
	python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
