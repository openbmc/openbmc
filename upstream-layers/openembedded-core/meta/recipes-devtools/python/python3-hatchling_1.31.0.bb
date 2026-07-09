SUMMARY = "The extensible, standards compliant build backend used by Hatch"
HOMEPAGE = "https://hatch.pypa.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbe2fd33fc9297692812fc94b7d27fd9"

inherit pypi python_hatchling

DEPENDS += "python3-pluggy-native python3-pathspec-native python3-packaging-native python3-editables-native python3-trove-classifiers-native"

SRC_URI[sha256sum] = "6b48ad4068a482ed7239b3a8215bc55b47aad3345d58dfc94e553c5d2d46211b"

BBCLASSEXTEND = "native nativesdk"
