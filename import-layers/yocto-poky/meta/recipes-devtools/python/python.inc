SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"
# bump this on every change in contrib/python/generate-manifest-2.7.py
INC_PR = "r1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=6b60258130e4ed10d3101517eb5b9385"

SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz"

SRC_URI[md5sum] = "57dffcee9cee8bb2ab5f82af1d8e9a69"
SRC_URI[sha256sum] = "d7837121dd5652a05fef807c361909d255d173280c4e1a4ded94d73d80a1f978"

# python recipe is actually python 2.x
# also, exclude pre-releases for both python 2.x and 3.x
UPSTREAM_CHECK_REGEX = "[Pp]ython-(?P<pver>2(\.\d+)+).tar"

PYTHON_MAJMIN = "2.7"

inherit autotools

EXTRA_OECONF = "\
  --with-threads \
  --with-pymalloc \
  --without-cxx-main \
  --with-signal-module \
  --enable-shared \
  --enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)} \
  ac_cv_header_bluetooth_bluetooth_h=no ac_cv_header_bluetooth_h=no \
  ${PYTHONLSBOPTS} \
"
