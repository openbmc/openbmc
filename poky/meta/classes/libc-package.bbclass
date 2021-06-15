#
# This class knows how to package up [e]glibc. Its shared since prebuild binary toolchains
# may need packaging and its pointless to duplicate this code.
#
# Caller should set GLIBC_INTERNAL_USE_BINARY_LOCALE to one of:
#  "compile" - Use QEMU to generate the binary locale files
#  "precompiled" - The binary locale files are pregenerated and already present
#  "ondevice" - The device will build the locale files upon first boot through the postinst

GLIBC_INTERNAL_USE_BINARY_LOCALE ?= "ondevice"

GLIBC_SPLIT_LC_PACKAGES ?= "0"

python __anonymous () {
    enabled = d.getVar("ENABLE_BINARY_LOCALE_GENERATION")

    pn = d.getVar("PN")
    if pn.endswith("-initial"):
        enabled = False

    if enabled and int(enabled):
        import re

        target_arch = d.getVar("TARGET_ARCH")
        binary_arches = d.getVar("BINARY_LOCALE_ARCHES") or ""
        use_cross_localedef = d.getVar("LOCALE_GENERATION_WITH_CROSS-LOCALEDEF") or ""

        for regexp in binary_arches.split(" "):
            r = re.compile(regexp)

            if r.match(target_arch):
                depends = d.getVar("DEPENDS")
                if use_cross_localedef == "1" :
                    depends = "%s cross-localedef-native" % depends
                else:
                    depends = "%s qemu-native" % depends
                d.setVar("DEPENDS", depends)
                d.setVar("GLIBC_INTERNAL_USE_BINARY_LOCALE", "compile")
                break
}

# try to fix disable charsets/locales/locale-code compile fail
PACKAGE_NO_GCONV ?= "0"

OVERRIDES_append = ":${TARGET_ARCH}-${TARGET_OS}"

locale_base_postinst_ontarget() {
localedef --inputfile=${datadir}/i18n/locales/%s --charmap=%s %s
}

locale_base_postrm() {
#!/bin/sh
localedef --delete-from-archive --inputfile=${datadir}/locales/%s --charmap=%s %s
}

LOCALETREESRC ?= "${PKGD}"

