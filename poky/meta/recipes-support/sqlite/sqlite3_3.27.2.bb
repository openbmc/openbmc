require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

SRC_URI = "\
  http://www.sqlite.org/2019/sqlite-autoconf-${SQLITE_PV}.tar.gz \
  file://CVE-2019-9936.patch \
  file://CVE-2019-9937.patch \
  "

SRC_URI[md5sum] = "1f72631ce6e8efa5b4a6e55a43b3bdc0"
SRC_URI[sha256sum] = "50c39e85ea28b5ecfdb3f9e860afe9ba606381e21836b2849efca6a0bfe6ef6e"
