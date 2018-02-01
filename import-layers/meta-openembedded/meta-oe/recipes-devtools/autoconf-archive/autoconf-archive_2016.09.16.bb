require autoconf-archive.inc


PARALLEL_MAKE = ""

LICENSE = "GPLv2 & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "bf19d4cddce260b3c3e1d51d42509071"
SRC_URI[sha256sum] = "e8f2efd235f842bad2f6938bf4a72240a5e5fcd248e8444335e63beb60fabd82"

SRC_URI += "\
        file://delete-some-m4-files.patch \
"

EXTRA_OECONF += "ac_cv_path_M4=m4"
BBCLASSEXTEND = "native nativesdk"
