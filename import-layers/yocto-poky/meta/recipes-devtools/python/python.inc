SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"
# bump this on every change in contrib/python/generate-manifest-2.7.py
INC_PR = "r1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=dd98d01d471fac8d8dbdd975229dba03"

SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz"

SRC_URI[md5sum] = "1dbcc848b4cd8399a8199d000f9f823c"
SRC_URI[sha256sum] = "962b4c45af50124ea61f11a30deb4342fc0bc21126790fa1d7f6c79809413f46"

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
