DESCRIPTION = "The leading native Python SSHv2 protocol library."
HOMEPAGE = "https://github.com/paramiko/paramiko/"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd0120fc2e9f841c73ac707a30389af5"

SRC_URI[md5sum] = "bf8239dc820ca86dd3c3226f4281c35f"
SRC_URI[sha256sum] = "920492895db8013f6cc0179293147f830b8c7b21fdfc839b6bad760c27459d9f"

PYPI_PACKAGE = "paramiko"

inherit pypi setuptools3

CLEANBROKEN = "1"

