SUMMARY = "Pure-Python Java Keystore (JKS) library"
DESCRIPTION = "PyJKS enables Python projects to load and manipulate\
 Java KeyStore (JKS) data without a JVM dependency. PyJKS supports JKS,\
 JCEKS, BKS and UBER (BouncyCastle) keystores."
HOMEPAGE = "http://github.com/kurtbrose/pyjks"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9694d6cc724caf7f7386be88a4a81958"

SRC_URI[md5sum] = "d53501e7c80d465969742bc6bee91d49"
SRC_URI[sha256sum] = "709c44a303dd6b2464078ebeb677417e717818d4b3dc3844a462bef9d215dcc8"

inherit pypi

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-javaobj-py3 \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-pyasn1-modules \
    ${PYTHON_PN}-pycryptodome \
    ${PYTHON_PN}-twofish\
"

BBCLASSEXTEND = "native nativesdk"
