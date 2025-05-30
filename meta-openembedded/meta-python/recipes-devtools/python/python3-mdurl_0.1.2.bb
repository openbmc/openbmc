SUMMARY = "Markdown URL utilities"
DESCRIPTION = "URL utilities for markdown-it (a Python port)"
HOMEPAGE = "https://github.com/executablebooks/mdurl"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aca1dc6b9088f1dda81c89cad2c77ad1"

SRC_URI[sha256sum] = "bb413d29f5eea38f31dd4754dd7377d4465116fb207585f97bf925588687c1ba"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
