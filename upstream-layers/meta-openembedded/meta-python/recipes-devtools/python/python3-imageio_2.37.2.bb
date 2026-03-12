SUMMARY = "Python library that provides an easy interface to read and \
write a wide range of image data, including animated images, video, \
volumetric data, and scientific formats."
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9da78aa88ef5e9acd41f7bb288286273"

SRC_URI[sha256sum] = "0212ef2727ac9caa5ca4b2c75ae89454312f440a756fcfc8ef1993e718f50f8a"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "python3-numpy python3-pillow"
