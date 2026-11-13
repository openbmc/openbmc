SUMMARY = "pytest plugin that allows you to add environment variables."
HOMEPAGE = "https://github.com/pytest-dev/pytest-env"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b70ef84b3d8d608b13b0287ed49df651"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

DEPENDS = "python3-hatch-vcs-native python3-hatchling-native"
SRC_URI[sha256sum] = "996f86ad78b6c7b5335e0d493f9e9ecd3e421174e056742856f91da439e1cbd9"

inherit pypi python_hatchling ptest-python-pytest

RDEPENDS:${PN} = "python3-pytest python3-tomli"

RDEPENDS:${PN}-ptest += " \
    python3-covdefaults \
    python3-coverage \
    python3-pytest-mock \
    python3-python-dotenv \
"
