require nginx.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=79da1c70d587d3a199af9255ad393f99"

SRC_URI[sha256sum] = "3cf46f5ed7504d460125866b7bea99fb357f31d887e2e63b35503310e9b8b6c8"

inherit upstream-version-is-even

CVE_STATUS[CVE-2026-95101] = "fixed-version: Fixed since 1.30.6"
CVE_STATUS[CVE-2026-97629] = "fixed-version: Fixed since 1.30.6"

# Remove following entries when upgrading recipe to 1.32
CVE_STATUS[CVE-2026-42055] = "fixed-version: Fixed since 1.30.3"
CVE_STATUS[CVE-2026-48142] = "fixed-version: Fixed since 1.30.3"
CVE_STATUS[CVE-2026-90439] = "fixed-version: Fixed since 1.30.5"
