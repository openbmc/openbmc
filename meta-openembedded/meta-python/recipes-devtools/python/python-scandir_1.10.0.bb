SUMMARY = "A better directory iterator and faster os.walk()"
HOMEPAGE = "https://github.com/benhoyt/scandir"
AUTHOR = "Ben Hoyt"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=482ee62da51806409d432a80eed4e3ea"

SRC_URI = "git://github.com/benhoyt/scandir.git"
SRCREV = "982e6ba60e7165ef965567eacd7138149c9ce292"

S = "${WORKDIR}/git"

inherit setuptools

BBCLASSEXTEND = "native nativesdk"