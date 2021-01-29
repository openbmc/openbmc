SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f77f61d14ee6feac4228d3ebd26cc1f1"

SRC_URI[sha256sum] = "1a4995114262bffbc2413b159f2a1a480c969de6e6eb13ee966d470af86af59c"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
