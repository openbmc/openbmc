SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://pypi.org/project/six/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e50fab7e9501f3dfc920eafeb24b46ea"

inherit pypi

RDEPENDS:${PN} = "python3-io"

BBCLASSEXTEND = "native nativesdk"
