DESCRIPTION = "ASGI is a standard for Python asynchronous web apps and servers to communicate with each other, and positioned as an asynchronous successor to WSGI."
HOMEPAGE = "https://pypi.org/project/asgiref/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI += "file://run-ptest \
	    "

SRC_URI[sha256sum] = "8de379fcc383bcfe4507e229fc31209ea23d4831c850f74063b2c11639474dd2"

export BUILD_SYS
export HOST_SYS

inherit pypi ptest setuptools3

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-pytest \
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-multiprocessing \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
