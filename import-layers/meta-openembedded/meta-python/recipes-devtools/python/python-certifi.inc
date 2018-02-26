SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f77f61d14ee6feac4228d3ebd26cc1f1"

SRC_URI[md5sum] = "48e8370da8b370a16e223ee9c7b6b063"
SRC_URI[sha256sum] = "40523d2efb60523e113b44602298f0960e900388cf3bb6043f645cf57ea9e3f5"

BBCLASSEXTEND = "native nativesdk"
