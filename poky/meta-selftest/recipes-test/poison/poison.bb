SUMMARY = "Sysroot poisoning test"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

inherit nopackages

# This test confirms that compiling code that searches /usr/include for headers
# will result in compiler errors.  This recipe should will fail to build and
# oe-selftest has a test that verifies that.
do_compile() {
    touch empty.c
    ${CPP} ${CFLAGS} -I/usr/include empty.c
}

EXCLUDE_FROM_WORLD = "1"
