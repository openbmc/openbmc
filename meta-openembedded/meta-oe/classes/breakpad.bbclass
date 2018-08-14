# Class to inherit when you want to build against Breakpad.
# Apart from inheriting this class, you need to set BREAKPAD_BIN in
# your recipe, and make sure that you link against libbreakpad_client.a.

DEPENDS += "breakpad breakpad-native"

CFLAGS += "-I${STAGING_DIR_TARGET}${includedir}/breakpad "
CXXFLAGS += "-I${STAGING_DIR_TARGET}${includedir}/breakpad "

BREAKPAD_BIN ?= ""

python () {
    breakpad_bin = d.getVar("BREAKPAD_BIN")

    if not breakpad_bin:
       PN = d.getVar("PN")
       FILE = os.path.basename(d.getVar("FILE"))
       bb.error("To build %s, see breakpad.bbclass for instructions on \
                 setting up your Breakpad configuration" % PN)
       raise ValueError('BREAKPAD_BIN not defined in %s' % PN)
}

# Add creation of symbols here
PACKAGE_PREPROCESS_FUNCS += "breakpad_package_preprocess"
breakpad_package_preprocess () {
    mkdir -p ${PKGD}/usr/share/breakpad-syms
    find ${D} -name ${BREAKPAD_BIN} -exec sh -c "dump_syms {} > ${PKGD}/usr/share/breakpad-syms/${BREAKPAD_BIN}.sym" \;
}

PACKAGES =+ "${PN}-breakpad"

FILES_${PN}-breakpad = "/usr/share/breakpad-syms"

