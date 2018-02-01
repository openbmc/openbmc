SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f77f61d14ee6feac4228d3ebd26cc1f1"

SRC_URI[md5sum] = "b72521a8badff5e89a8eabea586d79ab"
SRC_URI[sha256sum] = "81877fb7ac126e9215dfb15bfef7115fdc30e798e0013065158eed0707fd99ce"

BBCLASSEXTEND = "native nativesdk"
