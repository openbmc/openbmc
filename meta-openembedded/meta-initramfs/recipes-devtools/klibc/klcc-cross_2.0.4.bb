SUMMARY = "The klcc crosscompiler for klibc"

require klibc.inc
DEPENDS = "klibc"

# no packaging for this crossscript
PACKAGES = ""
inherit nopackages

FILESPATH =. "${FILE_DIRNAME}/klibc-${PV}:"

SRC_URI += "file://use-env-for-perl.patch"

# disable task already run in klibc recipe
do_configure[noexec] = "1"

do_compile() {
    oe_runmake 'INSTALLDIR=${STAGING_DIR_TARGET}${libdir}/klibc' klcc
}

do_install() {
    install -d ${D}${bindir_crossscripts}/
    install -m 0755 klcc/klcc ${D}${bindir_crossscripts}/${TARGET_PREFIX}klcc
    # Turn the horribly encoded paths into something which sstate can transform using its ususal
    # magic by removing all the crazy escaping.
    sed -i -e "2i \$TARGETSYSROOT = '${STAGING_DIR_TARGET}';" \
           -e "2i \$NATIVESYSROOT = '${STAGING_DIR_NATIVE}';" \
           -e 's#${@d.getVar("STAGING_DIR_NATIVE", True).replace("/", "\\\\/").replace("-", "\\\\-").replace(".", "\\\\.")}#${NATIVESYSROOT}#g;' \
           -e 's#${@d.getVar("STAGING_DIR_TARGET", True).replace("/", "\\\\/").replace("-", "\\\\-").replace(".", "\\\\.")}#${TARGETSYSROOT}#g' \
        ${D}${bindir_crossscripts}/${TARGET_PREFIX}klcc
}

SYSROOT_DIRS += "${bindir_crossscripts}"
SSTATE_SCAN_FILES += "*-klcc"
