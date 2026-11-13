SUMMARY = "a little task queue for python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cac039fcc82f01141cc170b48f315d4"


SRC_URI[sha256sum] = "328f5a2dd1406621e5edb17d45655ae232ad9f32c0795ca8dc0bfc121dd7b30c"

RDEPENDS:${PN} += " \
	python3-datetime \
	python3-logging \
	python3-multiprocessing \
	python3-json \
"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

