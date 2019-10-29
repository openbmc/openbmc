SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=d736cf16034030b4aab4ea179d91e778"

SRC_URI[md5sum] = "56f55188d8d5361834db0bc7120f8998"
SRC_URI[sha256sum] = "3eb500f8b9cde19e14472fcbee0195dbf0fbac006f8406a03f0cfb495dff20a0"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
