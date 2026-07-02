require sqlite3.inc

LICENSE = "blessing"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2026/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[sha256sum] = "588ad51949419a56ebe81fe56193d510c559eb94c9a57748387860b5d3069316"

SRC_URI += "file://0001-Add-option-to-disable-zlib.patch"
