SUMMARY = "Calculate the distance between 2 points on Earth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=20a52d2c688975e989fcbee3e6c8d1a1"

SRC_URI[sha256sum] = "1103d7e1f0f108c25b31b63452c54d9d6f29389a70de7dd75fd4b908329b6fcf"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-numpy"

BBCLASSEXTEND = "native nativesdk"
