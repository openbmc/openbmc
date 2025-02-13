# NOTE: This recipe cannot have -cross- in the file name because it triggers
# the cross build detection in sstate which causes it to use the wrong
# architecture
require binutils.inc
require binutils-${PV}.inc

BPN = "binutils"

DEPENDS += "dejagnu-native expect-native"
DEPENDS += "binutils-native"

deltask do_compile
deltask do_install

inherit nopackages

do_configure[dirs] += "${B}/ld ${B}/bfd"
do_configure() {
    # create config.h, oe enables initfini-array by default
    echo "#define HAVE_INITFINI_ARRAY" > ${B}/ld/config.h
}

# target depends
DEPENDS += "virtual/cross-binutils"
DEPENDS += "virtual/cross-cc"
DEPENDS += "virtual/${MLPREFIX}compilerlibs"
DEPENDS += "virtual/${MLPREFIX}libc"

python check_prepare() {
    def suffix_sys(sys):
        if sys.endswith("-linux"):
            return sys + "-gnu"
        return sys

    def generate_site_exp(d, suite):
        content = []
        content.append('set srcdir "{0}/{1}"'.format(d.getVar("S"), suite))
        content.append('set objdir "{0}/{1}"'.format(d.getVar("B"), suite))
        content.append('set build_alias "{0}"'.format(d.getVar("BUILD_SYS")))
        content.append('set build_triplet {0}'.format(d.getVar("BUILD_SYS")))
        # use BUILD here since HOST=TARGET
        content.append('set host_alias "{0}"'.format(d.getVar("BUILD_SYS")))
        content.append('set host_triplet {0}'.format(d.getVar("BUILD_SYS")))
        content.append('set target_alias "{0}"'.format(d.getVar("TARGET_SYS")))
        content.append('set target_triplet {0}'.format(suffix_sys(d.getVar("TARGET_SYS"))))
        content.append("set development true")
        content.append("set experimental false")

        content.append(d.expand('set CXXFILT "${TARGET_PREFIX}c++filt"'))
        content.append(d.expand('set CC "${TARGET_PREFIX}gcc --sysroot=${STAGING_DIR_TARGET} ${TUNE_CCARGS}"'))
        content.append(d.expand('set CXX "${TARGET_PREFIX}g++ --sysroot=${STAGING_DIR_TARGET} ${TUNE_CCARGS}"'))
        content.append(d.expand('set CFLAGS_FOR_TARGET "--sysroot=${STAGING_DIR_TARGET} ${TUNE_CCARGS}"'))
        content.append(d.expand('set LD "${TARGET_PREFIX}ld ${TUNE_LDARGS}"'))
        content.append(d.expand('set LDFLAGS_FOR_TARGET "${TUNE_LDARGS}"'))

        if suite == "ld" and d.getVar("TUNE_ARCH") == "mips64":
            # oe patches binutils to have the default mips64 abi as 64bit, but
            # skips gas causing issues with the ld test suite (which uses gas)
            content.append('set ASFLAGS "-64"')

        return "\n".join(content)

    for i in ["binutils", "gas", "ld"]:
        builddir = os.path.join(d.getVar("B"), i)
        if not os.path.isdir(builddir):
            os.makedirs(builddir)
        with open(os.path.join(builddir, "site.exp"), "w") as f:
            f.write(generate_site_exp(d, i))
}

CHECK_TARGETS ??= "binutils gas ld"

do_check[dirs] = "${B} ${B}/binutils ${B}/gas ${B}/ld"
do_check[prefuncs] += "check_prepare"
do_check[nostamp] = "1"
do_check() {
    export LC_ALL=C
    for i in ${CHECK_TARGETS}; do
        (cd ${B}/$i; runtest \
            --tool $i \
            --srcdir ${S}/$i/testsuite \
            --ignore 'plugin.exp' \
            || true)
    done
}
addtask check after do_configure
