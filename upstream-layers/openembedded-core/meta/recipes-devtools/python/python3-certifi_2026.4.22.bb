SUMMARY = "Python package for providing Mozilla's CA Bundle."
DESCRIPTION = "This installable Python package contains a CA Bundle that you can reference in your \
Python code. This is useful for verifying HTTP requests, for example.  This is the same CA Bundle \
which ships with the Requests codebase, and is derived from Mozilla Firefox's canonical set."
HOMEPAGE = " http://certifi.io/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11618cb6a975948679286b1211bd573c"

SRC_URI[sha256sum] = "8d455352a37b71bf76a79caa83a3d6c25afee4a385d632127b6afb3963f1c580"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-io"
