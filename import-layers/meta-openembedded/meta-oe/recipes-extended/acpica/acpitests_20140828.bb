SUMMARY = "Test suite used to validate ACPICA"
HOMEPAGE = "http://www.acpica.org/"

LICENSE = "Intel"
LIC_FILES_CHKSUM = "file://tests/aapits/atexec.c;beginline=1;endline=115;md5=e92bcdfcd01d117d1bda3e814bb2030a"

DEPENDS = "bison flex"

SRC_URI = "https://acpica.org/sites/acpica/files/acpitests-unix-${PV}.tar.gz;name=acpitests \
           https://acpica.org/sites/acpica/files/acpica-unix2-${PV}.tar.gz;name=acpica \
           file://aapits-linux.patch \
           file://aapits-makefile.patch \
"
SRC_URI[acpitests.md5sum] = "db9d6fdaa8e3eb101d700ee5ba4938ed"
SRC_URI[acpitests.sha256sum] = "e576c74bf1bf1c9f7348bf9419e05c8acfece7105abcdc052e66670c7af2cf00"
SRC_URI[acpica.md5sum] = "6f05f0d10166a1b1ff6107f3d1cdf1e5"
SRC_URI[acpica.sha256sum] = "01d8867656c5ba41dec307c4383ce676196ad4281ac2c9dec9f5be5fac6d888e"

S = "${WORKDIR}/acpitests-unix-${PV}"

EXTRA_OEMAKE = "'CC=${TARGET_PREFIX}gcc ${HOST_CC_ARCH} ${TOOLCHAIN_OPTIONS}' 'OPT_CFLAGS=-Wall'"

# The Makefiles expect a specific layout
do_compile() {
    cp -af ${WORKDIR}/acpica-unix2-${PV}/source ${S}
    cd tests/aapits
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}
    install -m0755 tests/aapits/bin/aapits ${D}${bindir}
}

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"
