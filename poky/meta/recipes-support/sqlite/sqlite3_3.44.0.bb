require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2023/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[sha256sum] = "b9cd386e7cd22af6e0d2a0f06d0404951e1bef109e42ea06cc0450e10cd15550"

CVE_STATUS[CVE-2023-36191] = "disputed: The error is a bug. It has been fixed upstream. But it is not a vulnerability"

