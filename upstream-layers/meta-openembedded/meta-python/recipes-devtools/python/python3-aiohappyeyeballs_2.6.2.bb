SUMMARY = "Happy Eyeballs"
DESCRIPTION = "This library exists to allow connecting with Happy Eyeballs when you already have a list of addrinfo and not a DNS name."
HOMEPAGE = "https://github.com/aio-libs/aiohappyeyeballs"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcf6b249c2641540219a727f35d8d2c2"

SRC_URI[sha256sum] = "e202810ee718bd01fc6ef49e8ea53d023d5cb6b581076d7925aa499fa55dbe64"

inherit pypi python_poetry_core

BBCLASSEXTEND = "native nativesdk"

