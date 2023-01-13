SUMMARY = "Python wrapper around rapidjson"
HOMEPAGE = "https://github.com/python-rapidjson/python-rapidjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4daf3929156304df67003c33274a98bd"

SRC_URI = "git://github.com/python-rapidjson/python-rapidjson.git;protocol=https;branch=master"
SRCREV = "8f4ab8e197ca30c03726b675ae7cce6ac9d6622e"

S = "${WORKDIR}/git"

inherit setuptools3

SETUPTOOLS_BUILD_ARGS += " --rj-include-dir=${RECIPE_SYSROOT}${includedir}"

DEPENDS += " \
    rapidjson \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-core \
"
