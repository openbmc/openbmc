SUMMARY = "Happy Eyeballs"
DESCRIPTION = "This library exists to allow connecting with Happy Eyeballs when you already have a list of addrinfo and not a DNS name."
HOMEPAGE = "https://github.com/aio-libs/aiohappyeyeballs"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcf6b249c2641540219a727f35d8d2c2"

SRC_URI[sha256sum] = "9b05052f9042985d32ecbe4b59a77ae19c006a78f1344d7fdad69d28ded3d0b0"

inherit pypi python_poetry_core

BBCLASSEXTEND = "native nativesdk"

