SUMMARY = "Python 3.3+'s ipaddress for Python 2.6, 2.7, 3.2."
HOMEPAGE = "https://github.com/phihag/ipaddress"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

DEPENDS += "python-pip"

SRC_URI[md5sum] = "74c1ce3109f30eaa1ab3dd342e7b76d4"
SRC_URI[sha256sum] = "b146c751ea45cad6188dd6cf2d9b757f6f4f8d6ffb96a023e6f2e26eea02a72c"

inherit pypi setuptools

BBCLASSEXTEND = "native nativesdk"
