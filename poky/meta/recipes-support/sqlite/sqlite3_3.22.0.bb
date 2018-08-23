require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "\
  http://www.sqlite.org/2018/sqlite-autoconf-${SQLITE_PV}.tar.gz \
  file://CVE-2018-8740.patch \
  "
SRC_URI[md5sum] = "96b5648d542e8afa6ab7ffb8db8ddc3d"
SRC_URI[sha256sum] = "2824ab1238b706bc66127320afbdffb096361130e23291f26928a027b885c612"
