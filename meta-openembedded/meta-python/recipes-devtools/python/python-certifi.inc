SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f77f61d14ee6feac4228d3ebd26cc1f1"

SRC_URI[md5sum] = "76381d19d0a1171fecb2d1002b81424e"
SRC_URI[sha256sum] = "b26104d6835d1f5e49452a26eb2ff87fe7090b89dfcaee5ea2212697e1e1d7ae"

BBCLASSEXTEND = "native nativesdk"
