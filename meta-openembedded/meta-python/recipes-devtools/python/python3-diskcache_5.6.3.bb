DESCRIPTION = "Disk Cache -- Disk and file backed persistent cache."
HOMEPAGE = "http://www.grantjenks.com/docs/diskcache/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c60ef82f0f40155453f6d5f2c94b6e8e"

SRC_URI[sha256sum] = "2c3a3fa2743d8535d832ec61c2054a1641f41775aa7c556758a109941e33e4fc"

PYPI_PACKAGE = "diskcache"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    python3-json \
    python3-pickle \
    python3-sqlite3 \
    python3-core \
    python3-io \
    python3-compression \
    python3-threading \
"

CLEANBROKEN = "1"
