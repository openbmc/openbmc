SUMMARY = "pimd is a lightweight stand-alone PIM-SM v2 multicast routing daemon."
HOMEPAGE = "http://troglobit.com/pimd.html"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94f108f91fab720d62425770b70dd790"

SRC_URI = "https://ftp.troglobit.com/pimd/${BP}.tar.gz \
           file://0001-configure-Dont-use-uname-to-determine-target-OS.patch \
           "
SRC_URI[sha256sum] = "c77a9812751f114490a28a6839b16aac8b020c8d9fd6aa22bf3880c054e19f1d"

EXTRA_OECONF:append:libc-musl = " --embedded-libc"

inherit autotools-brokensep update-alternatives

# The shipped Makefile "include config.mk" which is only created by configure,
# so the "make clean" run by autotools_preconfigure fails before configure has
# run. Skip that pre-configure clean.
CLEANBROKEN = "1"

do_configure() {
    oe_runconf
}

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "pimd"
ALTERNATIVE_LINK_NAME[pimd] = "${sbindir}/pimd"
