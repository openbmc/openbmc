SUMMARY = "Python library to build pretty command line user prompts. Easy to \
           use multi-select lists, confirmations, free text prompts."
HOMEPAGE = "https://github.com/tmbo/questionary"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19f0688967ec2b2624ee04c0136daae7"

inherit pypi python_poetry_core

SRC_URI[sha256sum] = "3d7e980292bb0107abaa79c68dd3eee3c561b83a0f89ae482860b181c8bd412d"

BBCLASSEXTEND = "native nativesdk"
