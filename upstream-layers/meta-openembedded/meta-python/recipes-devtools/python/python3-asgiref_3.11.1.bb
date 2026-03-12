DESCRIPTION = "ASGI is a standard for Python asynchronous web apps and servers to communicate with each other, and positioned as an asynchronous successor to WSGI."
HOMEPAGE = "https://pypi.org/project/asgiref/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI[sha256sum] = "5f184dc43b7e763efe848065441eac62229c9f7b0475f41f80e207a114eda4ce"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest-python-pytest setuptools3

RDEPENDS:${PN}-ptest += " \
    python3-asyncio \
    python3-io \
    python3-multiprocessing \
    python3-pytest-asyncio \
"

BBCLASSEXTEND = "native nativesdk"
