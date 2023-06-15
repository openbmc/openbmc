require sysstat.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a23a74b3f4caf9616230789d94217acb"

SRC_URI += "file://0001-configure.in-remove-check-for-chkconfig.patch \
           file://CVE-2022-39377.patch \
           file://CVE-2023-33204.patch \
           "
SRC_URI[sha256sum] = "ef445acea301bbb996e410842f6290a8d049e884d4868cfef7e85dc04b7eee5b"
