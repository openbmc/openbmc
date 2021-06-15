SUMMARY = "Strict, simple, lightweight RFC3339 function.s"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f0e2cd40e05189ec81232da84bd6e1a"

SRC_URI[md5sum] = "4d9b635b4df885bc37bc1189d66c9abc"
SRC_URI[sha256sum] = "5cad17bedfc3af57b399db0fed32771f18fc54bbd917e85546088607ac5e1277"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
