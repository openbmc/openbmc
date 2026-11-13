SUMMARY = "A collection of framework independent HTTP protocol utils."
HOMEPAGE = "https://github.com/MagicStack/httptools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0a2d82955bf3facdf04cb882655e840e"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI += "file://0001-pyproject.toml-relax-setuptools-upper-bound.patch"
SRC_URI[sha256sum] = "d484ebb7e3a3f3597b0f645fbd1b85633674ca808c1f5ba11c2caf7c66f5c8b6"
