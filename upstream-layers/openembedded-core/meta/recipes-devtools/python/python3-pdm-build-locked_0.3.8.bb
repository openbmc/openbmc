SUMMARY = "pdm plugin to publish locked dependencies as optional-dependencies"
HOMEPAGE = "https://github.com/pdm-project/pdm-build-locked"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19382cdf9c143df4f00b9caa0b60c75a"

SRC_URI[sha256sum] = "c2a608b288ed08618228880c2e6d6a616049c25eeb71786d06d1061d129764ba"

inherit pypi python_pdm

BBCLASSEXTEND += "native nativesdk"
