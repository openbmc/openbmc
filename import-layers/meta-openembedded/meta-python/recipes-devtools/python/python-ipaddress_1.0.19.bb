SUMMARY = "Python 3.3+'s ipaddress for Python 2.6, 2.7, 3.2."
HOMEPAGE = "https://github.com/phihag/ipaddress"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

DEPENDS += "python-pip"

SRC_URI[md5sum] = "d0687efaf93a32476d81e90ba0609c57"
SRC_URI[sha256sum] = "200d8686011d470b5e4de207d803445deee427455cd0cb7c982b68cf82524f81"

inherit pypi setuptools

BBCLASSEXTEND = "native nativesdk"
