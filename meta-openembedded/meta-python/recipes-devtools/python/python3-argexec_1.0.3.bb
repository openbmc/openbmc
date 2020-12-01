SUMMARY = "Expose your Python functions to the command line with one easy step!"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea70b07c354e36056bd35e17c9c3face"

inherit pypi setuptools3

SRC_URI[md5sum] = "448635948823309312ea9f70b30b6c2d"
SRC_URI[sha256sum] = "61f9ae9322e38ae64996848421afbdb018239a99c4e796fe064f172d6c98c3bf"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS_${PN} += "\
  python3-dynamic-dispatch \
  python3-typeguard \
"

BBCLASSEXTEND = "native nativesdk"
