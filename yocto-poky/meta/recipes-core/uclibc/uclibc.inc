SUMMARY = "C library for embedded systems"
DESCRIPTION = "The name uClibc is an abbreviation for 'the \
microcontroller C library'. For simplicity, uClibc is pronounced \
'yew-see-lib-see'.  The goal of uClibc is to provide as much \
functionality as possible in a small amount of space, and it is intended \
primarily for embedded use. It is also highly configurable in supported \
features, at the cost of ABI differences for different configurations. \
uClibc has been designed from the ground up to be a C library for \
embedded Linux.  It is NOT compatible with binaries linked against glibc."

LICENSE = "LGPLv2.1+"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
		    file://COPYING.LIB.boilerplate;md5=aaddeadcddeb918297e0e4afc52ce46f \
file://${S}/test/regex/testregex.c;beginline=1;endline=31;md5=234efb227d0a40677f895e4a1e26e960"

require uclibc-config.inc

PATH_prepend = "${STAGING_BINDIR_TOOLCHAIN}.${STAGINGCC}:"
TOOLCHAIN_OPTIONS = " --sysroot=${STAGING_DIR_TCBOOTSTRAP}"

# siteconfig.bbclass runs configure which needs a working compiler
# For the compiler to work we need a working libc yet libc isn't
# in the sysroots directory at this point. This means the libc.so
# linker script won't work as the --sysroot setting isn't correct.
# Here we create a hacked up libc linker script and pass in the right
# flags to let configure work. Ugly.
EXTRASITECONFIG = "CFLAGS='${CFLAGS} -Wl,-L${WORKDIR}/site_config_libc -L${WORKDIR}/site_config_libc -L${SYSROOT_DESTDIR}${libdir} -L${SYSROOT_DESTDIR}${base_libdir} -Wl,-L${SYSROOT_DESTDIR}${libdir} -Wl,-L${SYSROOT_DESTDIR}${base_libdir}'"
siteconfig_do_siteconfig_gencache_prepend = " \
mkdir -p ${WORKDIR}/site_config_libc; \
cp ${SYSROOT_DESTDIR}${libdir}/libc.so ${WORKDIR}/site_config_libc; \
sed -i -e 's# ${base_libdir}# ${SYSROOT_DESTDIR}${base_libdir}#g' -e 's# ${libdir}# ${SYSROOT_DESTDIR}${libdir}#g' ${WORKDIR}/site_config_libc/libc.so; \
"

# For now, we will skip building of a gcc package if it is a uclibc one
# and our build is not a uclibc one, and we skip a glibc one if our build
# is a uclibc build.
COMPATIBLE_HOST = ".*-uclibc.*"

INHIBIT_DEFAULT_DEPS = "1"

# There appears to be a parallel race in uclibc:
# GEN /extra/locale/locale_collate.h
# xargs: ../..//extra/locale/gen_collate: Text file busy
# make[1]: *** [../..//extra/locale/locale_collate.h] Error 126
PARALLEL_MAKE = ""

# do_stage barfs on a CC with whitespace, therefore put the 'HOST_CC_ARCH' in
# the CFLAGS (when building the utils).
OEMAKE_NO_CC = "'STRIPTOOL=true' 'LD=${LD}'"
EXTRA_OEMAKE = "${OEMAKE_NO_CC} \
                'HOSTCC=${BUILD_CC}' \
                'HOST_CFLAGS=${BUILD_CFLAGS}' \
                'CC=${CC}' \
                ARCH=${UCLIBC_ARCH}"

EXTRA_OEMAKE_task_do_package = "${OEMAKE_NO_CC}"

# enable verbose output:
export V="2"

# -O<n> -fno-omit-frame-pointer ends up with GCC ICE on thumb as reported
# http://gcc.gnu.org/bugzilla/show_bug.cgi?id=44860
#
CFLAGS_remove_arm = "-fno-omit-frame-pointer"
UCLIBC_EXTRA_CFLAGS  = "${@oe_filter_out('(-I\S+|-i\S+)', '${CFLAGS}', d)}"

configmangle = '/^KERNEL_HEADERS/d; \
                /^RUNTIME_PREFIX/d; \
                /^DEVEL_PREFIX/d; \
                /^SHARED_LIB_LOADER_PREFIX/d; \
                /^UCLIBC_EXTRA_CFLAGS/d; \
                s,.*UCLIBC_HAS_WCHAR.*,UCLIBC_HAS_WCHAR=y,g; \
                ${@["","s,.*COMPILE_IN_THUMB_MODE.*,COMPILE_IN_THUMB_MODE=y,;"][d.getVar("ARM_INSTRUCTION_SET", True) == "thumb"]} \
                ${@["","s,.*UCLIBC_HAS_LOCALE.*,UCLIBC_HAS_LOCALE=y,;"][d.getVar("USE_NLS", True) == "yes"]} \
                ${@["","s,.*LDSO_GNU_HASH_SUPPORT.*,# LDSO_GNU_HASH_SUPPORT is not set,;"][d.getVar("TARGET_ARCH", True) in ['mips', 'mipsel', 'mips64', 'mips64el', 'avr32']]} \
                /^CROSS/d; \
                /^TARGET_ARCH=/d; \
                /^TARGET_/s,^\([^=]*\).*,# \1 is not set,g; \
                s,^DOSTRIP.*,# DOSTRIP is not set,g; \
                /_[EO]*ABI/d; \
                /HAS_FPU/d; \
                ${@["","s,.*MULTILIB_DIR.*,MULTILIB_DIR=\"${baselib}\",;"][d.getVar("baselib", True) != "lib"]} \
               '
