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

PACKAGE_WRITE_DEPS += "kmod-native depmodwrapper-cross"

do_install_append() {
	install -d ${D}${sysconfdir}/modules-load.d/ ${D}${sysconfdir}/modprobe.d/
}

PACKAGESPLITFUNCS_prepend = "split_kernel_module_packages "

KERNEL_MODULES_META_PACKAGE ?= "${@ d.getVar("KERNEL_PACKAGE_NAME") or "kernel" }-modules"

KERNEL_MODULE_PACKAGE_PREFIX ?= ""
KERNEL_MODULE_PACKAGE_SUFFIX ?= "-${KERNEL_VERSION}"
KERNEL_MODULE_PROVIDE_VIRTUAL ?= "1"

python split_kernel_module_packages () {
    import re

    modinfoexp = re.compile("([^=]+)=(.*)")

    def extract_modinfo(file):
        import tempfile, subprocess
        tempfile.tempdir = d.getVar("WORKDIR")
        compressed = re.match( r'.*\.([xg])z$', file)
        tf = tempfile.mkstemp()
        tmpfile = tf[1]
        if compressed:
            tmpkofile = tmpfile + ".ko"
            if compressed.group(1) == 'g':
                cmd = "gunzip -dc %s > %s" % (file, tmpkofile)
                subprocess.check_call(cmd, shell=True)
            elif compressed.group(1) == 'x':
                cmd = "xz -dc %s > %s" % (file, tmpkofile)
                subprocess.check_call(cmd, shell=True)
            else:
                msg = "Cannot decompress '%s'" % file
                raise msg
            cmd = "%sobjcopy -j .modinfo -O binary %s %s" % (d.getVar("HOST_PREFIX") or "", tmpkofile, tmpfile)
        else:
            cmd = "%sobjcopy -j .modinfo -O binary %s %s" % (d.getVar("HOST_PREFIX") or "", file, tmpfile)
        subprocess.check_call(cmd, shell=True)
        # errors='replace': Some old kernel versions contain invalid utf-8 characters in mod descriptions (like 0xf6, 'ö')
        f = open(tmpfile, errors='replace')
        l = f.read().split("\000")
        f.close()
        os.close(tf[0])
        os.unlink(tmpfile)
        if compressed:
            os.unlink(tmpkofile)
        vals = {}
        for i in l:
            m = modinfoexp.match(i)
            if not m:
                continue
            vals[m.group(1)] = m.group(2)
        return vals

    def frob_metadata(file, pkg, pattern, format, basename):
        vals = extract_modinfo(file)

        dvar = d.getVar('PKGD')

        # If autoloading is requested, output /etc/modules-load.d/<name>.conf and append
        # appropriate modprobe commands to the postinst
        autoloadlist = (d.getVar("KERNEL_MODULE_AUTOLOAD") or "").split()
        autoload = d.getVar('module_autoload_%s' % basename)
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
            postinst = d.getVar('pkg_postinst_%s' % pkg)
            if not postinst:
                bb.fatal("pkg_postinst_%s not defined" % pkg)
            postinst += d.getVar('autoload_postinst_fragment') % (autoload or basename)
            d.setVar('pkg_postinst_%s' % pkg, postinst)

        # Write out any modconf fragment
        modconflist = (d.getVar("KERNEL_MODULE_PROBECONF") or "").split()
        modconf = d.getVar('module_conf_%s' % basename)
        if modconf and basename in modconflist:
            name = '%s/etc/modprobe.d/%s.conf' % (dvar, basename)
            f = open(name, 'w')
            f.write("%s\n" % modconf)
            f.close()
        elif modconf:
            bb.error("Please ensure module %s is listed in KERNEL_MODULE_PROBECONF since module_conf_%s is set" % (basename, basename))

        files = d.getVar('FILES_%s' % pkg)
        files = "%s /etc/modules-load.d/%s.conf /etc/modprobe.d/%s.conf" % (files, basename, basename)
        d.setVar('FILES_%s' % pkg, files)
        d.setVar('CONFFILES_%s' % pkg, files)

        if "description" in vals:
            old_desc = d.getVar('DESCRIPTION_' + pkg) or ""
            d.setVar('DESCRIPTION_' + pkg, old_desc + "; " + vals["description"])

        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS_' + pkg) or "")
        modinfo_deps = []
        if "depends" in vals and vals["depends"] != "":
            for dep in vals["depends"].split(","):
                on = legitimize_package_name(dep)
                dependency_pkg = format % on
                modinfo_deps.append(dependency_pkg)
        for dep in modinfo_deps:
            if not dep in rdepends:
                rdepends[dep] = []
        d.setVar('RDEPENDS_' + pkg, bb.utils.join_deps(rdepends, commasep=False))

        # Avoid automatic -dev recommendations for modules ending with -dev.
        d.setVarFlag('RRECOMMENDS_' + pkg, 'nodeprrecs', 1)

        # Provide virtual package without postfix
        providevirt = d.getVar('KERNEL_MODULE_PROVIDE_VIRTUAL')
        if providevirt == "1":
           postfix = format.split('%s')[1]
           d.setVar('RPROVIDES_' + pkg, pkg.replace(postfix, ''))

    kernel_package_name = d.getVar("KERNEL_PACKAGE_NAME") or "kernel"
    kernel_version = d.getVar("KERNEL_VERSION")

    module_regex = r'^(.*)\.k?o(?:\.[xg]z)?$'

    module_pattern_prefix = d.getVar('KERNEL_MODULE_PACKAGE_PREFIX')
    module_pattern_suffix = d.getVar('KERNEL_MODULE_PACKAGE_SUFFIX')
    module_pattern = module_pattern_prefix + kernel_package_name + '-module-%s' + module_pattern_suffix

    postinst = d.getVar('pkg_postinst_modules')
    postrm = d.getVar('pkg_postrm_modules')

    modules = do_split_packages(d, root='${nonarch_base_libdir}/modules', file_regex=module_regex, output_pattern=module_pattern, description='%s kernel module', postinst=postinst, postrm=postrm, recursive=True, hook=frob_metadata, extra_depends='%s-%s' % (kernel_package_name, kernel_version))
    if modules:
        metapkg = d.getVar('KERNEL_MODULES_META_PACKAGE')
        d.appendVar('RDEPENDS_' + metapkg, ' '+' '.join(modules))

    # If modules-load.d and modprobe.d are empty at this point, remove them to
    # avoid warnings. removedirs only raises an OSError if an empty
    # directory cannot be removed.
    dvar = d.getVar('PKGD')
    for dir in ["%s/etc/modprobe.d" % (dvar), "%s/etc/modules-load.d" % (dvar), "%s/etc" % (dvar)]:
        if len(os.listdir(dir)) == 0:
            os.rmdir(dir)
}

do_package[vardeps] += '${@" ".join(map(lambda s: "module_conf_" + s, (d.getVar("KERNEL_MODULE_PROBECONF") or "").split()))}'
