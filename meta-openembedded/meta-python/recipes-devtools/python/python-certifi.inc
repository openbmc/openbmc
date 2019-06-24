SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f77f61d14ee6feac4228d3ebd26cc1f1"

SRC_URI[md5sum] = "af8fea5f3df6f7f81e9c6cbc6dd7c1e8"
SRC_URI[sha256sum] = "945e3ba63a0b9f577b1395204e13c3a231f9bc0223888be653286534e5873695"

BBCLASSEXTEND = "native nativesdk"
