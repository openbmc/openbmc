SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01831ddbaf398471da3cc87f5037e822"

SRC_URI[md5sum] = "85bbebd2c90d2f833c1db467d4d0e9a3"
SRC_URI[sha256sum] = "cbfca99bd594a10f674d0cd97a3d802a1fdef635d4361e1a2658de47ed261e3a"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
