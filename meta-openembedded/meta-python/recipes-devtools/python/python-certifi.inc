SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f77f61d14ee6feac4228d3ebd26cc1f1"

SRC_URI[md5sum] = "a84571995a209dcd00a1702a74302b88"
SRC_URI[sha256sum] = "4c1d68a1408dd090d2f3a869aa94c3947cc1d967821d1ed303208c9f41f0f2f4"

BBCLASSEXTEND = "native nativesdk"
