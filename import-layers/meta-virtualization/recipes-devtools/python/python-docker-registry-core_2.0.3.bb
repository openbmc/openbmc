HOMEPAGE = "https://pypi.python.org/pypi/docker-registry-core"
SUMMARY = "Docker registry core package"
DESCRIPTION = "core package for docker-registry (drivers) developers"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCNAME = "docker-registry-core"

SRC_URI = "https://pypi.python.org/packages/source/d/docker-registry-core/${SRCNAME}-${PV}.tar.gz"

S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI[md5sum] = "610ef9395f2e9a2f91c68d13325fce7b"
SRC_URI[sha256sum] = "347e804f1f35b28dbe27bf8d7a0b630fca29d684032139bf26e3940572360360"

inherit setuptools

DEPENDS += "\
	python-distribute \
	python-boto (= 2.34.0) \
	python-redis (= 2.10.3) \
	python-simplejson (= 3.6.2) \
	"

# boto 2.34.0
# redis 2.10.3
# simplejson 3.6.2
# setuptools 5.8
