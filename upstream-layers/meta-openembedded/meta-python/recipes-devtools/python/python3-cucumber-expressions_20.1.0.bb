SUMMARY = "Cucumber Expressions - a simpler alternative to Regular Expressions"
DESCRIPTION = "Provides the cucumber-expression parser and matcher used by cucumber/behave"
HOMEPAGE = "https://github.com/cucumber/cucumber-expressions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b2bb1048cefa23c375b8ce5f13313ea"

SRC_URI[sha256sum] = "0d216ec26e36c71b3e5643f2e72c41f9b266ef04eaa0c7e47a6e3b2caf523b1a"

inherit pypi python_uv_build

PYPI_PACKAGE = "cucumber_expressions"

RDEPENDS:${PN} += " \
    python3-core \
    python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
