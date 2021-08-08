#
# Recipe needs to set MULTILIB_SCRIPTS in the form <pkgname>:<scriptname>, e.g.
# MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/file1 ${PN}:${base_bindir}/file2"
# to indicate which script files to process from which packages.
#

inherit update-alternatives

MULTILIB_SUFFIX = "${@d.getVar('base_libdir',1).split('/')[-1]}"

PACKAGE_PREPROCESS_FUNCS += "multilibscript_rename"

multilibscript_rename() {
	:
}

python () {
    # Do nothing if multilib isn't being used
    if not d.getVar("MULTILIB_VARIANTS"):
        return
    # Do nothing for native/cross
    if bb.data.inherits_class('native', d) or bb.data.inherits_class('cross', d):
        return

    for entry in (d.getVar("MULTILIB_SCRIPTS", False) or "").split():
        pkg, script = entry.split(":")
        epkg = d.expand(pkg)
        scriptname = os.path.basename(script)
        d.appendVar("ALTERNATIVE:" + epkg, " " + scriptname + " ")
        d.setVarFlag("ALTERNATIVE_LINK_NAME", scriptname, script)
        d.setVarFlag("ALTERNATIVE_TARGET", scriptname, script + "-${MULTILIB_SUFFIX}")
        d.appendVar("multilibscript_rename",  "\n	mv ${PKGD}" + script + " ${PKGD}" + script + "-${MULTILIB_SUFFIX}")
        d.appendVar("FILES:" + epkg, " " + script + "-${MULTILIB_SUFFIX}")
}
