SUMMARY = "The extensible, standards compliant build backend used by Hatch"
HOMEPAGE = "https://hatch.pypa.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbe2fd33fc9297692812fc94b7d27fd9"

inherit pypi python_hatchling

DEPENDS += "python3-packaging-native python3-pathspec-native python3-pluggy-native python3-tomlkit-native python3-trove-classifiers-native"

SRC_URI[sha256sum] = "0bdbde4a52b06c37e3eca395f85a762bf0ef06fe374fd8ae429dc6be10230f5f"

BBCLASSEXTEND = "native nativesdk"
