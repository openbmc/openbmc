SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=f4ce1ae80a385140f0ddb2bcef410b06"

SRC_URI[md5sum] = "f3131ac2a277f3b70dbb6132ca8f8889"
SRC_URI[sha256sum] = "050cd30b810ddb3aa941e7ddfbe0d8065e793012d0a88cb5739ec23624b9895e"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
