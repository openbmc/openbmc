SUMMARY = "The extensible, standards compliant build backend used by Hatch"
HOMEPAGE = "https://hatch.pypa.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbe2fd33fc9297692812fc94b7d27fd9"

inherit pypi python_hatchling

DEPENDS += "python3-pluggy-native python3-tomli-native python3-pathspec-native python3-packaging-native python3-editables-native"
DEPENDS:remove:class-native = "python3-hatchling-native"

SRC_URI[sha256sum] = "bd6e8505de511ac4217ff50927f6d1845494608e401e63a62b830c31fb613544"

do_compile:prepend() {
    export PYTHONPATH=src
}

BBCLASSEXTEND = "native nativesdk"
