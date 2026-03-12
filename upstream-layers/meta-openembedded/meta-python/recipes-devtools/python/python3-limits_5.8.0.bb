SUMMARY = "Rate limiting utilities"
DESCRIPTION = "About Rate limiting using various strategies and \
storage backends such as redis, memcached & mongodb"
HOMEPAGE = "https://github.com/alisaifee/limits"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2455d5e574bc0fc489411ca45766ac78"

SRC_URI[sha256sum] = "c9e0d74aed837e8f6f50d1fcebcf5fd8130957287206bc3799adaee5092655da"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
	python3-deprecated \
	python3-packaging \
	python3-typing-extensions \
"
