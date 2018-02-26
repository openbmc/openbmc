require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "\
  http://www.sqlite.org/2017/sqlite-autoconf-${SQLITE_PV}.tar.gz \
  file://sqlite3-fix-CVE-2017-13685.patch \
  "
SRC_URI[md5sum] = "e262a28b73cc330e7e83520c8ce14e4d"
SRC_URI[sha256sum] = "3814c6f629ff93968b2b37a70497cfe98b366bf587a2261a56a5f750af6ae6a0"
