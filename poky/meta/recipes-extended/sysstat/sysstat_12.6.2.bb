require sysstat.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a23a74b3f4caf9616230789d94217acb"

SRC_URI += "file://0001-configure.in-remove-check-for-chkconfig.patch \
            file://CVE-2023-33204.patch \
            "

SRC_URI[sha256sum] = "3e77134aedaa6fc57d9745da67edfd8990e19adee71ac47196229261c563fb48"
