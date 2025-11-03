SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20a52d2c688975e989fcbee3e6c8d1a1"

SRC_URI[sha256sum] = "ab750caa0c8f2168bd7b00a429757a83a8393be1aa30f91c2becf6b523189e2a"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
