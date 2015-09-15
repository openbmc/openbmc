DESCRIPTION = "nose extends the test loading and running features of unittest, \
making it easier to write, find and run tests."
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://lgpl.txt;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI = "http://pypi.python.org/packages/source/n/nose/nose-${PV}.tar.gz"

SRC_URI[md5sum] = "0ca546d81ca8309080fc80cb389e7a16"
SRC_URI[sha256sum] = "f61e0909a743eed37b1207e38a8e7b4a2fe0a82185e36f2be252ef1b3f901758"

S = "${WORKDIR}/nose-${PV}"

inherit setuptools

BBCLASSEXTEND = "native nativesdk"
