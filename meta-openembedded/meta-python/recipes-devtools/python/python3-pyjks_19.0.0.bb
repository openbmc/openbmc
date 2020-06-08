SUMMARY = "Pure-Python Java Keystore (JKS) library"
DESCRIPTION = "PyJKS enables Python projects to load and manipulate\
 Java KeyStore (JKS) data without a JVM dependency. PyJKS supports JKS,\
 JCEKS, BKS and UBER (BouncyCastle) keystores."
HOMEPAGE = "http://github.com/kurtbrose/pyjks"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9694d6cc724caf7f7386be88a4a81958"

SRC_URI[md5sum] = "b6e8f3b91be355d15e3ba99de2cccf9b"
SRC_URI[sha256sum] = "9ce5b40289bcdffcd6a8e7118b333ba8ba2ec4c9ebc5e324885cc8a69767011a"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-javaobj-py3 \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-pyasn1-modules \
    ${PYTHON_PN}-pycryptodome \
    ${PYTHON_PN}-twofish\
"

BBCLASSEXTEND = "native nativesdk"
