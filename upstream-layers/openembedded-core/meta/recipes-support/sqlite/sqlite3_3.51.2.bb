require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2026/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[sha256sum] = "fbd89f866b1403bb66a143065440089dd76100f2238314d92274a082d4f2b7bb"

SRC_URI += "file://0001-Add-option-to-disable-zlib.patch"
