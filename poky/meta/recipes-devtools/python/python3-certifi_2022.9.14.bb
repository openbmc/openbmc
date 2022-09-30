SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3c2b7404369c587c3559afb604fce2f2"

SRC_URI[sha256sum] = "36973885b9542e6bd01dea287b2b4b3b21236307c56324fcc3f1160f2d655ed5"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
