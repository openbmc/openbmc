require sqlite3.inc

LICENSE = "blessing"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2026/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[sha256sum] = "c917d7db16648ec95f714974ace5e5dcf46b7dc70e26600a0a102a3141125db0"

SRC_URI += "file://0001-Add-option-to-disable-zlib.patch"
