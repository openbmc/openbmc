SUMMARY = "Python 3.3+'s ipaddress for Python 2.6, 2.7, 3.2."
HOMEPAGE = "https://github.com/phihag/ipaddress"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

SRC_URI[sha256sum] = "b7f8e0369580bb4a24d5ba1d7cc29660a4a6987763faf1d8a8046830e020e7e2"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
