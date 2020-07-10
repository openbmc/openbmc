SUMMARY = "A pure-Python libconfig reader/writer with permissive license"
SECTION = "devel/python"
HOMEPAGE = "https://github.com/Grk0/python-libconf"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=05f696c084eaaf5d75bc150f70975758"

SRC_URI[md5sum] = "e212611cbf6a696e05742a983b3a0c57"
SRC_URI[sha256sum] = "2f907258953ba60a95a82d5633726b47c81f2d5cf8d8801b092579016d757f4a"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
