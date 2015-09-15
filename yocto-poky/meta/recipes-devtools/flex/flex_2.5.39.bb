require flex.inc
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067"
BBCLASSEXTEND = "native nativesdk"

SRC_URI += "file://do_not_create_pdf_doc.patch"

SRC_URI[md5sum] = "77d44c6bb8c0705e0017ab9a84a1502b"
SRC_URI[sha256sum] = "add2b55f3bc38cb512b48fad7d72f43b11ef244487ff25fc00aabec1e32b617f"
