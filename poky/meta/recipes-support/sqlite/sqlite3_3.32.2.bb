require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2020/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[md5sum] = "eb498918a33159cdf8104997aad29e83"
SRC_URI[sha256sum] = "2dbef1254c1dbeeb5d13d7722d37e633f18ccbba689806b0a65b68701a5b6084"

# -19242 is only an issue in specific development branch commits
CVE_CHECK_WHITELIST += "CVE-2019-19242"
