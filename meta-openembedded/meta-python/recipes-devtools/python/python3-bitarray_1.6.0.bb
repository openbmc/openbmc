SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[md5sum] = "08ddac722b139c1544087c4953a6335b"
SRC_URI[sha256sum] = "ba157ddebddc723fe021fc80595b3c70924d69ee58286b62bfca21da48edfc9d"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
