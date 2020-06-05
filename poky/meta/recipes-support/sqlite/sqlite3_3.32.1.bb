require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "http://www.sqlite.org/2020/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI[md5sum] = "bc7afc06f1e30b09ac930957af68d723"
SRC_URI[sha256sum] = "486748abfb16abd8af664e3a5f03b228e5f124682b0c942e157644bf6fff7d10"

# -19242 is only an issue in specific development branch commits
CVE_CHECK_WHITELIST += "CVE-2019-19242"
