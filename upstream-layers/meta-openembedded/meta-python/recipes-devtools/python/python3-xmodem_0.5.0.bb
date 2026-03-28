DESCRIPTION = "XMODEM protocol implementation"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90bc9522130d68de0dcbf33707bbf124"

SRC_URI[sha256sum] = "a1a818f31c29412f1cab0cd69deccd7be77bc1feb516723af990d00161f6fb6a"

inherit pypi python_setuptools_build_meta

do_install:append() {
    install -d ${D}${docdir}/${PN}
    mv ${D}${prefix}/doc/* ${D}${docdir}/${PN}/
    rmdir ${D}${prefix}/doc
}

RDEPENDS:${PN} += " \
    python3-logging \
"
BBCLASSEXTEND = "native nativesdk"
