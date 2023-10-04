SUMMARY = "A low-level interface to a growing number of Amazon Web Services."
HOMEPAGE = "https://github.com/boto/botocore"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "301436174635bec739b225b840fc365ca00e5c1a63e5b2a19ee679d204e01b78"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-jmespath python3-dateutil python3-logging"
