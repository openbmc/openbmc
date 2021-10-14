SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=1672e2674934fd93a31c09cf17f34100"

SRC_URI[sha256sum] = "d359de7217506c9851b7869f3708d8ee53ed70a1b8edbba4dbcb47442592920d"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-logging \
	python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
