DESCRIPTION = "Python data structure and operations for intervals"
HOMEPAGE = "https://github.com/AlexandreDecan/portion"
SECTION = "devel/python"

LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

inherit pypi setuptools3

SRC_URI[sha256sum] = "58b2792e6e9837a2d55a97d459b940c90fb08b5fba774f524e9359727c80e7b4"

BBCLASSEXTEND = "native"
