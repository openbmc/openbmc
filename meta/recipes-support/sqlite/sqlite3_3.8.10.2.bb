require sqlite3.inc

LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=65f0a57ca6928710b418c094b3570bb0"

def sqlite_download_version(d):
    pvsplit = d.getVar('PV', True).split('.')
    return pvsplit[0] + ''.join([part.rjust(2,'0') for part in pvsplit[1:]])

PE = "3"
SQLITE_PV = "${@sqlite_download_version(d)}"
SRC_URI = "http://www.sqlite.org/2015/sqlite-autoconf-${SQLITE_PV}.tar.gz \
           file://0001-using-the-dynamic-library.patch \
"

SRC_URI[md5sum] = "a18bfc015cd49a1e7a961b7b77bc3b37"
SRC_URI[sha256sum] = "8382e55a4e7d853c93038562ca3dd00307937fccf1c6b65ddd813e503a56d626"

S = "${WORKDIR}/sqlite-autoconf-${SQLITE_PV}"

# Provide column meta-data API
BUILD_CFLAGS += "-DSQLITE_ENABLE_COLUMN_METADATA"
TARGET_CFLAGS += "-DSQLITE_ENABLE_COLUMN_METADATA"

