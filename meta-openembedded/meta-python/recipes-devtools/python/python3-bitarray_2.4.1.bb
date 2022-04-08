SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "faeca03f979e992cc76f7406af7eb9795cb111b8d8969c891a032bd7497c87da"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
