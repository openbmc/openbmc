SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6c492278d46e42af592aa26d7cab1e54"

SRC_URI[sha256sum] = "bbb37efbfc4c400fcd534b367b91fcd66d5da639aaa138034431a1c5e0a41451"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-numpy python3-pillow"
