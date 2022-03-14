SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "60285184cb02fdba5e1cc8605ac84e150a50f940e9383ab43564e5258d1f47bb"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
