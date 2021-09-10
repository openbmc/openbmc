SUMMARY = "CryFS encrypts your files, so you can safely store them anywhere."
HOMEDIR = "https://www.cryfs.org"

LICENSE = "LGPL-3.0"
FILE_CHK_SUM = "file://;md5=12345"

SRC_URI = "https://github.com/${BPN}/${BPN}.git"
SRCREV = "0f83a1ab7e5ca9f37f97bc57b20d3fab0f351d11"

inherit cmake
