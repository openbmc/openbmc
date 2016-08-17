SUMMARY = "Test vectors for the cryptography package."
SECTION = "devel/python"
LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c3617db4fb6fae01f1d253ab91511e4"
DEPENDS = "python-cryptography"
SRCNAME = "cryptography_vectors"

SRC_URI = "https://pypi.python.org/packages/source/c/cryptography-vectors/${SRCNAME}-${PV}.tar.gz"
S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI[md5sum] = "0ad422501074929c06b7edd40df41844"
SRC_URI[sha256sum] = "a929fbb0eac391c93c5745451a4d4157a8bc18eb2e69faf3af1d825ceacbf32c"

inherit setuptools
