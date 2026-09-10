SUMMARY = "a little task queue for python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cac039fcc82f01141cc170b48f315d4"


SRC_URI[sha256sum] = "6de196c6ece2e38b5173f7510600091ab035c0e86b0958d0e5b38a82b9984666"

RDEPENDS:${PN} += " \
	python3-datetime \
	python3-logging \
	python3-multiprocessing \
	python3-json \
"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

