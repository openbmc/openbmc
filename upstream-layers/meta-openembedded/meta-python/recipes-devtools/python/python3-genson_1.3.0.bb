DESCRIPTION = "GenSON is a powerful, user-friendly JSON Schema generator built in Python."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6c30d55dbfb3a28d420d918534adf6b"

SRC_URI[sha256sum] = "e02db9ac2e3fd29e65b5286f7135762e2cd8a986537c075b06fc5f1517308e37"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
