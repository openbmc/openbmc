DESCRIPTION = "One version package to rule them all, One version package to find them, One version package to bring them all, and in the darkness bind them."
HOMEPAGE = "https://pypi.org/project/awesomeversion/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=92622b5a8e216099be741d78328bae5d"

SRC_URI[sha256sum] = "aee7ccbaed6f8d84e0f0364080c7734a0166d77ea6ccfcc4900b38917f1efc71"

RDEPENDS:${PN} += "python3-profile python3-logging"

inherit pypi python_poetry_core
