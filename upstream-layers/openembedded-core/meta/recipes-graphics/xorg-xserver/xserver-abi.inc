# Add runtime provides for the ABI versions, so that drivers can depend on the
# relevant version. This should be called from PACKAGEFUNCS.
python add_xorg_abi_provides() {
    import subprocess

    pn = d.getVar("PN")
    mlprefix = d.getVar("MLPREFIX") or ""

    # Set PKG_CONFIG_PATH so pkg-config looks at the .pc files that are going
    # into the new package, not the staged ones.
    newenv = dict(os.environ)
    newenv["PKG_CONFIG_PATH"] = d.expand("${PKGD}${libdir}/pkgconfig/:") + newenv["PKG_CONFIG_PATH"]

    # Get the list of ABIs that the pc file declares
    cmd = ("pkg-config", "--print-variables", "xorg-server")
    output = subprocess.run(cmd, check=True, capture_output=True, text=True, env=newenv).stdout
    abis = [var for var in output.splitlines() if var.startswith("abi_")]

    # Set RPROVIDES for those ABIs with the major version
    for abi in abis:
        cmd = ("pkg-config", "--variable", abi, "xorg-server")
        version = subprocess.run(cmd, check=True, capture_output=True, text=True, env=newenv).stdout
        major = version.split(".")[0]

        provides = " %sxorg-%s-%s" % (mlprefix, abi.replace("_", "-"), major)
        d.appendVar("RPROVIDES:" + pn, provides)
}

# Add the specified ABI dependency to the specified package.
# If package is not set then PN is used.
# This should be called via a shim function in PACKAGEFUNCS.
def _add_xorg_abi_depends(d, abi, package=None):
    import subprocess

    if not package:
        package = d.getVar("PN")

    # Set PKG_CONFIG_PATH to cater for the case where xserver is
    # itself providing drivers.
    newenv = dict(os.environ)
    newenv["PKG_CONFIG_PATH"] = d.expand("${PKGD}${libdir}/pkgconfig/:") + newenv["PKG_CONFIG_PATH"]

    mlprefix = d.getVar("MLPREFIX") or ""
    cmd = ("pkg-config", "xorg-server", "--variable=abi_%s" % abi)
    output = subprocess.run(cmd, text=True, capture_output=True, check=True, env=newenv).stdout
    abi = "%sxorg-abi-%s-%s" % (mlprefix, abi, output.split(".")[0])

    d.appendVar('RDEPENDS:' + package, ' ' + abi)
