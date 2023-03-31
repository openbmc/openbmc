SUMMARY = "Python wrapper around rapidjson"
HOMEPAGE = "https://github.com/python-rapidjson/python-rapidjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4daf3929156304df67003c33274a98bd"

SRC_URI = "git://github.com/python-rapidjson/python-rapidjson.git;protocol=https;branch=master"
SRCREV = "e9e209553a65db3568471f32392f54549c8a9816"

S = "${WORKDIR}/git"

inherit setuptools3

SETUPTOOLS_BUILD_ARGS += " --rj-include-dir=${RECIPE_SYSROOT}${includedir}"

DEPENDS += " \
    rapidjson \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-core \
"
