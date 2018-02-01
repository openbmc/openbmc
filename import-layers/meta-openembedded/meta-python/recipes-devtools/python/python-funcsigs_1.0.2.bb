SUMMARY = "Python function signatures from PEP362 for Python 2.6, 2.7 and 3.2+"
DESCRPTION = " \
funcsigs is a backport of the `PEP 362`_ function signature features from \
Python 3.3's `inspect`_ module. The backport is compatible with Python 2.6, \
2.7 as well as 3.3 and up. 3.2 was supported by version 0.4, but with \
setuptools and pip no longer supporting 3.2, we cannot make any statement \
about 3.2 compatibility. \
"

HOMEPAGE = "http://funcsigs.readthedocs.org"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6bc91dc8e5793892189fe7481a2d354"

SRC_URI[md5sum] = "7e583285b1fb8a76305d6d68f4ccc14e"
SRC_URI[sha256sum] = "a7bb0f2cf3a3fd1ab2732cb49eba4252c2af4240442415b4abce3b87022a8f50"

inherit pypi setuptools
