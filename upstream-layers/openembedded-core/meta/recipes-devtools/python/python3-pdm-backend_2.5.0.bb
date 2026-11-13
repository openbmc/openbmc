SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "7953b994563d3151755e3364b9d0cfe817ed0eaecdf27c8f777f412d26bcd98a"

inherit pypi python_pep517

BBCLASSEXTEND = "native nativesdk"
