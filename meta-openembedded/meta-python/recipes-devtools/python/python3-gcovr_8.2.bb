DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b307623752f2e1189349885b95b326e5"

SRC_URI = "git://github.com/gcovr/gcovr.git;branch=main;protocol=https"
SRCREV = "045fb8d51806bd7f9e3df4e4edffa3bb816cf77f"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += " \
    python3-colorlog \
    python3-jinja2 \
    python3-lxml \
    python3-multiprocessing \
    python3-pygments \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
