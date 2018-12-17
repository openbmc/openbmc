require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6dc95e63aa4d72502ff8193dfe2ddd38"

SRC_URI += "\
    file://not-check-libperl.patch \
    file://0001-Update-time-zone-data-files-to-tzdata-release-2018f.patch \
    file://0001-Sync-our-copy-of-the-timezone-library-with-IANA-rele.patch \
"

SRC_URI[md5sum] = "a5fe5fdff2d6c28f65601398be0950df"
SRC_URI[sha256sum] = "6c8e616c91a45142b85c0aeb1f29ebba4a361309e86469e0fb4617b6a73c4011"
