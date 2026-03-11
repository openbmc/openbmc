DESCRIPTION = "QR Code and Micro QR Code generator for Python 2 and Python 3"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8e8db3765a57bcb968140e0a353c1a35"

SRC_URI[sha256sum] = "983424b296e62189d70fc73460cd946cf56dcbe82b9bda18c066fc1b24371cdc"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"
