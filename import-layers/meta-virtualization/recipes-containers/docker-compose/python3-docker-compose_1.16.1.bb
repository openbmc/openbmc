SUMMARY = "Multi-container orchestration for Docker"
HOMEPAGE = "https://www.docker.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435b266b3899aa8a959f17d41c56def8"

SRC_URI += "file://0001-Allow-newer-versions-of-requests.patch"

inherit pypi setuptools3

SRC_URI[md5sum] = "8dcadf09143600fcb573b43f446c8f9a"
SRC_URI[sha256sum] = "fb46a6a2c4d193a3ff1e4d7208eea920b629c81dc92257c87f3f93095cfb0bdf"

RDEPENDS_${PN} = "\
  ${PYTHON_PN}-cached-property \
  ${PYTHON_PN}-certifi \
  ${PYTHON_PN}-chardet \
  ${PYTHON_PN}-colorama \
  ${PYTHON_PN}-docker \
  ${PYTHON_PN}-docker-pycreds \
  ${PYTHON_PN}-dockerpty \
  ${PYTHON_PN}-docopt \
  ${PYTHON_PN}-idna \
  ${PYTHON_PN}-jsonschema \
  ${PYTHON_PN}-pyyaml \
  ${PYTHON_PN}-requests \
  ${PYTHON_PN}-six \
  ${PYTHON_PN}-terminal \
  ${PYTHON_PN}-texttable \
  ${PYTHON_PN}-urllib3 \
  ${PYTHON_PN}-websocket-client \
  "
