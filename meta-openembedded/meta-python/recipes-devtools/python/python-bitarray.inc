SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=00f0d12ccaf4e6b0b4fe501663621fa3"

SRC_URI[md5sum] = "743d396a1012d6f7d791d8a526da302d"
SRC_URI[sha256sum] = "9f578314c7808eb1416620dc7d7977d4787a65a4f61b4c9685343a860712615b"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
