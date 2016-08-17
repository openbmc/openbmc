HOMEPAGE = "http://python-requests.org"
SUMMARY = "Python HTTP for Humans."
DESCRIPTION = "\
  Requests is an Apache2 Licensed HTTP library, written in Python, \
  for human beings. \
  .      \
  Most existing Python modules for sending HTTP requests are extremely \
  verbose and cumbersome. Python's builtin urllib2 module provides most \
  of the HTTP capabilities you should need, but the api is thoroughly \
  broken.  It requires an enormous amount of work (even method overrides) \
  to perform the simplest of tasks. \
  .      \
  Things shouldn't be this way. Not in Python \
  "
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=58c7e163c9f8ee037246da101c6afd1e"

SRCNAME = "requests"

SRC_URI = "http://pypi.python.org/packages/source/r/requests/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "a27ea3d72d7822906ddce5e252d6add9"
SRC_URI[sha256sum] = "84fe8d5bf4dcdcc49002446c47a146d17ac10facf00d9086659064ac43b6c25b"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools
