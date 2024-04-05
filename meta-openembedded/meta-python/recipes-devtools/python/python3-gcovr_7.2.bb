DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c5a58ca91c1758a53f896ba89d8aaac2"

SRC_URI = "git://github.com/gcovr/gcovr.git;branch=main;protocol=https"
SRCREV = "c4b74b0859611283be646d590c7915e787911b6f"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-jinja2 python3-lxml python3-setuptools python3-pygments python3-multiprocessing"

BBCLASSEXTEND = "native nativesdk"
