require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2023/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[sha256sum] = "39116c94e76630f22d54cd82c3cea308565f1715f716d1b2527f1c9c969ba4d9"

CVE_STATUS[CVE-2023-36191] = "disputed: The error is a bug. It has been fixed upstream. But it is not a vulnerability"

