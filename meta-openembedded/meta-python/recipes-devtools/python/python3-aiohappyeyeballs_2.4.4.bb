SUMMARY = "Happy Eyeballs"
DESCRIPTION = "This library exists to allow connecting with Happy Eyeballs when you already have a list of addrinfo and not a DNS name."
HOMEPAGE = "https://github.com/aio-libs/aiohappyeyeballs"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcf6b249c2641540219a727f35d8d2c2"

SRC_URI[sha256sum] = "5fdd7d87889c63183afc18ce9271f9b0a7d32c2303e394468dd45d514a757745"

inherit pypi python_poetry_core

BBCLASSEXTEND = "native nativesdk"

