SUMMARY = "Twisted Web Sockets"
HOMEPAGE = "https://github.com/MostAwesomeDude/txWS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76699830db7fa9e897f6a1ad05f98ec8"

DEPENDS = "python3-twisted python3-six python3-vcversioner python3-six-native python3-vcversioner-native"

SRC_URI = "git://github.com/MostAwesomeDude/txWS.git;branch=master;protocol=https"
SRCREV= "88cf6d9b9b685ffa1720644bd53c742afb10a414"

S = "${WORKDIR}/git"

inherit setuptools3

