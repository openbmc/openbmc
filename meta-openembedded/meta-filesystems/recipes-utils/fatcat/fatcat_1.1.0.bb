SUMMARY = "FAT filesystems explore, extract, repair, and forensic tool"
DESCRIPTION = "This tool is designed to manipulate FAT filesystems, in order to \
explore, extract, repair, recover and forensic them. It currently supports \
FAT12, FAT16 and FAT32."
HOMEPAGE = "https://github.com/Gregwar/fatcat"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=57fbbfebd0dd1d6ff21b8cecb552a03f"

SRC_URI = "git://github.com/Gregwar/fatcat.git;branch=master;protocol=https \
           file://0001-Use-unistd.h-not-argp.h-for-all-POSIX-systems.patch \
           "

SRCREV = "b923172d97c578297964c062e3a92799e2a9eca4"

S = "${WORKDIR}/git"

inherit cmake
