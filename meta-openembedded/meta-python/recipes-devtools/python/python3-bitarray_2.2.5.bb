SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "efb2dc83f0acb832a94af3687eea558d72512cf2e54a64fca56a10aacf57934c"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
