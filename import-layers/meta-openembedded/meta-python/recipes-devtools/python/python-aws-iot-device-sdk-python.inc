DESCRIPTION = "SDK for connecting to AWS IoT using Python."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9ac49901b833e769c7d6f21e8dbd7b30"

SRC_URI[md5sum] = "e6781f6c42bbc547d508d6fa6720ac8f"
SRC_URI[sha256sum] = "6b2865d4f518794b96a834fc81b568428daa0ef92037da2e9288b4af5d8dd736"

inherit pypi

PYPI_PACKAGE = "AWSIoTPythonSDK"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"