OE_FEATURES = "${@features_to_uclibc_conf(d)}"
OE_DEL      = "${@features_to_uclibc_del(d)}"
python () {
    if "${OE_DEL}":
        d.setVar('configmangle_append', "${OE_DEL}" + "\n")

    # by default uclibc uses mips1 ISA for o32 ABI
    # if we use TARGET_CC_ARCH="-march=mips32" we end up 
    # with conflicting march options to gcc. Here we
    # ask for MIPS32 ISA to match the chosen arch
    tune = d.getVar("DEFAULTTUNE", True)
    if tune.startswith('mips32'):
        import re
        tune = re.sub('(el)*(-nf)*', '', tune)
        d.setVar('configmangle_append',
                 "/^### MIPS32_CHECK$/a\\\nCONFIG_MIPS_ISA_%s=y\n\n" % (tune.upper()))
    if "${OE_FEATURES}":
        d.setVar('configmangle_append',
                   "/^### DISTRO FEATURES$/a\\\n%s\n\n" %
                   ("\\n".join((d.expand("${OE_FEATURES}").split("\n")))))
    d.setVar('configmangle_append',
                 "/^### CROSS$/a\\\n%s\n" %
                  ("\\n".join(["CROSS_COMPILER_PREFIX=\"${TARGET_PREFIX}\"",
                        "UCLIBC_EXTRA_CFLAGS=\"${UCLIBC_EXTRA_CFLAGS}\"",
                         "KERNEL_HEADERS=\"${STAGING_INCDIR}\"",
                         "RUNTIME_PREFIX=\"/\"",
                         "DEVEL_PREFIX=\"/${prefix}\"",
                         "SHARED_LIB_LOADER_PREFIX=\"/lib\"",
                        ])
                  ))
    d.setVar('configmangle_append',
                 "/^### TGT$/a\\\nTARGET_ARCH=\"%s\"\\nTARGET_%s=y\n" %
                        ("${UCLIBC_ARCH}", "${UCLIBC_ARCH}"))
    d.setVar('configmangle_append',
    "/^### FPU$/a\\\n%s\n\n" % (["UCLIBC_HAS_FPU=y","# UCLIBC_HAS_FPU is not set"][d.getVar('TARGET_FPU', True) in [ 'soft' ]]))
    if "${UCLIBC_ENDIAN}":
        d.setVar('configmangle_append',
                   "/^### ABI$/a\\\nARCH_WANTS_%s_ENDIAN=y\n\n" % ("${UCLIBC_ENDIAN}"))
    if "${UCLIBC_ABI}":
        d.setVar('configmangle_append',
                   "/^### ABI$/a\\\nCONFIG_%s=y\n\n" % ("${UCLIBC_ABI}"))
}

python do_patch_append() {
    import subprocess
    subprocess.call("ln -sf ${STAGING_INCDIR}/linux ${S}/include/linux", shell=True)
    subprocess.call("ln -sf ${STAGING_INCDIR}/asm ${S}/include/asm", shell=True)
}

do_configure() {
        rm -f ${S}/.config

        # OpenEmbedded splits the uClibc.config in two parts:
        # uClibc.machine, uClibc.distro
        echo "### uClibc.machine ###" >${S}/merged.config
        cat ${WORKDIR}/uClibc.machine >>${S}/merged.config
        echo "### uClibc.distro ###" >>${S}/merged.config
        cat ${WORKDIR}/uClibc.distro >>${S}/merged.config
        echo "### CROSS" >>${S}/merged.config
        echo "### TGT" >>${S}/merged.config
        echo "### MMU" >>${S}/merged.config
        echo "### FPU" >>${S}/merged.config
        echo "### ABI" >>${S}/merged.config
        echo "### DISTRO FEATURES" >>${S}/merged.config
        echo "### MIPS32_CHECK" >>${S}/merged.config

        # Mangle the resulting .config depending on OE variables
        sed -i -e '${configmangle}' ${S}/merged.config
	cp ${S}/merged.config .config
        merge_config.sh -r -n -m .config ${@" ".join(find_cfgs(d))}
	cml1_do_configure

}

do_install() {
	oe_runmake PREFIX=${D} install
	rm -rf ${D}${includedir}/iconv.h
}

# build ldd, ldconfig and friends but only for full uclibc
do_install_append_pn-uclibc () {
        oe_runmake PREFIX=${D} "SSP_ALL_CFLAGS=${TARGET_LINK_HASH_STYLE}" -C utils utils_install
}

