SUMMARY = "Test recipe for recipeutils.patch_recipe()"

require recipeutils-test.inc

LICENSE = "HPND"
LIC_FILES_CHKSUM = "file://${WORKDIR}/somefile;md5=d41d8cd98f00b204e9800998ecf8427e"
DEPENDS += "zlib"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += "file://somefile"

SRC_URI:append = " file://anotherfile"
