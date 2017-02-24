SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = " http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=22b38951eb857cf285a4560a914b7cd6"

SRC_URI[md5sum] = "a3316cf3842ed0375ba5931914239d97"
SRC_URI[sha256sum] = "c823de61ff40d1996fe087cec343e0503881ca641b897e0f9b86c7683a0bfee1"

inherit pypi setuptools

RDEPENDS_${PN} = "\
    python-core \
    python-datetime \
"
