HOMEPAGE = "https://github.com/boto/boto"
SUMMARY = "Amazon Web Services API"
DESCRIPTION = "\
  Boto is a Python package that provides interfaces to Amazon Web Services. \
  Currently, all features work with Python 2.6 and 2.7. Work is under way to \
  support Python 3.3+ in the same codebase. Modules are being ported one at \
  a time with the help of the open source community, so please check below \
  for compatibility with Python 3.3+. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;md5=182ef81236d3fac2c6ed8e8d3c988ec8"

PR = "r0"
SRCNAME = "boto"

SRC_URI = "https://pypi.python.org/packages/source/b/boto/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "5556223d2d0cc4d06dd4829e671dcecd"
SRC_URI[sha256sum] = "33baab022ecb803414ad0d6cf4041d010cfc2755ff8acc3bea7b32e77ba98be0"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

