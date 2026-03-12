SUMMARY = "A Fast, spec compliant Python 3.14+ tokenizer that runs on older Pythons."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=627dc9a75d5dcc4759b26bacf13a4c46"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "292052fe80923aae2260c073f822ceba21f3872ced9a68bb7953b348e561179a"

DEPENDS += "python3-mypy-native"
