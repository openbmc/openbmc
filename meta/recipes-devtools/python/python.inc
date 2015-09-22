SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"
# bump this on every change in contrib/python/generate-manifest-2.7.py
INC_PR = "r1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=dff3d00f049545862992d2d097831a13"

SRC_URI = "http://www.python.org/ftp/python/${PV}/Python-${PV}.tar.xz"

SRC_URI[md5sum] = "38d530f7efc373d64a8fb1637e3baaa7"
SRC_URI[sha256sum] = "90d27e14ea7e03570026850e2e50ba71ad20b7eb31035aada1cf3def8f8d4916"

PYTHON_MAJMIN = "2.7"

inherit autotools

PYTHONLSBOPTS_linuxstdbase = "ac_cv_sizeof_off_t=8"

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
