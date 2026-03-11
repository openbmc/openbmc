EXCLUDE_FROM_WORLD = "1"

# handle PN differences
FILESEXTRAPATHS:prepend := "${THISDIR}/glibc:"

# setup depends
INHIBIT_DEFAULT_DEPS = ""

python () {
    libc = d.getVar("PREFERRED_PROVIDER_virtual/libc")
    libclocale = d.getVar("PREFERRED_PROVIDER_virtual/libc-locale")
    if libc != "glibc" or libclocale != "glibc-locale":
        raise bb.parse.SkipRecipe("glibc-testsuite requires that virtual/libc is glibc")
}

DEPENDS += "glibc-locale libgcc gcc-runtime"

# remove the initial depends
DEPENDS:remove = "libgcc-initial"

do_check[dirs] += "${B}"
do_check () {
    # clean out previous test results
    oe_runmake tests-clean
    # makefiles don't clean entirely (and also sometimes fails due to too many args)
    find ${B} -type f -name "*.out" -delete
    find ${B} -type f -name "*.test-result" -delete
    find ${B}/catgets -name "*.cat" -delete
    find ${B}/conform -name "symlist-*" -delete
    [ ! -e ${B}/timezone/testdata ] || rm -rf ${B}/timezone/testdata
}
addtask do_check after do_compile
