require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "\
  http://www.sqlite.org/2018/sqlite-autoconf-${SQLITE_PV}.tar.gz \
  "
SRC_URI[md5sum] = "99a51b40a66872872a91c92f6d0134fa"
SRC_URI[sha256sum] = "92842b283e5e744eff5da29ed3c69391de7368fccc4d0ee6bf62490ce555ef25"
