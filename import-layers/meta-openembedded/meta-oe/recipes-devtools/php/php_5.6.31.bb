require php.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=b602636d46a61c0ac0432bbf5c078fe4"

SRC_URI += "file://change-AC_TRY_RUN-to-AC_TRY_LINK.patch \
            file://pthread-check-threads-m4.patch \
            file://0001-Add-lpthread-to-link.patch \
           "
SRC_URI[md5sum] = "620abe25e0d6cd5f041a360a30ce5783"
SRC_URI[sha256sum] = "8f397169cb65f0539f3bcb04060f97770d73e19074a37bd2c58b98ebf6ecb10f"
