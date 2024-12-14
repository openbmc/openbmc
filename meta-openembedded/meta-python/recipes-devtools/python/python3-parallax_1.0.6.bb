SUMMARY = "Execute commands and copy files over SSH to multiple machines at once."
HOMEPAGE = "https://github.com/krig/parallax/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=52c67ffa6102f288a0347f8c5802fd18"

SRC_URI[sha256sum] = "c16703202ff67aed4740c0727df304abe9f3e7851e653533b24de21b338d9081"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-fcntl python3-threading python3-unixadmin"

BBCLASSEXTEND = "native nativesdk"
