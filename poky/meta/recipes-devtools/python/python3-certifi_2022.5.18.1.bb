SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=67da0714c3f9471067b729eca6c9fbe8"

SRC_URI[sha256sum] = "9c5705e395cd70084351dd8ad5c41e65655e08ce46f2ec9cf6c2c08390f71eb7"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
