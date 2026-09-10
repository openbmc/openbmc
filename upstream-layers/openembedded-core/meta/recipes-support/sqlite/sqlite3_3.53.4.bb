require sqlite3.inc

LICENSE = "blessing"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2026/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[sha256sum] = "0e9483900e92cd5de8fd48d16bf9200145a61f7fd5be542a5ac81d8a9516eb9c"

SRC_URI += "file://0001-Add-option-to-disable-zlib.patch"
