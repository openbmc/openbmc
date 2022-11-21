SUMMARY = "The extensible, standards compliant build backend used by Hatch"
HOMEPAGE = "https://hatch.pypa.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbe2fd33fc9297692812fc94b7d27fd9"

inherit pypi python_hatchling

DEPENDS += "python3-pluggy-native python3-tomli-native python3-pathspec-native python3-packaging-native python3-editables-native"
DEPENDS:remove:class-native = "python3-hatchling-native"

SRC_URI[sha256sum] = "9f84361f70cf3a7ab9543b0c3ecc64211ed2ba8a606a71eb6a473c1c9b08e1d0"

do_compile:prepend() {
    export PYTHONPATH=src
}

BBCLASSEXTEND = "native nativesdk"
