DESCRIPTION = "Python module for Semantic Versioning"
HOMEPAGE = "https://github.com/k-bx/python-semver"
BUGTRACKER = "https://github.com/k-bx/python-semver/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[md5sum] = "dc579ba9d0bb2137bad5324d4bdb7e40"
SRC_URI[sha256sum] = "5b09010a66d9a3837211bb7ae5a20d10ba88f8cb49e92cb139a69ef90d5060d8"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
