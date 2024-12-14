SUMMARY = "Pure-Python Java Keystore (JKS) library"
DESCRIPTION = "PyJKS enables Python projects to load and manipulate\
 Java KeyStore (JKS) data without a JVM dependency. PyJKS supports JKS,\
 JCEKS, BKS and UBER (BouncyCastle) keystores."
HOMEPAGE = "http://github.com/kurtbrose/pyjks"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9694d6cc724caf7f7386be88a4a81958"

SRC_URI[sha256sum] = "0378cec15fb11b2ed27ba54dad9fd987d48e6f62f49fcff138f5f7a8b312b044"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-javaobj-py3 \
    python3-pyasn1 \
    python3-pyasn1-modules \
    python3-pycryptodomex \
    python3-twofish \
"

BBCLASSEXTEND = "native nativesdk"
