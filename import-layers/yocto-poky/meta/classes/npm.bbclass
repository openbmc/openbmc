DEPENDS_prepend = "nodejs-native "
RDEPENDS_${PN}_prepend = "nodejs "
S = "${WORKDIR}/npmpkg"

NPM_INSTALLDIR = "${D}${libdir}/node_modules/${PN}"

# function maps arch names to npm arch names
def npm_oe_arch_map(target_arch, d):
    import re
    if   re.match('p(pc|owerpc)(|64)', target_arch): return 'ppc'
    elif re.match('i.86$', target_arch): return 'ia32'
    elif re.match('x86_64$', target_arch): return 'x64'
    elif re.match('arm64$', target_arch): return 'arm'
    return target_arch

NPM_ARCH ?= "${@npm_oe_arch_map(d.getVar('TARGET_ARCH', True), d)}"

npm_do_compile() {
	# Copy in any additionally fetched modules
	if [ -d ${WORKDIR}/node_modules ] ; then
		cp -a ${WORKDIR}/node_modules ${S}/
	fi
	# changing the home directory to the working directory, the .npmrc will
	# be created in this directory
	export HOME=${WORKDIR}
	npm config set dev false
	npm set cache ${WORKDIR}/npm_cache
	# clear cache before every build
	npm cache clear
	# Install pkg into ${S} without going to the registry
	npm --arch=${NPM_ARCH} --target_arch=${NPM_ARCH} --production --no-registry install
}

npm_do_install() {
	mkdir -p ${NPM_INSTALLDIR}/
	cp -a ${S}/* ${NPM_INSTALLDIR}/ --no-preserve=ownership
}

python populate_packages_prepend () {
    instdir = d.expand('${D}${libdir}/node_modules/${PN}')
    extrapackages = oe.package.npm_split_package_dirs(instdir)
    pkgnames = extrapackages.keys()
    d.prependVar('PACKAGES', '%s ' % ' '.join(pkgnames))
    for pkgname in pkgnames:
        pkgrelpath, pdata = extrapackages[pkgname]
        pkgpath = '${libdir}/node_modules/${PN}/' + pkgrelpath
        # package names can't have underscores but npm packages sometimes use them
        oe_pkg_name = pkgname.replace('_', '-')
        expanded_pkgname = d.expand(oe_pkg_name)
        d.setVar('FILES_%s' % expanded_pkgname, pkgpath)
        if pdata:
            version = pdata.get('version', None)
            if version:
                d.setVar('PKGV_%s' % expanded_pkgname, version)
            description = pdata.get('description', None)
            if description:
                d.setVar('SUMMARY_%s' % expanded_pkgname, description.replace(u"\u2018", "'").replace(u"\u2019", "'"))
    d.appendVar('RDEPENDS_%s' % d.getVar('PN', True), ' %s' % ' '.join(pkgnames).replace('_', '-'))
}

FILES_${PN} += " \
    ${libdir}/node_modules/${PN} \
"

EXPORT_FUNCTIONS do_compile do_install
