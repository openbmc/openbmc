DESCRIPTION = "XMODEM protocol implementation"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90bc9522130d68de0dcbf33707bbf124"

SRC_URI[sha256sum] = "2f1068aa8676f0d1d112498b5786c4f8ea4f89d8f25d07d3a0f293cd21db1c35"

inherit pypi setuptools3

do_install:append() {
    install -d ${D}${docdir}/${PN}
    mv ${D}${prefix}/doc/* ${D}${docdir}/${PN}/
    rmdir ${D}${prefix}/doc
}

RDEPENDS:${PN} += " \
    python3-logging \
"
BBCLASSEXTEND = "native nativesdk"
