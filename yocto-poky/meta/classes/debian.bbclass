# Debian package renaming only occurs when a package is built
# We therefore have to make sure we build all runtime packages
# before building the current package to make the packages runtime
# depends are correct
#
# Custom library package names can be defined setting
# DEBIANNAME_ + pkgname to the desired name.
#
# Better expressed as ensure all RDEPENDS package before we package
# This means we can't have circular RDEPENDS/RRECOMMENDS

AUTO_LIBNAME_PKGS = "${PACKAGES}"

inherit package

DEBIANRDEP = "do_packagedata"
do_package_write_ipk[rdeptask] = "${DEBIANRDEP}"
do_package_write_deb[rdeptask] = "${DEBIANRDEP}"
do_package_write_tar[rdeptask] = "${DEBIANRDEP}"
do_package_write_rpm[rdeptask] = "${DEBIANRDEP}"

python () {
    if not d.getVar("PACKAGES", True):
        d.setVar("DEBIANRDEP", "")
}

python debian_package_name_hook () {
    import glob, copy, stat, errno, re

    pkgdest = d.getVar('PKGDEST', True)
    packages = d.getVar('PACKAGES', True)
    bin_re = re.compile(".*/s?" + os.path.basename(d.getVar("bindir", True)) + "$")
    lib_re = re.compile(".*/" + os.path.basename(d.getVar("libdir", True)) + "$")
    so_re = re.compile("lib.*\.so")

    def socrunch(s):
        s = s.lower().replace('_', '-')
        m = re.match("^(.*)(.)\.so\.(.*)$", s)
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
        newpkg = d.getVar('PKG_' + pkg, True)
        if newpkg and newpkg != pkg:
            provs = (d.getVar('RPROVIDES_' + pkg, True) or "").split()
            if pkg not in provs:
                d.appendVar('RPROVIDES_' + pkg, " " + pkg + " (=" + d.getVar("PKGV", True) + ")")

    def auto_libname(packages, orig_pkg):
        sonames = []
        has_bins = 0
        has_libs = 0
        for file in pkgfiles[orig_pkg]:
            root = os.path.dirname(file)
            if bin_re.match(root):
                has_bins = 1
            if lib_re.match(root):
                has_libs = 1
                if so_re.match(os.path.basename(file)):
                    cmd = (d.getVar('TARGET_PREFIX', True) or "") + "objdump -p " + file + " 2>/dev/null"
                    fd = os.popen(cmd)
                    lines = fd.readlines()
                    fd.close()
                    for l in lines:
                        m = re.match("\s+SONAME\s+([^\s]*)", l)
                        if m and not m.group(1) in sonames:
                            sonames.append(m.group(1))

        bb.debug(1, 'LIBNAMES: pkg %s libs %d bins %d sonames %s' % (orig_pkg, has_libs, has_bins, sonames))
        soname = None
        if len(sonames) == 1:
            soname = sonames[0]
        elif len(sonames) > 1:
            lead = d.getVar('LEAD_SONAME', True)
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
                    if (d.getVar('PKG_' + pkg, False) or d.getVar('DEBIAN_NOAUTONAME_' + pkg, False)):
                        add_rprovides(pkg, d)
                        continue
                    debian_pn = d.getVar('DEBIANNAME_' + pkg, False)
                    if debian_pn:
                        newpkg = debian_pn
                    elif pkg == orig_pkg:
                        newpkg = pkgname
                    else:
                        newpkg = pkg.replace(orig_pkg, devname, 1)
                    mlpre=d.getVar('MLPREFIX', True)
                    if mlpre:
                        if not newpkg.find(mlpre) == 0:
                            newpkg = mlpre + newpkg
                    if newpkg != pkg:
                        d.setVar('PKG_' + pkg, newpkg)
                        add_rprovides(pkg, d)
        else:
            add_rprovides(orig_pkg, d)

    # reversed sort is needed when some package is substring of another
    # ie in ncurses we get without reverse sort: 
    # DEBUG: LIBNAMES: pkgname libtic5 devname libtic pkg ncurses-libtic orig_pkg ncurses-libtic debian_pn None newpkg libtic5
    # and later
    # DEBUG: LIBNAMES: pkgname libtic5 devname libtic pkg ncurses-libticw orig_pkg ncurses-libtic debian_pn None newpkg libticw
    # so we need to handle ncurses-libticw->libticw5 before ncurses-libtic->libtic5
    for pkg in sorted((d.getVar('AUTO_LIBNAME_PKGS', True) or "").split(), reverse=True):
        auto_libname(packages, pkg)
}

EXPORT_FUNCTIONS package_name_hook

DEBIAN_NAMES = "1"

