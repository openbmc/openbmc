SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=d331e1d54f5e78247388f5416c2ab928"

SRC_URI[md5sum] = "81c48852b0bc538ac3ed25fae020360c"
SRC_URI[sha256sum] = "27a69ffcee3b868abab3ce8b17c69e02b63e722d4d64ffd91d659f81e9984954"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
