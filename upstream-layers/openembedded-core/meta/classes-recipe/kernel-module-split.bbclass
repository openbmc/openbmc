#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

pkg_postinst:modules () {
if [ -z "$D" ]; then
	depmod -a ${KERNEL_VERSION}
else
	# image.bbclass will call depmodwrapper after everything is installed,
	# no need to do it here as well
	:
fi
}

pkg_postrm:modules () {
if [ -z "$D" ]; then
	depmod -a ${KERNEL_VERSION}
else
	depmodwrapper -a -b $D ${KERNEL_VERSION} ${KERNEL_PACKAGE_NAME}
fi
}

autoload_postinst_fragment() {
if [ x"$D" = "x" ]; then
	modprobe %s || true
fi
}

PACKAGE_WRITE_DEPS += "kmod-native depmodwrapper-cross"

modulesloaddir ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${nonarch_libdir}', '${sysconfdir}', d)}/modules-load.d"
modprobedir ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${nonarch_base_libdir}', '${sysconfdir}', d)}/modprobe.d"

KERNEL_SPLIT_MODULES ?= "1"
PACKAGESPLITFUNCS =+ "split_kernel_module_packages"

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
        compressed = re.match( r'.*\.(gz|xz|zst)$', file)
        tf = tempfile.mkstemp()
        tmpfile = tf[1]
        if compressed:
            tmpkofile = tmpfile + ".ko"
            if compressed.group(1) == 'gz':
                cmd = "gunzip -dc %s > %s" % (file, tmpkofile)
                subprocess.check_call(cmd, shell=True)
            elif compressed.group(1) == 'xz':
                cmd = "xz -dc %s > %s" % (file, tmpkofile)
                subprocess.check_call(cmd, shell=True)
            elif compressed.group(1) == 'zst':
                cmd = "zstd -dc %s > %s" % (file, tmpkofile)
                subprocess.check_call(cmd, shell=True)
            else:
                msg = "Cannot decompress '%s'" % file
                raise msg
            cmd = "%s -j .modinfo -O binary %s %s" % (d.getVar("OBJCOPY"), tmpkofile, tmpfile)
        else:
            cmd = "%s -j .modinfo -O binary %s %s" % (d.getVar("OBJCOPY"), file, tmpfile)
        subprocess.check_call(cmd, shell=True)
        # errors='replace': Some old kernel versions contain invalid utf-8 characters in mod descriptions (like 0xf6, 'ö')
        with open(tmpfile, errors='replace') as f:
            l = f.read().split("\000")
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

    def handle_conf_files(d, basename, pkg):
        # If autoloading is requested, output ${modulesloaddir}/<name>.conf and append
        # appropriate modprobe commands to the postinst
        autoloadlist = (d.getVar("KERNEL_MODULE_AUTOLOAD") or "").split()
        autoload = d.getVar('module_autoload_%s' % basename)
        if autoload and autoload == basename:
            bb.warn("module_autoload_%s was replaced by KERNEL_MODULE_AUTOLOAD for cases where basename == module name, please drop it" % basename)
        if autoload and basename not in autoloadlist:
            bb.warn("module_autoload_%s is defined but '%s' isn't included in KERNEL_MODULE_AUTOLOAD, please add it there" % (basename, basename))

        # The .conf file can either be installed by a recipe or generated from module_autoload_*
        conf = '%s/%s.conf' % (d.getVar('modulesloaddir'), basename)
        name = '%s%s' % (d.getVar('PKGD'), conf)
        # If module name is in KERNEL_MODULE_AUTOLOAD, then generate the .conf file and write to `name`.
        if basename in autoloadlist:
            os.makedirs(os.path.dirname(name), exist_ok=True)
            with open(name, 'w') as f:
                if autoload:
                    for m in autoload.split():
                        f.write('%s\n' % m)
                else:
                    f.write('%s\n' % basename)
        # If the .conf file exits, then add it to FILES:* and CONFFILES:* and add postinstall hook.
        # It doesn't matter if it was generated from module_autoload_* or installed by the recipe.
        if os.path.exists(name):
            conf2append = ' %s' % conf
            d.appendVar('FILES:%s' % pkg, conf2append)
            d.appendVar('CONFFILES:%s' % pkg, conf2append)
            postinst = d.getVar('pkg_postinst:%s' % pkg)
            if not postinst:
                postinst = d.getVar('pkg_postinst:modules')
            postinst += d.getVar('autoload_postinst_fragment') % (autoload or basename)
            d.setVar('pkg_postinst:%s' % pkg, postinst)

        # Write out any modconf fragment
        modconflist = (d.getVar("KERNEL_MODULE_PROBECONF") or "").split()
        modconf = d.getVar('module_conf_%s' % basename)

        # The .conf file can either be installed by a recipe or generated from module_conf_*
        conf = '%s/%s.conf' % (d.getVar('modprobedir'), basename)
        name = '%s%s' % (d.getVar('PKGD'), conf)
        # If module name is in KERNEL_MODULE_PROBECONF, then generate the .conf file and write to `name`.
        if modconf and basename in modconflist:
            os.makedirs(os.path.dirname(name), exist_ok=True)
            with open(name, 'w') as f:
                f.write("%s\n" % modconf)
        elif modconf:
            bb.error("Please ensure module %s is listed in KERNEL_MODULE_PROBECONF since module_conf_%s is set" % (basename, basename))
        # If the .conf file exits, then add it to FILES:* and CONFFILES:*.
        # It doesn't matter if it was generated from module_conf_* or installed by the recipe.
        if os.path.exists(name):
            conf2append = ' %s' % conf
            d.appendVar('FILES:%s' % pkg, conf2append)
            d.appendVar('CONFFILES:%s' % pkg, conf2append)

    def generate_conf_files(d, root, file_regex, output_pattern):
        """
        Arguments:
        root           -- the path in which to search. Contains system lib path
                          so needs expansion.
        file_regex     -- regular expression to match searched files. Use
                          parentheses () to mark the part of this expression
                          that should be used to derive the module name (to be
                          substituted where %s is used in other function
                          arguments as noted below)
        output_pattern -- pattern to use for the package names. Must include %s.
        """
        import re, stat

        dvar = d.getVar('PKGD')
        root = d.expand(root)

        # if the root directory doesn't exist, it's fatal - exit from the current execution.
        if not os.path.exists(dvar + root):
            bb.fatal("kernel module root directory path does not exist")

        # walk through kernel module directory. for each entry in the directory, check if it
        # matches the desired regex pattern and file type. if it fullfills, process it to generate
        # it's conf file based on its package name.
        for walkroot, dirs, files in os.walk(dvar + root):
            for file in files:
                relpath = os.path.join(walkroot, file).replace(dvar + root + '/', '', 1)
                if not relpath:
                    continue
                m = re.match(file_regex, os.path.basename(relpath))
                if not m:
                    continue
                file_f = os.path.join(dvar + root, relpath)
                mode = os.lstat(file_f).st_mode
                if not (stat.S_ISREG(mode) or (allow_links and stat.S_ISLNK(mode)) or (allow_dirs and stat.S_ISDIR(mode))):
                    continue

                basename = m.group(1)
                on = legitimize_package_name(basename)
                pkg = output_pattern % on
                handle_conf_files(d, basename, pkg)


    def frob_metadata(file, pkg, pattern, format, basename):
        vals = extract_modinfo(file)
        dvar = d.getVar('PKGD')

        handle_conf_files(d, basename, pkg)

        if "description" in vals:
            old_desc = d.getVar('DESCRIPTION:' + pkg) or ""
            d.setVar('DESCRIPTION:' + pkg, old_desc + "; " + vals["description"])

        rdepends = bb.utils.explode_dep_versions2(d.getVar('RDEPENDS:' + pkg) or "")
        modinfo_deps = []
        if "depends" in vals and vals["depends"] != "":
            for dep in vals["depends"].split(","):
                on = legitimize_package_name(dep)
                dependency_pkg = format % on
                modinfo_deps.append(dependency_pkg)
        for dep in modinfo_deps:
            if not dep in rdepends:
                rdepends[dep] = []
        d.setVar('RDEPENDS:' + pkg, bb.utils.join_deps(rdepends, commasep=False))

        # Avoid automatic -dev recommendations for modules ending with -dev.
        d.setVarFlag('RRECOMMENDS:' + pkg, 'nodeprrecs', 1)

        # Provide virtual package without postfix
        providevirt = d.getVar('KERNEL_MODULE_PROVIDE_VIRTUAL')
        if providevirt == "1":
           postfix = format.split('%s')[1]
           d.setVar('RPROVIDES:' + pkg, pkg.replace(postfix, ''))

    kernel_package_name = d.getVar("KERNEL_PACKAGE_NAME") or "kernel"
    kernel_version = d.getVar("KERNEL_VERSION")

    metapkg = d.getVar('KERNEL_MODULES_META_PACKAGE')
    splitmods = d.getVar('KERNEL_SPLIT_MODULES')
    postinst = d.getVar('pkg_postinst:modules')
    postrm = d.getVar('pkg_postrm:modules')

    module_regex = r'^(.*)\.k?o(?:\.(gz|xz|zst))?$'

    module_pattern_prefix = d.getVar('KERNEL_MODULE_PACKAGE_PREFIX')
    module_pattern_suffix = d.getVar('KERNEL_MODULE_PACKAGE_SUFFIX')
    module_pattern = module_pattern_prefix + kernel_package_name + '-module-%s' + module_pattern_suffix

    if splitmods != '1':
        d.appendVar('FILES:' + metapkg, '%s %s %s/modules' %
            (d.getVar('modulesloaddir'), d.getVar('modprobedir'), d.getVar("nonarch_base_libdir")))
        d.appendVar('pkg_postinst:%s' % metapkg, postinst)
        d.prependVar('pkg_postrm:%s' % metapkg, postrm)
        generate_conf_files(d, root='${nonarch_base_libdir}/modules', file_regex=module_regex, output_pattern=module_pattern)
        return

    modules = do_split_packages(d, root='${nonarch_base_libdir}/modules', file_regex=module_regex, output_pattern=module_pattern, description='%s kernel module', postinst=postinst, postrm=postrm, recursive=True, hook=frob_metadata, extra_depends='%s-%s' % (kernel_package_name, kernel_version))
    if modules:
        d.appendVar('RDEPENDS:' + metapkg, ' '+' '.join(modules))
}

do_package[vardeps] += '${@" ".join(map(lambda s: "module_conf_" + s, (d.getVar("KERNEL_MODULE_PROBECONF") or "").split()))}'
