SUMMARY = "A Python library for the Docker Engine API."
HOMEPAGE = "https://github.com/docker/docker-py"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34f3846f940453127309b920eeb89660"

inherit pypi setuptools3

SRC_URI[md5sum] = "7d917152976df075e6e90ee853df641f"
SRC_URI[sha256sum] = "b876e6909d8d2360e0540364c3a952a62847137f4674f2439320ede16d6db880"

DEPENDS += "${PYTHON_PN}-pip-native"

RDEPENDS_${PN} += " \
	python3-docker-pycreds \
	python3-requests \
	python3-websocket-client \
"
