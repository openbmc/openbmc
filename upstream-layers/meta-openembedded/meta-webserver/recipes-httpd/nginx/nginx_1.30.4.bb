require nginx.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=79da1c70d587d3a199af9255ad393f99"

SRC_URI[sha256sum] = "4261dc90e9e47c1c4041276e9aaa3d48ebe2e664f728e14fa95ae6c67d57a08b"

inherit upstream-version-is-even

CVE_STATUS[CVE-2026-42055] = "fixed-version: Fixed since 1.30.3"
CVE_STATUS[CVE-2026-48142] = "fixed-version: Fixed since 1.30.3"
