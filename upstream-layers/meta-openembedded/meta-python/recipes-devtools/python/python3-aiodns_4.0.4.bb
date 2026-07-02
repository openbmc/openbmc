SUMMARY = "Simple DNS resolver for asyncio"
DESCRIPTION = "aiodns provides a simple way for doing asynchronous DNS resolutions using pycares."
HOMEPAGE = "https://github.com/saghul/aiodns"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d574ab425d1fcb37c9f1ad3961f18527"

SRC_URI[sha256sum] = "cb10e0c0d2591636716ad2fe402e977c16d71bdaf76bb8cb49e8a6633596f736"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-pycares-native"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pycares \
"

BBCLASSEXTEND = "native nativesdk"
