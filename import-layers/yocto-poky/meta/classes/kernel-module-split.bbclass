pkg_postinst_modules () {
if [ -z "$D" ]; then
	depmod -a ${KERNEL_VERSION}
else
	# image.bbclass will call depmodwrapper after everything is installed,
	# no need to do it here as well
	:
fi
}

pkg_postrm_modules () {
if [ -z "$D" ]; then
	depmod -a ${KERNEL_VERSION}
else
	depmodwrapper -a -b $D ${KERNEL_VERSION}
fi
}

autoload_postinst_fragment() {
if [ x"$D" = "x" ]; then
	modprobe %s || true
fi
}

do_install_append() {
	install -d ${D}${sysconfdir}/modules-load.d/ ${D}${sysconfdir}/modprobe.d/
}

PACKAGESPLITFUNCS_prepend = "split_kernel_module_packages "

KERNEL_MODULES_META_PACKAGE ?= "kernel-modules"

python split_kernel_module_packages () {
    import re

    modinfoexp = re.compile("([^=]+)=(.*)")
    kerverrexp = re.compile('^(.*-hh.*)[\.\+].*$')
    depmodpat0 = re.compile("^(.*\.k?o):..*$")
    depmodpat1 = re.compile("^(.*\.k?o):\s*(.*\.k?o)\s*$")
    depmodpat2 = re.compile("^(.*\.k?o):\s*(.*\.k?o)\s*\\\$")
    depmodpat3 = re.compile("^\t(.*\.k?o)\s*\\\$")
    depmodpat4 = re.compile("^\t(.*\.k?o)\s*$")

    def extract_modinfo(file):
        import tempfile, subprocess
        tempfile.tempdir = d.getVar("WORKDIR", True)
        tf = tempfile.mkstemp()
        tmpfile = tf[1]
        cmd = "%sobjcopy -j .modinfo -O binary %s %s" % (d.getVar("HOST_PREFIX", True) or "", file, tmpfile)
        subprocess.call(cmd, shell=True)
        f = open(tmpfile)
        l = f.read().split("\000")
        f.close()
        os.close(tf[0])
        os.unlink(tmpfile)
        vals = {}
        for i in l:
            m = modinfoexp.match(i)
            if not m:
                continue
            vals[m.group(1)] = m.group(2)
        return vals

    def parse_depmod():

        dvar = d.getVar('PKGD', True)

        kernelver = d.getVar('KERNEL_VERSION', True)
        kernelver_stripped = kernelver
        m = kerverrexp.match(kernelver)
        if m:
            kernelver_stripped = m.group(1)
        staging_kernel_dir = d.getVar("STAGING_KERNEL_BUILDDIR", True)
        system_map_file = "%s/boot/System.map-%s" % (dvar, kernelver)
        if not os.path.exists(system_map_file):
            system_map_file = "%s/System.map-%s" % (staging_kernel_dir, kernelver)
            if not os.path.exists(system_map_file):
                bb.fatal("System.map-%s does not exist in '%s/boot' nor STAGING_KERNEL_BUILDDIR '%s'" % (kernelver, dvar, staging_kernel_dir))

        cmd = "depmod -n -a -b %s -F %s %s" % (dvar, system_map_file, kernelver_stripped)
        f = os.popen(cmd, 'r')

        deps = {}
        line = f.readline()
        while line:
            if not depmodpat0.match(line):
                line = f.readline()
                continue
            m1 = depmodpat1.match(line)
            if m1:
                deps[m1.group(1)] = m1.group(2).split()
            else:
                m2 = depmodpat2.match(line)
                if m2:
                    deps[m2.group(1)] = m2.group(2).split()
                    line = f.readline()
                    m3 = depmodpat3.match(line)
                    while m3:
                        deps[m2.group(1)].extend(m3.group(1).split())
                        line = f.readline()
                        m3 = depmodpat3.match(line)
                    m4 = depmodpat4.match(line)
                    deps[m2.group(1)].extend(m4.group(1).split())
            line = f.readline()
        f.close()
        return deps

    def get_dependencies(file, pattern, format):
        # file no longer includes PKGD
        file = file.replace(d.getVar('PKGD', True) or '', '', 1)
        # instead is prefixed with /lib/modules/${KERNEL_VERSION}
        file = file.replace("/lib/modules/%s/" % d.getVar('KERNEL_VERSION', True) or '', '', 1)

        if file in module_deps:
            dependencies = []
            for i in module_deps[file]:
                m = re.match(pattern, os.path.basename(i))
                if not m:
                    continue
                on = legitimize_package_name(m.group(1))
                dependency_pkg = format % on
                dependencies.append(dependency_pkg)
            return dependencies
        return []

    def frob_metadata(file, pkg, pattern, format, basename):
        vals = extract_modinfo(file)

        dvar = d.getVar('PKGD', True)

        # If autoloading is requested, output /etc/modules-load.d/<name>.conf and append
        # appropriate modprobe commands to the postinst
        autoloadlist = (d.getVar("KERNEL_MODULE_AUTOLOAD", True) or "").split()
        autoload = d.getVar('module_autoload_%s' % basename, True)
        if autoload and autoload == basename:
            bb.warn("module_autoload_%s was replaced by KERNEL_MODULE_AUTOLOAD for cases where basename == module name, please drop it" % basename)
        if autoload and basename not in autoloadlist:
            bb.warn("module_autoload_%s is defined but '%s' isn't included in KERNEL_MODULE_AUTOLOAD, please add it there" % (basename, basename))
        if basename in autoloadlist:
            name = '%s/etc/modules-load.d/%s.conf' % (dvar, basename)
            f = open(name, 'w')
            if autoload:
                for m in autoload.split():
                    f.write('%s\n' % m)
            else:
                f.write('%s\n' % basename)
            f.close()
            postinst = d.getVar('pkg_postinst_%s' % pkg, True)
            if not postinst:
                bb.fatal("pkg_postinst_%s not defined" % pkg)
            postinst += d.getVar('autoload_postinst_fragment', True) % (autoload or basename)
            d.setVar('pkg_postinst_%s' % pkg, postinst)

        # Write out any modconf fragment
        modconflist = (d.getVar("KERNEL_MODULE_PROBECONF", True) or "").split()
        modconf = d.getVar('module_conf_%s' % basename, True)
        if modconf and basename in modconflist:
            name = '%s/etc/modprobe.d/%s.conf' % (dvar, basename)
            f = open(name, 'w')
            f.write("%s\n" % modconf)
            f.close()
        elif modconf:
            bb.error("Please ensure module %s is listed in KERNEL_MODULE_PROBECONF since module_conf_%s is set" % (basename, basename))

        files = d.getVar('FILES_%s' % pkg, True)
        files = "%s /etc/modules-load.d/%s.conf /etc/modprobe.d/%s.conf" % (files, basename, basename)
        d.setVar('FILES_%s' % pkg, files)

        if "description" in vals:
            old_desc = d.getVar('DESCRIPTION_' + pkg, True) or ""
            d.setVar('DESCRIPTION_' + pkg, old_desc + "; " + vals["description"])

        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS_' + pkg, True) or "")
        for dep in get_dependencies(file, pattern, format):
            if not dep in rdepends:
                rdepends[dep] = []
        d.setVar('RDEPENDS_' + pkg, bb.utils.join_deps(rdepends, commasep=False))

        # Avoid automatic -dev recommendations for modules ending with -dev.
        d.setVarFlag('RRECOMMENDS_' + pkg, 'nodeprrecs', 1)

    module_deps = parse_depmod()
    module_regex = '^(.*)\.k?o$'
    module_pattern = 'kernel-module-%s'

    postinst = d.getVar('pkg_postinst_modules', True)
    postrm = d.getVar('pkg_postrm_modules', True)

    modules = do_split_packages(d, root='/lib/modules', file_regex=module_regex, output_pattern=module_pattern, description='%s kernel module', postinst=postinst, postrm=postrm, recursive=True, hook=frob_metadata, extra_depends='kernel-%s' % (d.getVar("KERNEL_VERSION", True)))
    if modules:
        metapkg = d.getVar('KERNEL_MODULES_META_PACKAGE', True)
        d.appendVar('RDEPENDS_' + metapkg, ' '+' '.join(modules))

    # If modules-load.d and modprobe.d are empty at this point, remove them to
    # avoid warnings. removedirs only raises an OSError if an empty
    # directory cannot be removed.
    dvar = d.getVar('PKGD', True)
    for dir in ["%s/etc/modprobe.d" % (dvar), "%s/etc/modules-load.d" % (dvar), "%s/etc" % (dvar)]:
        if len(os.listdir(dir)) == 0:
            os.rmdir(dir)
}

do_package[vardeps] += '${@" ".join(map(lambda s: "module_conf_" + s, (d.getVar("KERNEL_MODULE_PROBECONF", True) or "").split()))}'
