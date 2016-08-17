HOMEPAGE = "https://pypi.python.org/pypi/Flask-Cors/1.10.3"
SUMMARY = "A Flask extension adding a decorator for CORS support"
DESCRIPTION = "\
  A Flask extension for handling Cross Origin Resource Sharing (CORS), making cross-origin AJAX possible \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4784781a5ee9fed9c50272e733e07685"

DEPENDS += "python-six python-flask"

PR = "r0"
SRCNAME = "Flask-Cors"

SRC_URI = "https://pypi.python.org/packages/source/F/Flask-Cors/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "4f3c75ace0f724d1de167bd73745c965"
SRC_URI[sha256sum] = "9e6927aa0a46f314bca0ec63eb871cee898a162adfdd5b65224db7a008287423"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
