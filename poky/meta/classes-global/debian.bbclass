#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Debian package renaming only occurs when a package is built
# We therefore have to make sure we build all runtime packages
# before building the current package to make the packages runtime
# depends are correct
#
# Custom library package names can be defined setting
# DEBIANNAME: + pkgname to the desired name.
#
# Better expressed as ensure all RDEPENDS package before we package
# This means we can't have circular RDEPENDS/RRECOMMENDS

AUTO_LIBNAME_PKGS = "${PACKAGES}"

inherit package

python debian_package_name_hook () {
    import glob, copy, stat, errno, re, pathlib, subprocess

    pkgdest = d.getVar("PKGDEST")
    packages = d.getVar('PACKAGES')
    so_re = re.compile(r"lib.*\.so")

    def socrunch(s):
        s = s.lower().replace('_', '-')
        m = re.match(r"^(.*)(.)\.so\.(.*)$", s)
        if m is None:
            return None
        if m.group(2) in '0123456789':
            bin = '%s%s-%s' % (m.group(1), m.group(2), m.group(3))
        else:
            bin = m.group(1) + m.group(2) + m.group(3)
        dev = m.group(1) + m.group(2)
        return (bin, dev)

    def isexec(path):
        try:
            s = os.stat(path)
        except (os.error, AttributeError):
            return 0
        return (s[stat.ST_MODE] & stat.S_IEXEC)

    def add_rprovides(pkg, d):
        newpkg = d.getVar('PKG:' + pkg)
        if newpkg and newpkg != pkg:
            provs = (d.getVar('RPROVIDES:' + pkg) or "").split()
            if pkg not in provs:
                d.appendVar('RPROVIDES:' + pkg, " " + pkg + " (=" + d.getVar("PKGV") + ")")

    def auto_libname(packages, orig_pkg):
        p = lambda var: pathlib.PurePath(d.getVar(var))
        libdirs = (p("base_libdir"), p("libdir"))
        bindirs = (p("base_bindir"), p("base_sbindir"), p("bindir"), p("sbindir"))

        sonames = []
        has_bins = 0
        has_libs = 0
        for f in pkgfiles[orig_pkg]:
            # This is .../packages-split/orig_pkg/
            pkgpath = pathlib.PurePath(pkgdest, orig_pkg)
            # Strip pkgpath off the full path to a file in the package, re-root
            # so it is absolute, and then get the parent directory of the file.
            path = pathlib.PurePath("/") / (pathlib.PurePath(f).relative_to(pkgpath).parent)
            if path in bindirs:
                has_bins = 1
            if path in libdirs:
                has_libs = 1
                if so_re.match(os.path.basename(f)):
                    try:
                        cmd = [d.expand("${TARGET_PREFIX}objdump"), "-p", f]
                        output = subprocess.check_output(cmd).decode("utf-8")
                        for m in re.finditer(r"\s+SONAME\s+([^\s]+)", output):
                            if m.group(1) not in sonames:
                                sonames.append(m.group(1))
                    except subprocess.CalledProcessError:
                        pass
        bb.debug(1, 'LIBNAMES: pkg %s libs %d bins %d sonames %s' % (orig_pkg, has_libs, has_bins, sonames))
        soname = None
        if len(sonames) == 1:
            soname = sonames[0]
        elif len(sonames) > 1:
            lead = d.getVar('LEAD_SONAME')
            if lead:
                r = re.compile(lead)
                filtered = []
                for s in sonames:
                    if r.match(s):
                        filtered.append(s)
                if len(filtered) == 1:
                    soname = filtered[0]
                elif len(filtered) > 1:
                    bb.note("Multiple matches (%s) for LEAD_SONAME '%s'" % (", ".join(filtered), lead))
                else:
                    bb.note("Multiple libraries (%s) found, but LEAD_SONAME '%s' doesn't match any of them" % (", ".join(sonames), lead))
            else:
                bb.note("Multiple libraries (%s) found and LEAD_SONAME not defined" % ", ".join(sonames))

        if has_libs and not has_bins and soname:
            soname_result = socrunch(soname)
            if soname_result:
                (pkgname, devname) = soname_result
                for pkg in packages.split():
                    if (d.getVar('PKG:' + pkg, False) or d.getVar('DEBIAN_NOAUTONAME:' + pkg, False)):
                        add_rprovides(pkg, d)
                        continue
                    debian_pn = d.getVar('DEBIANNAME:' + pkg, False)
                    if debian_pn:
                        newpkg = debian_pn
                    elif pkg == orig_pkg:
                        newpkg = pkgname
                    else:
                        newpkg = pkg.replace(orig_pkg, devname, 1)
                    mlpre=d.getVar('MLPREFIX')
                    if mlpre:
                        if not newpkg.find(mlpre) == 0:
                            newpkg = mlpre + newpkg
                    if newpkg != pkg:
                        bb.note("debian: renaming %s to %s" % (pkg, newpkg))
                        d.setVar('PKG:' + pkg, newpkg)
                        add_rprovides(pkg, d)
        else:
            add_rprovides(orig_pkg, d)

    # reversed sort is needed when some package is substring of another
    # ie in ncurses we get without reverse sort: 
    # DEBUG: LIBNAMES: pkgname libtic5 devname libtic pkg ncurses-libtic orig_pkg ncurses-libtic debian_pn None newpkg libtic5
    # and later
    # DEBUG: LIBNAMES: pkgname libtic5 devname libtic pkg ncurses-libticw orig_pkg ncurses-libtic debian_pn None newpkg libticw
    # so we need to handle ncurses-libticw->libticw5 before ncurses-libtic->libtic5
    for pkg in sorted((d.getVar('AUTO_LIBNAME_PKGS') or "").split(), reverse=True):
        auto_libname(packages, pkg)
}

EXPORT_FUNCTIONS package_name_hook

DEBIAN_NAMES = "1"
