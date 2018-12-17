SECTION = "devel"
# Need binutils for libiberty.a
# Would need transfig-native for documentation if it wasn't disabled
DEPENDS = "elfutils binutils"
SUMMARY = "An ELF prelinking utility"
HOMEPAGE = "http://git.yoctoproject.org/cgit.cgi/prelink-cross/about/"
DESCRIPTION = "The prelink package contains a utility which modifies ELF shared libraries \
and executables, so that far fewer relocations need to be resolved at \
runtime and thus programs come up faster."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"
SRCREV = "a853a5d715d84eec93aa68e8f2df26b7d860f5b2"
PV = "1.0+git${SRCPV}"

#
# The cron script attempts to re-prelink the system daily -- on
# systems where users are adding applications, this might be reasonable
# but for embedded, we should be re-running prelink -a after an update.
#
# Default is prelinking is enabled.
#
SUMMARY_${PN}-cron = "Cron scripts to control automatic prelinking"
DESCRIPTION_${PN}-cron = "Cron scripts to control automatic prelinking.  \
See: ${sysconfdir}/cron.daily/prelink for configuration information."

FILES_${PN}-cron = "${sysconfdir}/cron.daily ${sysconfdir}/default"

PACKAGES =+ "${PN}-cron"

SRC_URI = "git://git.yoctoproject.org/prelink-cross.git;branch=cross_prelink_staging \
           file://prelink.conf \
           file://prelink.cron.daily \
           file://prelink.default \
           file://macros.prelink \
           file://0001-src-arch-mips.c-check-info-resolvetls-before-use-its.patch \
"
UPSTREAM_CHECK_COMMITS = "1"

TARGET_OS_ORIG := "${TARGET_OS}"
OVERRIDES_append = ":${TARGET_OS_ORIG}"

S = "${WORKDIR}/git"

inherit autotools 

BBCLASSEXTEND = "native"

EXTRA_OECONF = "--disable-selinux --with-pkgversion=${PV}-${PR} \
	--with-bugurl=http://bugzilla.yoctoproject.org/"


#
# For target prelink we need to ensure paths match the lib path layout
# including for any configured multilibs
#
python do_linkerpaths () {
    values = all_multilib_tune_list(["TUNE_ARCH", "baselib", "ABIEXTENSION"], d)

    arches = values["TUNE_ARCH"]
    baselibs = values["baselib"]
    abis = values["ABIEXTENSION"]

    def replace_lines(f, search, replacement, d, firstonly = False, secondonly = False):
        f = d.expand(f)
        if search == replacement:
            return
        bb.debug(2, "Replacing %s with %s in %s" % (search, replacement, f))
        with open(f, "r") as data:
            lines = data.readlines()
        with open(f, "w") as data:
            for line in lines:
                if not secondonly and not firstonly:
                    line = line.replace(search, replacement)
                elif secondonly and search in line:
                    secondonly = False
                elif firstonly and search and search in line:
                    line = line.replace(search, replacement)
                    search = None
                data.write(line)

    def replace_lines_rtld(f, search, replacement, section, d):
        f = d.expand(f)
        bb.debug(2, "Replacing %s with %s in %s" % (search, replacement, f))
        with open(f, "r") as data:
            lines = data.readlines()
        found = False
        found2 = False
        with open(f, "w") as data:
            for line in lines:
                if section in line:
                    if section == "else" and "if" in line:
                        found = False
                    else:
                        found = True
                if found and "dst_LIB =" in line:
                    found2 = True
                elif "}" in line:
                    found = False
                    found2 = False
                if found2:
                    line = line.replace(search, replacement)
                data.write(line)

    for i, arch in enumerate(arches):
        tune_baselib = baselibs[i]
        abi = abis[i]

        bits = 32
        if arch == "powerpc":
            replace_lines("${S}/src/arch-ppc.c", "/lib/ld.so.1", "/" + tune_baselib + "/ld.so.1", d)
        elif arch == "powerpc64":
            replace_lines("${S}/src/arch-ppc64.c", "/lib64/ld64.so.1", "/" + tune_baselib + "/ld64.so.1", d)
            bits = 64
        elif arch == "x86_64":
            if abi == "x32":
                replace_lines("${S}/src/arch-x86_64.c", "/libx32/ld-linux-x32.so.2", "/" + tune_baselib + "/ld-linux-x32.so.2", d)
            else:
                replace_lines("${S}/src/arch-x86_64.c", "/lib64/ld-linux-x86-64.so.2", "/" + tune_baselib + "/ld-linux-x86-64.so.2", d)
            bits = 64
        elif arch == "arm":
            replace_lines("${S}/src/arch-arm.c", "/lib/ld-linux.so.3", "/" + tune_baselib + "/ld-linux.so.3", d)
            replace_lines("${S}/src/arch-arm.c", "/lib/ld-linux-armhf.so.3", "/" + tune_baselib + "/ld-linux-armhf.so.3", d)
        elif arch == "mips" or arch == "mipsel":
            replace_lines("${S}/src/arch-mips.c", "/lib/ld.so.1", "/" + tune_baselib + "/ld.so.1", d, firstonly=True)
            replace_lines("${S}/src/arch-mips.c", "/lib32/ld.so.1", "/" + tune_baselib + "/ld.so.1", d)
        elif arch == "mips64" or arch == "mips64el":
            replace_lines("${S}/src/arch-mips.c", "/lib/ld.so.1", "/" + tune_baselib + "/ld.so.1", d, secondonly=True)
            replace_lines("${S}/src/arch-mips.c", "/lib64/ld.so.1", "/" + tune_baselib + "/ld.so.1", d)
            bits = 64
        elif arch.endswith("86"):
            replace_lines("${S}/src/arch-i386.c", "/lib/ld-linux.so.2", "/" + tune_baselib + "/ld-linux.so.2", d)
        if bits == 32 and tune_baselib != "lib":
            replace_lines_rtld("${S}/src/rtld/rtld.c", "lib", tune_baselib, "else", d)
        if bits == 64 and tune_baselib != "lib64":
            replace_lines_rtld("${S}/src/rtld/rtld.c", "lib64", tune_baselib, "use_64bit", d)
}

python () {
    overrides = d.getVar("OVERRIDES").split(":")
    if "class-target" in overrides:
        bb.build.addtask('do_linkerpaths', 'do_configure', 'do_patch', d)
}

do_configure_prepend () {
        # Disable documentation!
        echo "all:" > ${S}/doc/Makefile.am
}

do_install_append () {
	install -d ${D}${sysconfdir}/cron.daily ${D}${sysconfdir}/default ${D}${sysconfdir}/rpm
	install -m 0644 ${WORKDIR}/prelink.conf ${D}${sysconfdir}/prelink.conf
	install -m 0644 ${WORKDIR}/prelink.cron.daily ${D}${sysconfdir}/cron.daily/prelink
	install -m 0644 ${WORKDIR}/prelink.default ${D}${sysconfdir}/default/prelink
	install -m 0644 ${WORKDIR}/macros.prelink ${D}${sysconfdir}/rpm/macros.prelink
}

# If we ae doing a cross install, we want to avoid prelinking.
# Prelinking during a cross install should be handled by the image-prelink
# bbclass.  If the user desires this to run on the target at first boot
# they will need to create a custom boot script.
pkg_postinst_prelink() {
#!/bin/sh

if [ "x$D" != "x" ]; then
  exit 0
fi

prelink -a
}

pkg_prerm_prelink() {
#!/bin/sh

if [ "x$D" != "x" ]; then
  exit 1
fi

prelink -au
}

