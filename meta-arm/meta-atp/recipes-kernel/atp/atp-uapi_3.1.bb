require recipes-devtools/atp/atp-source_3.1.inc
inherit package

SUMMARY = "User API for accessing services from ATP kernel modules"
SECTION = "kernel/userland"

S = "${WORKDIR}/git"
SRC_URI = "${ATP_SRC}"

# Unversioned library
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

EXTRA_OEMAKE += "-C linux/uapi"

do_compile() {
    oe_runmake KERNEL_HDR_PATH=${STAGING_INCDIR}
}

do_install() {
    oe_runmake DESTDIR=${D} PREFIX=${prefix} install
}

DEPENDS = "linux-libc-headers kernel-module-atp"
