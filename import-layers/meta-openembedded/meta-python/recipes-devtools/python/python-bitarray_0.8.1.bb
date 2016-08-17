SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ba469353f9f2a244b2075902351e37d"

SRC_URI[md5sum] = "3825184f54f4d93508a28031b4c65d3b"
SRC_URI[sha256sum] = "7da501356e48a83c61f479393681c1bc4b94e5a34ace7e08cb29e7dd9290ab18"

inherit pypi setuptools

BBCLASSEXTEND = "nativesdk"
