SUMMARY = "Twisted Web Sockets"
HOMEPAGE = "https://github.com/MostAwesomeDude/txWS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76699830db7fa9e897f6a1ad05f98ec8"

DEPENDS = "python-twisted python-six python-vcversioner"

SRC_URI = "git://github.com/MostAwesomeDude/txWS.git"
SRCREV= "88cf6d9b9b685ffa1720644bd53c742afb10a414"

S = "${WORKDIR}/git"

inherit setuptools

