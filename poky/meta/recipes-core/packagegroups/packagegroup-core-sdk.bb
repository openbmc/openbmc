#
# Copyright (C) 2007 OpenedHand Ltd.
#

SUMMARY = "Software development tools"
PR = "r9"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

#PACKAGEFUNCS =+ 'generate_sdk_pkgs'

RDEPENDS_packagegroup-core-sdk = "\
    packagegroup-core-buildessential \
    coreutils \
    ccache \
    diffutils \
    perl-module-re \
    perl-module-text-wrap \
    findutils \
    quilt \
    less \
    ldd \
    file \
    tcl"

SANITIZERS = "libasan-dev libubsan-dev"
SANITIZERS_arc = ""
SANITIZERS_microblaze = ""
SANITIZERS_mipsarch = ""
SANITIZERS_nios2 = ""
SANITIZERS_riscv64 = ""
SANITIZERS_riscv32 = ""
SANITIZERS_libc-musl = ""

RRECOMMENDS_packagegroup-core-sdk = "\
    libgomp \
    libgomp-dev \
    ${SANITIZERS}"

#python generate_sdk_pkgs () {
#    poky_pkgs = read_pkgdata('packagegroup-core', d)['PACKAGES']
#    pkgs = d.getVar('PACKAGES').split()
#    for pkg in poky_pkgs.split():
#        newpkg = pkg.replace('packagegroup-core', 'packagegroup-core-sdk')
#
#        # for each of the task packages, add a corresponding sdk task
#        pkgs.append(newpkg)
#
#        # for each sdk task, take the rdepends of the non-sdk task, and turn
#        # that into rrecommends upon the -dev versions of those, not unlike
#        # the package depchain code
#        spkgdata = read_subpkgdata(pkg, d)
#
#        rdepends = explode_deps(spkgdata.get('RDEPENDS_%s' % pkg) or '')
#        rreclist = []
#
#        for depend in rdepends:
#            split_depend = depend.split(' (')
#            name = split_depend[0].strip()
#            if packaged('%s-dev' % name, d):
#                rreclist.append('%s-dev' % name)
#            else:
#                deppkgdata = read_subpkgdata(name, d)
#                rdepends2 = explode_deps(deppkgdata.get('RDEPENDS_%s' % name) or '')
#                for depend in rdepends2:
#                    split_depend = depend.split(' (')
#                    name = split_depend[0].strip()
#                    if packaged('%s-dev' % name, d):
#                        rreclist.append('%s-dev' % name)
#
#            oldrrec = d.getVar('RRECOMMENDS_%s' % newpkg, False) or ''
#            d.setVar('RRECOMMENDS_%s' % newpkg, oldrrec + ' ' + ' '.join(rreclist))
#            # bb.note('RRECOMMENDS_%s = "%s"' % (newpkg, d.getVar('RRECOMMENDS_%s' % newpkg, False)))
#
#    # bb.note('pkgs is %s' % pkgs)
#    d.setVar('PACKAGES', ' '.join(pkgs))
#}
#
#PACKAGES_DYNAMIC += "^packagegroup-core-sdk-.*"
