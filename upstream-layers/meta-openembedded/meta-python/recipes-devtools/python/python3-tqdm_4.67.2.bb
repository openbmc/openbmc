SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "https://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=9a9bed097dea538bf341c8623c8f8852"

SRC_URI[sha256sum] = "649aac53964b2cb8dec76a14b405a4c0d13612cb8933aae547dd144eacc99653"

CVE_PRODUCT = "tqdm"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-logging \
	python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
