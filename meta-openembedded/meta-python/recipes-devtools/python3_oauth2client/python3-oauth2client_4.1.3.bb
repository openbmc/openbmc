DESCRIPTION = "OAuth 2.0 client library"

HOMEPAGE = "http://github.com/google/oauth2client/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=038e1390e94fe637991fa5569daa62bc"

inherit pypi

SRC_URI[sha256sum] = "d486741e451287f69568a4d26d70d9acd73a2bbfa275746c535b4209891cccc6"

RDEPENDS:${PN} += " \
        python3-httplib2 \
        python3-pyasn1 \
        python3-pyasn1-modules \
        python3-rsa \
        python3-six \
        "

inherit setuptools3
