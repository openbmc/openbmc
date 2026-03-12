SUMMARY = "Expand system variables Unix style"
HOMEPAGE = "https://github.com/sayanarijit/expandvars"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b2e744064bd184728ac09dbfb52aaf4"

SRC_URI[sha256sum] = "6c5822b7b756a99a356b915dd1267f52ab8a4efaa135963bd7f4bd5d368f71d7"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
