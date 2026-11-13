DESCRIPTION = "The leading native Python SSHv2 protocol library."
HOMEPAGE = "https://github.com/paramiko/paramiko/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd0120fc2e9f841c73ac707a30389af5"

SRC_URI[sha256sum] = "239b5f9b2a53006cd54f83cf8495ee742083708ead947882c8dd2786de79f39b"


inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

RDEPENDS:${PN} += " \
    python3-bcrypt \
    python3-cryptography \
    python3-logging \
    python3-pynacl \
    python3-unixadmin \
"

CVE_PRODUCT = "paramiko:paramiko python_software_foundation:paramiko"
