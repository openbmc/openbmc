DESCRIPTION = "Python data structure and operations for intervals"
HOMEPAGE = "https://github.com/AlexandreDecan/portion"
SECTION = "devel/python"

LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05f1e16a8e59ce3e9a979e881816c2ab"

inherit pypi setuptools3

SRC_URI[sha256sum] = "725b65da806fb79df05c0b6383b01631c510f371d9bc0ece93a996b4260ba085"

BBCLASSEXTEND = "native"
