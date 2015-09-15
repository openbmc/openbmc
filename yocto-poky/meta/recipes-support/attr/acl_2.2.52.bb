require acl.inc

SRC_URI += "file://add-missing-configure.ac.patch"

SRC_URI[md5sum] = "a61415312426e9c2212bd7dc7929abda"
SRC_URI[sha256sum] = "179074bb0580c06c4b4137be4c5a92a701583277967acdb5546043c7874e0d23"


BBCLASSEXTEND = "native nativesdk"
