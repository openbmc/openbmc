HOMEPAGE = "https://github.com/peterjc/backports.lzma"
SUMMARY = "\
  Backport of Python 3.3's 'lzma' module for XZ/LZMA compressed files."
DESCRIPTION = "\
  This is a backport of the 'lzma' module included in Python 3.3 or later \
  by Nadeem Vawda and Per Oyvind Karlsen, which provides a Python wrapper \
  for XZ Utils (aka LZMA Utils v2) by Igor Pavlov. \
  . \
  In order to compile this, you will need to install XZ Utils from \
  http://tukaani.org/xz/ \
  "
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=db4345b3b9524aabc8fe8c65f235c6b2"

SRC_URI[md5sum] = "c3d109746aefa86268e500c07d7e8e0f"
SRC_URI[sha256sum] = "bac58aec8d39ac3d22250840fb24830d0e4a0ef05ad8f3f09172dc0cc80cdbca"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

DEPENDS += "xz"

SRCNAME = "backports.lzma"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI = "\
	https://pypi.python.org/packages/source/b/backports.lzma/${SRCNAME}-${PV}.tar.gz \
	file://fix_paths.patch \
	"