do_prep_locale_tree() {
	treedir=${WORKDIR}/locale-tree
	rm -rf $treedir
	mkdir -p $treedir/${base_bindir} $treedir/${base_libdir} $treedir/${datadir} $treedir/${localedir}
	tar -cf - -C ${LOCALETREESRC}${datadir} -p i18n | tar -xf - -C $treedir/${datadir}
	# unzip to avoid parsing errors
	for i in $treedir/${datadir}/i18n/charmaps/*gz; do 
		gunzip $i
	done
	# The extract pattern "./l*.so*" is carefully selected so that it will
	# match ld*.so and lib*.so*, but not any files in the gconv directory
	# (if it exists). This makes sure we only unpack the files we need.
	# This is important in case usrmerge is set in DISTRO_FEATURES, which
	# means ${base_libdir} == ${libdir}.
	tar -cf - -C ${LOCALETREESRC}${base_libdir} -p . | tar -xf - -C $treedir/${base_libdir} --wildcards './l*.so*'
	if [ -f ${STAGING_LIBDIR_NATIVE}/libgcc_s.* ]; then
		tar -cf - -C ${STAGING_LIBDIR_NATIVE} -p libgcc_s.* | tar -xf - -C $treedir/${base_libdir}
	fi
	install -m 0755 ${LOCALETREESRC}${bindir}/localedef $treedir/${base_bindir}
}

do_collect_bins_from_locale_tree() {
	treedir=${WORKDIR}/locale-tree

	parent=$(dirname ${localedir})
	mkdir -p ${PKGD}/$parent
	tar -cf - -C $treedir/$parent -p $(basename ${localedir}) | tar -xf - -C ${PKGD}$parent

	# Finalize tree by chaning all duplicate files into hard links
	cross-localedef-hardlink -c -v ${WORKDIR}/locale-tree
}

inherit qemu

python package_do_split_gconvs () {
    import re
    if (d.getVar('PACKAGE_NO_GCONV') == '1'):
        bb.note("package requested not splitting gconvs")
        return

    if not d.getVar('PACKAGES'):
        return

    mlprefix = d.getVar("MLPREFIX") or ""

    bpn = d.getVar('BPN')
    libdir = d.getVar('libdir')
    if not libdir:
        bb.error("libdir not defined")
        return
    datadir = d.getVar('datadir')
    if not datadir:
        bb.error("datadir not defined")
        return

    gconv_libdir = oe.path.join(libdir, "gconv")
    charmap_dir = oe.path.join(datadir, "i18n", "charmaps")
    locales_dir = oe.path.join(datadir, "i18n", "locales")
    binary_locales_dir = d.getVar('localedir')

    def calc_gconv_deps(fn, pkg, file_regex, output_pattern, group):
        deps = []
        f = open(fn, "rb")
        c_re = re.compile(r'^copy "(.*)"')
        i_re = re.compile(r'^include "(\w+)".*')
        for l in f.readlines():
            l = l.decode("latin-1")
            m = c_re.match(l) or i_re.match(l)
            if m:
                dp = legitimize_package_name('%s%s-gconv-%s' % (mlprefix, bpn, m.group(1)))
                if not dp in deps:
                    deps.append(dp)
        f.close()
        if deps != []:
            d.setVar('RDEPENDS_%s' % pkg, " ".join(deps))
        if bpn != 'glibc':
            d.setVar('RPROVIDES_%s' % pkg, pkg.replace(bpn, 'glibc'))

    do_split_packages(d, gconv_libdir, file_regex=r'^(.*)\.so$', output_pattern=bpn+'-gconv-%s', \
        description='gconv module for character set %s', hook=calc_gconv_deps, \
        extra_depends=bpn+'-gconv')

    def calc_charmap_deps(fn, pkg, file_regex, output_pattern, group):
        deps = []
        f = open(fn, "rb")
        c_re = re.compile(r'^copy "(.*)"')
        i_re = re.compile(r'^include "(\w+)".*')
        for l in f.readlines():
            l = l.decode("latin-1")
            m = c_re.match(l) or i_re.match(l)
            if m:
                dp = legitimize_package_name('%s%s-charmap-%s' % (mlprefix, bpn, m.group(1)))
                if not dp in deps:
                    deps.append(dp)
        f.close()
        if deps != []:
            d.setVar('RDEPENDS_%s' % pkg, " ".join(deps))
        if bpn != 'glibc':
            d.setVar('RPROVIDES_%s' % pkg, pkg.replace(bpn, 'glibc'))

    do_split_packages(d, charmap_dir, file_regex=r'^(.*)\.gz$', output_pattern=bpn+'-charmap-%s', \
        description='character map for %s encoding', hook=calc_charmap_deps, extra_depends='')

    def calc_locale_deps(fn, pkg, file_regex, output_pattern, group):
        deps = []
        f = open(fn, "rb")
        c_re = re.compile(r'^copy "(.*)"')
        i_re = re.compile(r'^include "(\w+)".*')
        for l in f.readlines():
            l = l.decode("latin-1")
            m = c_re.match(l) or i_re.match(l)
            if m:
                dp = legitimize_package_name(mlprefix+bpn+'-localedata-%s' % m.group(1))
                if not dp in deps:
                    deps.append(dp)
        f.close()
        if deps != []:
            d.setVar('RDEPENDS_%s' % pkg, " ".join(deps))
        if bpn != 'glibc':
            d.setVar('RPROVIDES_%s' % pkg, pkg.replace(bpn, 'glibc'))

    do_split_packages(d, locales_dir, file_regex=r'(.*)', output_pattern=bpn+'-localedata-%s', \
        description='locale definition for %s', hook=calc_locale_deps, extra_depends='')
    d.setVar('PACKAGES', d.getVar('PACKAGES', False) + ' ' + d.getVar('MLPREFIX', False) + bpn + '-gconv')

    use_bin = d.getVar("GLIBC_INTERNAL_USE_BINARY_LOCALE")

    dot_re = re.compile(r"(.*)\.(.*)")

    # Read in supported locales and associated encodings
    supported = {}
    with open(oe.path.join(d.getVar('WORKDIR'), "SUPPORTED")) as f:
        for line in f.readlines():
            try:
                locale, charset = line.rstrip().split()
            except ValueError:
                continue
            supported[locale] = charset

    # GLIBC_GENERATE_LOCALES var specifies which locales to be generated. empty or "all" means all locales
    to_generate = d.getVar('GLIBC_GENERATE_LOCALES')
    if not to_generate or to_generate == 'all':
        to_generate = sorted(supported.keys())
    else:
        to_generate = to_generate.split()
        for locale in to_generate:
            if locale not in supported:
                if '.' in locale:
                    charset = locale.split('.')[1]
                else:
                    charset = 'UTF-8'
                    bb.warn("Unsupported locale '%s', assuming encoding '%s'" % (locale, charset))
                supported[locale] = charset

    def output_locale_source(name, pkgname, locale, encoding):
        d.setVar('RDEPENDS_%s' % pkgname, '%slocaledef %s-localedata-%s %s-charmap-%s' % \
        (mlprefix, mlprefix+bpn, legitimize_package_name(locale), mlprefix+bpn, legitimize_package_name(encoding)))
        d.setVar('pkg_postinst_ontarget_%s' % pkgname, d.getVar('locale_base_postinst_ontarget') \
        % (locale, encoding, locale))
        d.setVar('pkg_postrm_%s' % pkgname, d.getVar('locale_base_postrm') % \
        (locale, encoding, locale))

    def output_locale_binary_rdepends(name, pkgname, locale, encoding):
        dep = legitimize_package_name('%s-binary-localedata-%s' % (bpn, name))
        lcsplit = d.getVar('GLIBC_SPLIT_LC_PACKAGES')
        if lcsplit and int(lcsplit):
            d.appendVar('PACKAGES', ' ' + dep)
            d.setVar('ALLOW_EMPTY_%s' % dep, '1')
        d.setVar('RDEPENDS_%s' % pkgname, mlprefix + dep)

    commands = {}

    def output_locale_binary(name, pkgname, locale, encoding):
        treedir = oe.path.join(d.getVar("WORKDIR"), "locale-tree")
        ldlibdir = oe.path.join(treedir, d.getVar("base_libdir"))
        path = d.getVar("PATH")
        i18npath = oe.path.join(treedir, datadir, "i18n")
        gconvpath = oe.path.join(treedir, "iconvdata")
        outputpath = oe.path.join(treedir, binary_locales_dir)

        use_cross_localedef = d.getVar("LOCALE_GENERATION_WITH_CROSS-LOCALEDEF") or "0"
        if use_cross_localedef == "1":
            target_arch = d.getVar('TARGET_ARCH')
            locale_arch_options = { \
                "arc":     " --uint32-align=4 --little-endian ", \
                "arceb":   " --uint32-align=4 --big-endian ",    \
                "arm":     " --uint32-align=4 --little-endian ", \
                "armeb":   " --uint32-align=4 --big-endian ",    \
                "aarch64": " --uint32-align=4 --little-endian ",    \
                "aarch64_be": " --uint32-align=4 --big-endian ",    \
                "sh4":     " --uint32-align=4 --big-endian ",    \
                "powerpc": " --uint32-align=4 --big-endian ",    \
                "powerpc64": " --uint32-align=4 --big-endian ",  \
                "powerpc64le": " --uint32-align=4 --little-endian ",  \
                "mips":    " --uint32-align=4 --big-endian ",    \
                "mipsisa32r6":    " --uint32-align=4 --big-endian ",    \
                "mips64":  " --uint32-align=4 --big-endian ",    \
                "mipsisa64r6":  " --uint32-align=4 --big-endian ",    \
                "mipsel":  " --uint32-align=4 --little-endian ", \
                "mipsisa32r6el":  " --uint32-align=4 --little-endian ", \
                "mips64el":" --uint32-align=4 --little-endian ", \
                "mipsisa64r6el":" --uint32-align=4 --little-endian ", \
                "riscv64": " --uint32-align=4 --little-endian ", \
                "riscv32": " --uint32-align=4 --little-endian ", \
                "i586":    " --uint32-align=4 --little-endian ", \
                "i686":    " --uint32-align=4 --little-endian ", \
                "x86_64":  " --uint32-align=4 --little-endian "  }

            if target_arch in locale_arch_options:
                localedef_opts = locale_arch_options[target_arch]
            else:
                bb.error("locale_arch_options not found for target_arch=" + target_arch)
                bb.fatal("unknown arch:" + target_arch + " for locale_arch_options")

            localedef_opts += " --force --no-hard-links --no-archive --prefix=%s \
                --inputfile=%s/%s/i18n/locales/%s --charmap=%s %s/%s" \
                % (treedir, treedir, datadir, locale, encoding, outputpath, name)

            cmd = "PATH=\"%s\" I18NPATH=\"%s\" GCONV_PATH=\"%s\" cross-localedef %s" % \
                (path, i18npath, gconvpath, localedef_opts)
        else: # earlier slower qemu way 
            qemu = qemu_target_binary(d) 
            localedef_opts = "--force --no-hard-links --no-archive --prefix=%s \
                --inputfile=%s/i18n/locales/%s --charmap=%s %s" \
                % (treedir, datadir, locale, encoding, name)

            qemu_options = d.getVar('QEMU_OPTIONS')

            cmd = "PSEUDO_RELOADED=YES PATH=\"%s\" I18NPATH=\"%s\" %s -L %s \
                -E LD_LIBRARY_PATH=%s %s %s${base_bindir}/localedef %s" % \
                (path, i18npath, qemu, treedir, ldlibdir, qemu_options, treedir, localedef_opts)

        commands["%s/%s" % (outputpath, name)] = cmd

        bb.note("generating locale %s (%s)" % (locale, encoding))

    def output_locale(name, locale, encoding):
        pkgname = d.getVar('MLPREFIX', False) + 'locale-base-' + legitimize_package_name(name)
        d.setVar('ALLOW_EMPTY_%s' % pkgname, '1')
        d.setVar('PACKAGES', '%s %s' % (pkgname, d.getVar('PACKAGES')))
        rprovides = ' %svirtual-locale-%s' % (mlprefix, legitimize_package_name(name))
        m = re.match(r"(.*)_(.*)", name)
        if m:
            rprovides += ' %svirtual-locale-%s' % (mlprefix, m.group(1))
        d.setVar('RPROVIDES_%s' % pkgname, rprovides)

        if use_bin == "compile":
            output_locale_binary_rdepends(name, pkgname, locale, encoding)
            output_locale_binary(name, pkgname, locale, encoding)
        elif use_bin == "precompiled":
            output_locale_binary_rdepends(name, pkgname, locale, encoding)
        else:
            output_locale_source(name, pkgname, locale, encoding)

    if use_bin == "compile":
        bb.note("preparing tree for binary locale generation")
        bb.build.exec_func("do_prep_locale_tree", d)

    utf8_only = int(d.getVar('LOCALE_UTF8_ONLY') or 0)
    utf8_is_default = int(d.getVar('LOCALE_UTF8_IS_DEFAULT') or 0)

    encodings = {}
    for locale in to_generate:
        charset = supported[locale]
        if utf8_only and charset != 'UTF-8':
            continue

        m = dot_re.match(locale)
        if m:
            base = m.group(1)
        else:
            base = locale

        # Non-precompiled locales may be renamed so that the default
        # (non-suffixed) encoding is always UTF-8, i.e., instead of en_US and
        # en_US.UTF-8, we have en_US and en_US.ISO-8859-1. This implicitly
        # contradicts SUPPORTED.
        if use_bin == "precompiled" or not utf8_is_default:
            output_locale(locale, base, charset)
        else:
            if charset == 'UTF-8':
                output_locale(base, base, charset)
            else:
                output_locale('%s.%s' % (base, charset), base, charset)

    def metapkg_hook(file, pkg, pattern, format, basename):
        name = basename.split('/', 1)[0]
        metapkg = legitimize_package_name('%s-binary-localedata-%s' % (mlprefix+bpn, name))
        d.appendVar('RDEPENDS_%s' % metapkg, ' ' + pkg)

    if use_bin == "compile":
        makefile = oe.path.join(d.getVar("WORKDIR"), "locale-tree", "Makefile")
        with open(makefile, "w") as m:
            m.write("all: %s\n\n" % " ".join(commands.keys()))
            total = len(commands)
            for i, (maketarget, makerecipe) in enumerate(commands.items()):
                m.write(maketarget + ":\n")
                m.write("\t@echo 'Progress %d/%d'\n" % (i, total))
                m.write("\t" + makerecipe + "\n\n")
        d.setVar("EXTRA_OEMAKE", "-C %s ${PARALLEL_MAKE}" % (os.path.dirname(makefile)))
        d.setVarFlag("oe_runmake", "progress", "outof:Progress\s(\d+)/(\d+)")
        bb.note("Executing binary locale generation makefile")
        bb.build.exec_func("oe_runmake", d)
        bb.note("collecting binary locales from locale tree")
        bb.build.exec_func("do_collect_bins_from_locale_tree", d)

    if use_bin in ('compile', 'precompiled'):
        lcsplit = d.getVar('GLIBC_SPLIT_LC_PACKAGES')
        if lcsplit and int(lcsplit):
            do_split_packages(d, binary_locales_dir, file_regex=r'^(.*/LC_\w+)', \
                output_pattern=bpn+'-binary-localedata-%s', \
                description='binary locale definition for %s', recursive=True,
                hook=metapkg_hook, extra_depends='', allow_dirs=True, match_path=True)
        else:
            do_split_packages(d, binary_locales_dir, file_regex=r'(.*)', \
                output_pattern=bpn+'-binary-localedata-%s', \
                description='binary locale definition for %s', extra_depends='', allow_dirs=True)
    else:
        bb.note("generation of binary locales disabled. this may break i18n!")

}

# We want to do this indirection so that we can safely 'return'
# from the called function even though we're prepending
python populate_packages_prepend () {
    bb.build.exec_func('package_do_split_gconvs', d)
}
