SUMMARY = "The extensible, standards compliant build backend used by Hatch"
HOMEPAGE = "https://hatch.pypa.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbe2fd33fc9297692812fc94b7d27fd9"

inherit pypi python_hatchling

DEPENDS += "python3-packaging-native python3-pathspec-native python3-pluggy-native python3-tomlkit-native python3-trove-classifiers-native"

SRC_URI[sha256sum] = "c4468f73144c054d2aab4ef0f0378c43b9878bf07f8ffd6b79690e970d375f07"

BBCLASSEXTEND = "native nativesdk"
